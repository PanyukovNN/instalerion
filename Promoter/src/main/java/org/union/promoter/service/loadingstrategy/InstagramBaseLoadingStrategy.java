package org.union.promoter.service.loadingstrategy;

import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.kilianB.matcher.persistent.ConsecutiveMatcher;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.exception.RequestException;
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
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.union.common.Constants.*;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramBaseLoadingStrategy implements LoadingStrategy {

    private final Logger logger = LoggerFactory.getLogger(InstagramBaseLoadingStrategy.class);

    private final PostService postService;
    private final ImageMatcher imageMatcher;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final LoaderService loaderService;
    private final DateTimeHelper dateTimeHelper;
    private final ProducingChannelService producingChannelService;
    private final ConsumingChannelService consumingChannelService;

    @Override
    public void load(LoadingRequest request) throws IGLoginException, NotFoundException {
        ProducingChannel producingChannel = producingChannelService.findById(request.getProducingChannelId())
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, request.getProducingChannelId())));

        InstaClient client = instaService.getClient(producingChannel);

        List<ConsumingChannel> consumingChannels = producingChannel.getConsumingChannels();

        consumingChannels.forEach(
                consumingChannel -> processConsumeChannel(producingChannel, client, consumingChannel, request.getLoadingVolume())
        );

        producingChannel.setLastLoadingDateTime(dateTimeHelper.getCurrentDateTime());
        consumingChannelService.saveAll(consumingChannels);
        producingChannelService.save(producingChannel);

        logger.info(POSTS_LOADING_SUCCESS_MSG);
    }

    private void processConsumeChannel(ProducingChannel producingChannel,
                                       InstaClient client,
                                       ConsumingChannel consumingChannel,
                                       LoadingVolume loadingVolume) {
        try {
            List<TimelineMedia> timelineItems = loaderService.loadConsumingChannelPosts(producingChannel, client, consumingChannel, loadingVolume);

            //TODO check for stories
            List<Post> publishedPosts = postService.findPublished(producingChannel, PublicationType.INSTAGRAM_POST);
            ConsecutiveMatcher matcher = imageMatcher.createMatcher(publishedPosts);

            processVideoPosts(matcher, producingChannel, consumingChannel, timelineItems);
            processImagePosts(matcher, producingChannel, consumingChannel, timelineItems);
        } catch (Exception e) {
            logger.error(String.format(ERROR_WHILE_CONSUME_CHANNEL_LOADING, consumingChannel.getName()), e);
        }
    }

    private void processVideoPosts(ConsecutiveMatcher matcher, ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<Post> videoPosts = timelineItems.stream()
                .filter(TimelineVideoMedia.class::isInstance)
                .map(TimelineVideoMedia.class::cast)
                .filter(videoPost -> videoPost.getVideo_duration() <= 60)
                .filter(videoPost -> videoPost.getMedia_type().equals(MediaType.VIDEO.getValue()))
                .map(videoItem -> getVideoPost(producingChannel, videoItem))
                .filter(videoPost -> imageMatcher.isUniqueImage(matcher, videoPost.getMediaInfo().getImageUrl(), videoPost.getCode()))
                .map(postService::save)
                .collect(Collectors.toList());

        cloudService.savePostsMedia(videoPosts);
        addPostsToConsumingChannel(consumingChannel, videoPosts);

        logger.info(String.format(SAVED_VIDEOS_FROM_CHANNEL_MSG, videoPosts.size(), consumingChannel.getName()));
    }

    private void processImagePosts(ConsecutiveMatcher matcher, ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<Post> imagePosts = timelineItems.stream()
                .filter(TimelineImageMedia.class::isInstance)
                .map(TimelineImageMedia.class::cast)
                .filter(imageItem -> imageItem.getMedia_type().equals(MediaType.IMAGE.getValue()))
                .map(imageItem -> getImagePost(producingChannel, imageItem))
                .filter(imagePost -> imageMatcher.isUniqueImage(matcher, imagePost.getMediaInfo().getImageUrl(), imagePost.getCode()))
                .map(postService::save)
                .collect(Collectors.toList());

        cloudService.savePostsMedia(imagePosts);
        addPostsToConsumingChannel(consumingChannel, imagePosts);

        logger.info(String.format(SAVED_IMAGES_FROM_CHANNEL_MSG, imagePosts.size(), consumingChannel.getName()));
    }

    private void addPostsToConsumingChannel(ConsumingChannel consumingChannel, List<Post> postsToAdd) {
        List<Post> posts = consumingChannel.getPosts();

        if (posts == null) {
            consumingChannel.setPosts(new ArrayList<>());
        }

        consumingChannel.getPosts().addAll(postsToAdd);
    }

    /**
     * Creates video post from TimelineVideoMedia
     *
     * @param producingChannel producing channel
     * @param video timeline video media item
     * @return VideoPost
     */
    private Post getVideoPost(ProducingChannel producingChannel, TimelineVideoMedia video) {
        Post videoPost = new Post();

        try {
            fillPostInfo(videoPost, producingChannel, video, video.getView_count());
            MediaInfo videoMediaInfo = videoPost.getMediaInfo();
            videoMediaInfo.setDuration(video.getVideo_duration());
            videoMediaInfo.setVideoUrl(video.getVideo_versions().get(0).getUrl());
            videoMediaInfo.setImageUrl(video.getImage_versions2().getCandidates().get(0).getUrl());
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
    private Post getImagePost(ProducingChannel producingChannel, TimelineImageMedia image) {
        Post imagePost = new Post();

        try {
            fillPostInfo(imagePost, producingChannel, image, image.getView_count());
            MediaInfo imageMediaInfo = imagePost.getMediaInfo();
            imageMediaInfo.setImageUrl(image.getImage_versions2().getCandidates().get(0).getUrl());
        } catch (Exception e) {
            logger.error(String.format(TRANSFORM_TO_IMAGE_POST_ERROR_MSG, e.getMessage()), e);
        }

        return imagePost;
    }

    public void fillPostInfo(Post post, ProducingChannel producingChannel, TimelineMedia media, int viewCount) {
        post.setCode(media.getCode());
        post.setDescription(media.getCaption() != null ? media.getCaption().getText() : EMPTY);
        post.setTakenAt(instaService.getTimelineMediaDateTime(media));
        post.setRating(postService.calculateRating(media, viewCount, post.getTakenAt()));
        post.setProducingChannelId(producingChannel.getId());

        MediaInfo mediaInfo = new MediaInfo();
        mediaInfo.setMediaId(media.getPk());
        mediaInfo.setType(defineMediaType(media));
        post.setMediaInfo(mediaInfo);
    }

    @NotNull
    private MediaType defineMediaType(TimelineMedia media) {
        for (MediaType mediaType : MediaType.values()) {
            if (media.getMedia_type().equals(mediaType.getValue())) {
                return mediaType;
            }
        }

        throw new RequestException(UNABLE_TO_DEFINE_MEDIA_TYPE_MSG);
    }
}
