package org.union.promoter.service.requestprocessor;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.kilianB.matcher.persistent.ConsecutiveMatcher;
import io.micrometer.core.instrument.util.StringUtils;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.MediaType;
import org.union.common.model.post.Post;
import org.union.common.model.post.VideoPost;
import org.union.common.service.*;
import org.union.promoter.service.RequestHelper;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.union.common.Constants.*;

/**
 * Request processor for posts loading
 */
@Service
@RequiredArgsConstructor
public class LoaderRequestProcessor {

    private final Logger logger = LoggerFactory.getLogger(LoaderRequestProcessor.class);

    @Value("${post.days}")
    private int postDays;
    @Value("${post.limit}")
    private int postLimit;
    @Value("${kafka.loader.topic}")
    private String topicName;

    private final PostService postService;
    private final ImageMatcher imageMatcher;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final RequestHelper requestHelper;
    private final DateTimeHelper dateTimeHelper;
    private final ImagePostService imagePostService;
    private final VideoPostService videoPostService;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param producingChannelId id of producing channel
     * @throws IOException exception
     * @throws NotFoundException exception
     */
    @Transactional
    public void load(String producingChannelId) throws IOException, NotFoundException {
        ProducingChannel producingChannel = producingChannelService.findById(producingChannelId)
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, producingChannelId)));

