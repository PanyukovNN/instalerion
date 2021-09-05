package org.union.promoter.service.loadingstrategy;

import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.kilianB.matcher.persistent.ConsecutiveMatcher;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.*;
import org.union.common.model.request.LoadingRequest;
import org.union.common.service.*;
import org.union.common.service.loadingstrategy.LoadingVolume;
import org.union.promoter.service.LoaderService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.union.common.Constants.*;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramPostLoadingStrategy implements LoadingStrategy {

    private final Logger logger = LoggerFactory.getLogger(InstagramPostLoadingStrategy.class);

    private final PostService postService;
    private final ImageMatcher imageMatcher;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final LoaderService loaderService;
    private final DateTimeHelper dateTimeHelper;
    private final ImagePostService imagePostService;
    private final VideoPostService videoPostService;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    @Override
    public void load(LoadingRequest request) throws IGLoginException, NotFoundException, ExecutionException, InterruptedException {
        ProducingChannel producingChannel = producingChannelService.findById(request.getProducingChannelId())
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, request.getProducingChannelId())));

        // Login to access instagram account
        InstaClient client = instaService.getClient(producingChannel);

        List<ConsumingChannel> consumingChannels = producingChannel.getConsumingChannels();

        consumingChannels.forEach(
                consumingChannel -> processConsumeChannel(producingChannel, client, consumingChannel, request.getLoadingVolume())
        );

        producingChannel.setLastLoadingDateTime(dateTimeHelper.getCurrentDateTime());
        consumingChannelService.saveAll(consumingChannels);
        producingChannelService.save(producingChannel);

//        updatePostsRating(producingChannel, client);
    }

    //TODO протестировать
    private void updatePostsRating(ProducingChannel producingChannel, InstaClient client) throws ExecutionException, InterruptedException {
        List<Post> lastUnratedPost = postService.findLastUnratedPost(producingChannel.getId());

        for (Post post : lastUnratedPost) {
            MediaInfoResponse infoResponse = client.requestMediaInfo(post.getMediaId());

            if (infoResponse == null
                    || CollectionUtils.isEmpty(infoResponse.getItems())
                    || infoResponse.getItems().size() > 1) {
                post.setRating(new PostRating(0d, true));
            } else {
                TimelineMedia media = infoResponse.getItems().get(0);

                int viewCount = 0;
                if (media.getMedia_type().equals(MediaType.VIDEO.getValue())) {
                    viewCount = ((TimelineVideoMedia) media).getView_count();
                } else if (media.getMedia_type().equals(MediaType.IMAGE.getValue())) {
                    viewCount = ((TimelineImageMedia) media).getView_count();
                }
                post.setRating(postService.calculateRating(media, viewCount, post.getTakenAt()));
            }
        }

        postService.saveAll(lastUnratedPost);
    }

    private void processConsumeChannel(ProducingChannel producingChannel,
                                       InstaClient client,
                                       ConsumingChannel consumingChannel,
                                       LoadingVolume loadingVolume) {
        try {
            List<TimelineMedia> timelineItems = loaderService.loadConsumingChannelPosts(producingChannel, client, consumingChannel, loadingVolume);

            List<Post> publishedPosts = postService.findPublishedInProducingChannel(producingChannel);
            ConsecutiveMatcher matcher = imageMatcher.createMatcher(publishedPosts);

            processVideoPosts(matcher, producingChannel, consumingChannel, timelineItems);
            processImagePosts(matcher, producingChannel, consumingChannel, timelineItems);
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_CONSUME_CHANNEL_LOADING, consumingChannel.getName()), e);
        }
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
            fillPostInfo(videoPost, producingChannel, video, video.getView_count());
            videoPost.setDuration(video.getVideo_duration());
            videoPost.setVideoUrl(video.getVideo_versions().get(0).getUrl());
            videoPost.setImageUrl(video.getImage_versions2().getCandidates().get(0).getUrl());
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
            fillPostInfo(imagePost, producingChannel, image, image.getView_count());
            imagePost.setImageUrl(image.getImage_versions2().getCandidates().get(0).getUrl());
        } catch (Exception e) {
            logger.error(String.format(TRANSFORM_TO_IMAGE_POST_ERROR_MSG, e.getMessage()), e);
        }

        return imagePost;
    }

    public void fillPostInfo(Post post, ProducingChannel producingChannel, TimelineMedia media, int viewCount) {
        post.setCode(media.getCode());
        post.setDescription(media.getCaption() != null ? media.getCaption().getText() : EMPTY);
        post.setTakenAt(instaService.getTimelineMediaDateTime(media));
        post.setMediaId(media.getPk());
        post.setRating(postService.calculateRating(media, viewCount, post.getTakenAt()));
        post.setMediaType(media.getMedia_type());
        post.setProducingChannelId(producingChannel.getId());
    }
}
