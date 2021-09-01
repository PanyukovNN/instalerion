package org.union.promoter.service.publishingstrategy;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.reel.item.StoryHashtagsItem;
import com.github.instagram4j.instagram4j.requests.media.MediaInfoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.Constants;
import org.union.common.exception.NotFoundException;
import org.union.common.exception.RequestException;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.MediaType;
import org.union.common.model.post.Post;
import org.union.common.model.post.VideoPost;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.*;
import org.union.common.service.publishingstrategy.PublishingStrategy;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.union.common.Constants.*;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramStoryPublishingStrategy implements PublishingStrategy {

    private final Logger logger = LoggerFactory.getLogger(InstagramStoryPublishingStrategy.class);

    private final PostService postService;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final DateTimeHelper dateTimeHelper;
    private final ImagePostService imagePostService;
    private final VideoPostService videoPostService;
    private final ProducingChannelService producingChannelService;

    /**
     * Publish story to producing channel
     *
     * @param request publish post request
     * @throws IGLoginException instagram4j login exception
     */
    public void publish(PublishingRequest request) throws IGLoginException, ExecutionException, InterruptedException {
        Post post = definePost(request);

        String producingChannelId = post.getProducingChannelId();
        try {
            UseContext.setInUse(producingChannelId);

            ProducingChannel producingChannel = producingChannelService.findById(post.getProducingChannelId())
                    .orElseThrow(() -> new org.union.common.exception.NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, post.getProducingChannelId())));

            // Login to instagram account
            IGClient client = instaService.getClient(producingChannel);

            MediaResponse.MediaConfigureToStoryResponse response = processPublishing(post, client);

            checkResponse(response, client, post);

            LocalDateTime now = dateTimeHelper.getCurrentDateTime();
            post.setPublishDateTime(now);
            postService.save(post);

            producingChannel.setLastPostingDateTime(now);
            producingChannelService.save(producingChannel);

            logger.info(String.format(STORY_SUCCESSFULLY_PUBLISHED_MSG, post.getId(), producingChannelId));
        } catch (Exception e) {
            post.increasePublishingErrors();
            postService.save(post);

            throw e;
        } finally {
            UseContext.release(producingChannelId);
        }
    }

    private Post definePost(PublishingRequest request) {
        Post post = postService.findMostRecentStory(request.getProducingChannelId())
                .orElse(null);

        checkPost(request, post);

        return post;
    }

    private MediaResponse.MediaConfigureToStoryResponse processPublishing(Post post, IGClient client) throws ExecutionException, InterruptedException {
        if (post.getMediaType().equals(MediaType.IMAGE.getValue())) {
            return publishImage(post.getId(), client);
        } else if (post.getMediaType().equals(MediaType.VIDEO.getValue())) {
            return publishVideo(post.getId(), client);
        } else {
            throw new RequestException(String.format(UNRECOGNIZED_MEDIA_TYPE_ERROR_MSG, post.getId()));
        }
    }

    /**
     * Check that post is published
     *
     * @param response story response from instagram
     * @param client client
     * @param post post
     * @throws ExecutionException future exception
     * @throws InterruptedException future exception
     */
    private void checkResponse(MediaResponse.MediaConfigureToStoryResponse response, IGClient client, Post post) throws ExecutionException, InterruptedException {
        if (response == null) {
            throw new RequestException(NO_PUBLISHING_ANSWER_FROM_INSTAGRAM_ERROR_MSG);
        }

        if (response.getStatusCode() != 200) {
            throw new RequestException(String.format(PUBLISHING_STATUS_ERROR, response.getStatusCode(), response.getMessage()));
        }

        if (response.getMedia() == null) {
            throw new RequestException(String.format(NO_POST_MEDIA_INFO_ERROR_MSG, post.getId()));
        }

        long mediaId = response.getMedia().getPk();

        // request information about post from instagram
        MediaInfoResponse mediaInfoResponse = client
                .sendRequest(new MediaInfoRequest(String.valueOf(mediaId)))
                .get();

        if (mediaInfoResponse == null || mediaInfoResponse.getItems() == null) {
            throw new RequestException(String.format(POST_PUBLICATION_NOT_CONFIRMED_ERROR_MSG, post.getId()));
        }

        boolean noPublishedItem = mediaInfoResponse
                .getItems().stream()
                .noneMatch(item -> item.getCode().equals(response.getMedia().getCode()));

        if (noPublishedItem) {
            throw new RequestException(String.format(POST_PUBLICATION_NOT_CONFIRMED_ERROR_MSG, post.getId()));
        }
    }

    private void checkPost(PublishingRequest request, Post post) {
        if (post == null) {
            throw new RequestException(String.format(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG, request.getProducingChannelId()));
        }

        if (post.getPublishDateTime() != null) {
            throw new RequestException(POST_ALREADY_PUBLISHED_ERROR_MSG);
        }

        if (post.getPublishingErrorCount() >= PUBLISHING_ERROR_COUNT_LIMIT) {
            throw new RequestException(POST_TOO_MANY_PUBLISHING_ERRORS);
        }
    }

    private MediaResponse.MediaConfigureToStoryResponse publishImage(String postId, IGClient client) throws ExecutionException, InterruptedException {
        ImagePost imagePost = imagePostService.findById(postId)
                .orElseThrow(() -> new org.union.common.exception.NotFoundException(String.format(IMAGE_POST_NOT_FOUND_ERROR_MSG, postId)));

        File imageFile = cloudService.getImageFileByCode(imagePost.getCode());

        if (!imageFile.exists()) {
            throw new RequestException(String.format(FILE_NOT_FOUND_ERROR_MSG, imagePost.getCode()));
        }

        return client
                .actions()
                .story()
                .uploadPhoto(imageFile)
                .get();
    }

    private MediaResponse.MediaConfigureToStoryResponse publishVideo(String postId, IGClient client) throws ExecutionException, InterruptedException {
        VideoPost videoPost = videoPostService.findById(postId)
                .orElseThrow(() -> new NotFoundException(String.format(VIDEO_POST_NOT_FOUND_ERROR_MSG, postId)));

        if (videoPost.getDuration() > 60) {
            throw new RequestException(TOO_LONG_VIDEO_ERROR_MSG);
        }

        File videoFile = cloudService.getVideoFileByCode(videoPost.getCode());
        File coverFile = cloudService.getImageFileByCode(videoPost.getCode());

        if (!videoFile.exists() || !coverFile.exists()) {
            throw new RequestException(String.format(FILE_NOT_FOUND_ERROR_MSG, videoPost.getCode()));
        }

//        StoryHashtagsItem.builder()

        return client
                .actions()
                .story()
                .uploadVideo(videoFile, coverFile)
                .get();
    }
}