//        requestHelper.checkOftenRequests(producingChannel.getLastLoadingDateTime(), topicName);

        // Login to access instagram account
        IGClient client = instaService.getClient(producingChannel);

        List<ConsumingChannel> consumingChannels = producingChannel.getConsumingChannels();

        // Load posts from consume channels
        for (ConsumingChannel consumingChannel : consumingChannels) {
            try {
                processConsumeChannel(producingChannel, client, consumingChannel);
            } catch (Exception e) {
                logger.error(String.format(ERROR_WHILE_CONSUME_CHANNEL_LOADING, consumingChannel.getName()), e);
            }
        }

        producingChannel.setLastLoadingDateTime(dateTimeHelper.getCurrentDateTime());
        consumingChannelService.saveAll(consumingChannels);
        producingChannelService.save(producingChannel);
    }

    private void processConsumeChannel(ProducingChannel producingChannel, IGClient client, ConsumingChannel consumingChannel) throws InterruptedException, ExecutionException, IOException {
        List<TimelineMedia> timelineItems = loadConsumingChannelPosts(producingChannel, client, consumingChannel);

        List<Post> publishedPosts = postService.findPublishedInProducingChannel(producingChannel);
        ConsecutiveMatcher matcher = imageMatcher.createMatcher(publishedPosts);

        processVideoPosts(matcher, producingChannel, consumingChannel, timelineItems);
        processImagePosts(matcher, producingChannel, consumingChannel, timelineItems);
    }

    private List<TimelineMedia> loadConsumingChannelPosts(ProducingChannel producingChannel, IGClient client, ConsumingChannel consumingChannel) throws InterruptedException, ExecutionException {
        String consumeChannelName = consumingChannel.getName();

        UserAction userAction = client.actions().users().findByUsername(consumeChannelName).get();

        List<TimelineMedia> timelineItems = new ArrayList<>();

        // id of last loaded post for pagination
        String maxId = null;
        int leftToLoadPosts = postLimit;
        boolean continueLoading = true;

        // while has posts to load or loading is allowed
        while(leftToLoadPosts > 0 && continueLoading) {
            // Loads first 12 posts
            FeedUserResponse feedUserResponse = client
                    .sendRequest(new FeedUserRequest(userAction.getUser().getPk(), maxId))
                    .get();

            // Set max_id for next pagination request
            maxId = feedUserResponse.getNext_max_id();
            continueLoading = feedUserResponse.isMore_available();

            List<TimelineMedia> responseItems = feedUserResponse.getItems();

            // filter posts by time/database existence/advertising
            List<TimelineMedia> filteredResponseItems = responseItems.stream()
                    .filter(item -> {
                        // filter posts taken more that 2 hours from now and earlier than current time minus postDays
                        LocalDateTime takenAt = getDateTime(item.getTaken_at() * 1000);
                        LocalDateTime now = dateTimeHelper.getCurrentDateTime();

                        return takenAt.isBefore(now.minusDays(1))
                                && takenAt.isAfter(now.minusDays(postDays + 1));
                    })
                    .filter(post -> !postService.exists(post.getCode(), producingChannel.getId()))
                    .filter(post -> post.getCode() != null)
                    .filter(post -> !isAdvertising(post, consumeChannelName))
                    .collect(Collectors.toList());

            timelineItems.addAll(filteredResponseItems);

            // if even one post is filtered - stop loading
            if (responseItems.size() > filteredResponseItems.size()) {
                break;
            }

            // decrease number of posts, which needs to be loaded
            leftToLoadPosts -= responseItems.size();
        }

        return timelineItems;
    }

    /**
     * Check does media contain advertising (usertags or outer links)
     *
     * @param media media
     * @param consumingChannelName name of consuming channel
     * @return is contain ad
     */
    private boolean isAdvertising(TimelineMedia media, String consumingChannelName) {
        // check usertags
        if (media.getUsertags() != null
                && !CollectionUtils.isEmpty(media.getUsertags().getIn())) {
            return true;
        }

        if (media.getCaption() != null) {
            String captionText = media.getCaption().getText();

            if (StringUtils.isEmpty(captionText)) {
                return false;
            }

            // remove links on consuming chanel
            captionText = captionText.replace("@" + consumingChannelName, "");

            // check outer links
            return captionText.contains("@")
                    || captionText.contains("http://")
                    || captionText.contains("https://");
        }

        return false;
    }

    private void processVideoPosts(ConsecutiveMatcher matcher, ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<VideoPost> videoPosts = timelineItems.stream()
                .filter(TimelineVideoMedia.class::isInstance)
                .map(TimelineVideoMedia.class::cast)
                .filter(videoPost -> videoPost.getVideo_duration() <= 60)
                .filter(videoPost -> videoPost.getMedia_type().equals(MediaType.VIDEO.getValue()))
                .map(videoItem -> getVideoPost(producingChannel, videoItem))
                .filter(videoPost -> imageMatcher.isUniqueImage(matcher, videoPost.getImageUrl(), videoPost.getCode()))
                .map(videoPostService::save)
                .collect(Collectors.toList());

        cloudService.saveVideoPosts(videoPosts);
        addVideoPostsToConsumingChannel(consumingChannel, videoPosts);

        logger.info(String.format(SAVED_VIDEOS_FROM_CHANNEL_MSG, videoPosts.size(), consumingChannel.getName()));
    }

    private void processImagePosts(ConsecutiveMatcher matcher, ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<ImagePost> imagePosts = timelineItems.stream()
                .filter(TimelineImageMedia.class::isInstance)
                .map(TimelineImageMedia.class::cast)
                .filter(imageItem -> imageItem.getMedia_type().equals(MediaType.IMAGE.getValue()))
                .map(imageItem -> getImagePost(producingChannel, imageItem))
                .filter(imagePost -> imageMatcher.isUniqueImage(matcher, imagePost.getImageUrl(), imagePost.getCode()))
                .map(imagePostService::save)
                .collect(Collectors.toList());

        cloudService.saveImagePosts(imagePosts);
        addImagePostsToConsumingChannel(consumingChannel, imagePosts);

        logger.info(String.format(SAVED_IMAGES_FROM_CHANNEL_MSG, imagePosts.size(), consumingChannel.getName()));
    }

    private void addVideoPostsToConsumingChannel(ConsumingChannel consumingChannel, List<VideoPost> postsToAdd) {
        List<Post> posts = consumingChannel.getVideoPosts();

        if (posts == null) {
            consumingChannel.setVideoPosts(new ArrayList<>());
        }

        consumingChannel.getVideoPosts().addAll(postsToAdd);
    }

    private void addImagePostsToConsumingChannel(ConsumingChannel consumingChannel, List<ImagePost> postsToAdd) {
        List<Post> posts = consumingChannel.getImagePosts();

        if (posts == null) {
            consumingChannel.setImagePosts(new ArrayList<>());
        }

        consumingChannel.getImagePosts().addAll(postsToAdd);
    }

    /**
     * Creates video post from TimelineVideoMedia
     *
     * @param producingChannel producing channel
     * @param video timeline video media item
     * @return VideoPost
     */
    private VideoPost getVideoPost(ProducingChannel producingChannel, TimelineVideoMedia video) {
        VideoPost videoPost = new VideoPost();

        try {
            videoPost.setCode(video.getCode());

            if (video.getCaption() != null) {
                videoPost.setDescription(video.getCaption().getText());
            } else {
                videoPost.setDescription("");
            }

            videoPost.setRating(postService.calculateRating(video, video.getView_count()));

            videoPost.setMediaType(video.getMedia_type());
            videoPost.setDuration(video.getVideo_duration());
            videoPost.setVideoUrl(video.getVideo_versions().get(0).getUrl());
            videoPost.setImageUrl(video.getImage_versions2().getCandidates().get(0).getUrl());
            videoPost.setProducingChannelId(producingChannel.getId());
        } catch (Exception e) {
            logger.error(String.format(TRANSFORM_TO_VIDEO_POST_ERROR_MSG, e.getMessage()), e);
        }

        return videoPost;
    }

    /**
     * Creates image post from TimelineImageMedia
     *
     * @param producingChannel producing channel
     * @param image timeline image media item
     * @return ImagePost
     */
    private ImagePost getImagePost(ProducingChannel producingChannel, TimelineImageMedia image) {
        ImagePost imagePost = new ImagePost();

        try {
            imagePost.setCode(image.getCode());

            if (image.getCaption() != null) {
                imagePost.setDescription(image.getCaption().getText());
            } else {
                imagePost.setDescription("");
            }

            imagePost.setRating(postService.calculateRating(image, image.getView_count()));

            imagePost.setMediaType(image.getMedia_type());
            imagePost.setImageUrl(image.getImage_versions2().getCandidates().get(0).getUrl());
            imagePost.setProducingChannelId(producingChannel.getId());
        } catch (Exception e) {
            logger.error(String.format(TRANSFORM_TO_IMAGE_POST_ERROR_MSG, e.getMessage()), e);
        }

        return imagePost;
    }

    /**
     * Get localDateTime from post taken_at time
     *
     * @param mills milli seconds
     * @return localDateTime
     */
    private LocalDateTime getDateTime(long mills) {
        return Instant.ofEpochMilli(mills)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
