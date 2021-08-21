package org.union.promoter.service.requestprocessor;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.PostMediaType;
import org.union.common.model.post.VideoPost;
import org.union.promoter.service.RequestHelper;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.service.*;

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
    private final CloudService cloudService;
    private final InstaService instaService;
    private final RequestHelper requestHelper;
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

        requestHelper.checkOftenRequests(producingChannel.getLastLoadingDateTime(), topicName);

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

        producingChannel.setLastLoadingDateTime(LocalDateTime.now());
        consumingChannelService.saveAll(consumingChannels);
        producingChannelService.save(producingChannel);
    }

    private void processConsumeChannel(ProducingChannel producingChannel, IGClient client, ConsumingChannel consumingChannel) throws InterruptedException, ExecutionException, IOException {
        List<TimelineMedia> timelineItems = loadConsumingChannelPosts(producingChannel, client, consumingChannel);

        processVideoPosts(producingChannel, consumingChannel, timelineItems);
        processImagePosts(producingChannel, consumingChannel, timelineItems);
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

            // filter posts by time and existing in database
            List<TimelineMedia> filteredResponseItems = responseItems.stream()
                    .filter(item -> getDateTime(item.getTaken_at() * 1000)
                            .isAfter(LocalDateTime.now().minusDays(postDays)))
                    .filter(videoPost -> !postService.exists(videoPost.getCode(), producingChannel.getId()))
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

    private void processVideoPosts(ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<VideoPost> videoPosts = timelineItems.stream()
                .filter(TimelineVideoMedia.class::isInstance)
                .map(TimelineVideoMedia.class::cast)
                .map(videoItem -> getVideoPost(producingChannel, videoItem))
                .filter(videoPost -> videoPost.getCode() != null)
                .map(postService::save)
                .collect(Collectors.toList());

        cloudService.saveVideoPosts(videoPosts);
        consumingChannel.setVideoPosts(videoPosts);

        logger.info(String.format(SAVED_VIDEOS_FROM_CHANNEL_MSG, videoPosts.size(), consumingChannel.getName()));
    }

    private void processImagePosts(ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<ImagePost> imagePosts = timelineItems.stream()
                .filter(TimelineImageMedia.class::isInstance)
                .map(TimelineImageMedia.class::cast)
                .map(imageItem -> getImagePost(producingChannel, imageItem))
                .filter(imagePost -> imagePost.getCode() != null)
                .map(postService::save)
                .collect(Collectors.toList());

        cloudService.saveImagePosts(imagePosts);
        consumingChannel.setImagePosts(imagePosts);

        logger.info(String.format(SAVED_IMAGES_FROM_CHANNEL_MSG, imagePosts.size(), consumingChannel.getName()));
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

            videoPost.setPostMediaType(PostMediaType.VIDEO);
            videoPost.setUrl(video.getVideo_versions().get(0).getUrl());
            videoPost.setCoverUrl(video.getImage_versions2().getCandidates().get(0).getUrl());
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

            imagePost.setPostMediaType(PostMediaType.IMAGE);
            imagePost.setUrl(image.getImage_versions2().getCandidates().get(0).getUrl());
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
