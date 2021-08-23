package org.union.promoter.service.requestprocessor;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.requests.media.MediaInfoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.union.common.Constants;
import org.union.common.exception.NotFoundException;
import org.union.common.exception.RequestException;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.MediaType;
import org.union.common.model.post.Post;
import org.union.common.model.post.VideoPost;
import org.union.common.model.request.PublishPostRequest;
import org.union.common.service.*;
import org.union.promoter.service.RequestHelper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.union.common.Constants.*;

/**
 * Request processor for post publishing
 */
@Service
@RequiredArgsConstructor
public class PublisherRequestProcessor {

    @Value("${kafka.publisher.topic}")
    private String topicName;

    private final PostService postService;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final RequestHelper requestHelper;
    private final DateTimeHelper dateTimeHelper;
    private final ImagePostService imagePostService;
    private final VideoPostService videoPostService;
    private final ProducingChannelService producingChannelService;

    /**
     * Publish post on producing channel
     *
     * @param request publish post request
     * @throws IGLoginException instagram4j login exception
     */
    public void publish(PublishPostRequest request) throws IGLoginException, ExecutionException, InterruptedException {
        Post post = postService.findById(request.getPostId()).orElse(null);

        checkPost(request, post);

        try {
            ProducingChannel producingChannel = producingChannelService.findById(post.getProducingChannelId())
                    .orElseThrow(() -> new NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, post.getProducingChannelId())));

            requestHelper.checkOftenRequests(producingChannel.getLastPostingDateTime(), topicName);

            // Login to instagram account
            IGClient client = instaService.getClient(producingChannel);

            MediaResponse.MediaConfigureTimelineResponse response = processInstagramResponse(post, client);

            checkResponse(response, client, post);

            LocalDateTime now = dateTimeHelper.getCurrentDateTime();
            post.setPublishDateTime(now);
            postService.save(post);

            producingChannel.setLastPostingDateTime(now);
            producingChannelService.save(producingChannel);
        } catch (Exception e) {
            post.increasePublishingErrors();
            postService.save(post);

            throw e;
        }
    }

    private MediaResponse.MediaConfigureTimelineResponse processInstagramResponse(Post post, IGClient client) throws ExecutionException, InterruptedException {
        MediaResponse.MediaConfigureTimelineResponse response;
        if (post.getMediaType().equals(MediaType.IMAGE.getValue())) {
            response = publishImage(post.getId(), client);
        } else if (post.getMediaType().equals(MediaType.VIDEO.getValue())) {
            response = publishVideo(post.getId(), client);
        } else {
            throw new RequestException(String.format(UNRECOGNIZED_MEDIA_TYPE_ERROR_MSG, post.getId()));
        }

        return response;
    }

    /**
     * Check that post is published
     *
     * @param response media response from instagram
     * @param client client
     * @param post post
     * @throws ExecutionException future exception
     * @throws InterruptedException future exception
     */
    private void checkResponse(MediaResponse.MediaConfigureTimelineResponse response, IGClient client, Post post) throws ExecutionException, InterruptedException {
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

    private void checkPost(PublishPostRequest request, Post post) {
        if (post == null) {
            throw new RequestException(String.format(POST_NOT_FOUND_ERROR_MSG, request.getPostId()));
        }

        if (post.getPublishDateTime() != null) {
            throw new RequestException(POST_ALREADY_PUBLISHED_ERROR_MSG);
        }

        if (post.getPublishingErrorCount() >= PUBLISHING_ERROR_COUNT_LIMIT) {
            throw new RequestException(POST_TOO_MANY_PUBLISHING_ERRORS);
        }
    }

    private MediaResponse.MediaConfigureTimelineResponse publishImage(String postId, IGClient client) throws ExecutionException, InterruptedException {
        ImagePost imagePost = imagePostService.findById(postId)
                .orElseThrow(() -> new NotFoundException(String.format(IMAGE_POST_NOT_FOUND_ERROR_MSG, postId)));

        File imageFile = cloudService.getImageFileByCode(imagePost.getCode());

        if (!imageFile.exists()) {
            throw new RequestException(String.format(FILE_NOT_FOUND_ERROR_MSG, imagePost.getCode()));
        }

        return client.actions().timeline()
                .uploadPhoto(imageFile, getCaption(imagePost)).get();
    }

    private MediaResponse.MediaConfigureTimelineResponse publishVideo(String postId, IGClient client) throws ExecutionException, InterruptedException {
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

        return client
                .actions()
                .timeline()
                .uploadVideo(videoFile, coverFile, getCaption(videoPost))
                .get();
    }

    private String getCaption(Post post) {
        return EMPTY;
        //post.getDescription();
//        StringUtils.hasText(post.getDescription())
//                ? post.getDescription() + "\n\n" + String.format(SOURCE_STRING_TEMPLATE, post.getCode())
//                : String.format(SOURCE_STRING_TEMPLATE, post.getCode());
    }
}
