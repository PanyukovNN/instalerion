package org.union.promoter.service.requestprocessor;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import org.union.common.Constants;
import org.union.common.exception.NotFoundException;
import org.union.common.exception.RequestException;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.Post;
import org.union.common.model.post.PostMediaType;
import org.union.common.model.post.VideoPost;
import org.union.common.model.request.PublishPostRequest;
import org.union.common.repository.ProducingChannelRepository;
import org.union.common.service.CloudService;
import org.union.common.service.InstaService;
import org.union.common.service.PostService;
import org.union.promoter.service.RequestHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static org.union.common.Constants.*;

/**
 * Request processor for post publishing
 */
@Service
@RequiredArgsConstructor
public class PublisherRequestProcessor {

    @Value("${publishing.errors.limit}")
    private int publishingErrorsLimit;
    @Value("${kafka.publisher.topic}")
    private String topicName;

    private final PostService postService;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final RequestHelper requestHelper;
    private final ProducingChannelRepository producingChannelRepository;

    /**
     * Publish post on producing channel
     *
     * @param request publish post request
     * @throws IGLoginException instagram4j login exception
     */
    public void publish(PublishPostRequest request) throws IGLoginException, ExecutionException, InterruptedException {
        Post post = postService.findById(request.getPostId()).orElse(null);

        checkPost(request, post);

        ProducingChannel producingChannel = producingChannelRepository.findById(post.getProducingChannelId())
                .orElseThrow(() -> new NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, post.getProducingChannelId())));

        requestHelper.checkOftenRequests(producingChannel.getLastPostingDateTime(), topicName);

        // Login to instagram account
        IGClient client = instaService.getClient(producingChannel);

        MediaResponse.MediaConfigureTimelineResponse response = null;
        if (post.getPostMediaType() == PostMediaType.IMAGE) {
            response = publishImage((ImagePost) post, client);
        } else if (post.getPostMediaType() == PostMediaType.VIDEO) {
            response = publishVideo((VideoPost) post, client);
        }

        checkResponse(post, response);

        post.setPublishDateTime(LocalDateTime.now());
        postService.save(post);
    }

    private void checkResponse(Post post, MediaResponse.MediaConfigureTimelineResponse response) {
        if (response == null) {
            post.increasePublishingErrors();
            postService.save(post);

            throw new RequestException(NO_PUBLISHING_ANSWER_FROM_INSTAGRAM_ERROR_MSG);
        }

        if (response.getStatusCode() != 200) {
            post.increasePublishingErrors();
            postService.save(post);

            throw new RequestException(String.format(PUBLISHING_ERROR, response.getMessage()));
        }
    }

    private void checkPost(PublishPostRequest request, Post post) {
        if (post == null) {
            throw new RequestException(String.format(POST_NOT_FOUND_ERROR_MSG, request.getPostId()));
        }

        if (post.getPublishDateTime() != null) {
            throw new RequestException(POST_ALREADY_PUBLISHED_ERROR_MSG);
        }

        if (post.getPublishingErrorCount() > publishingErrorsLimit) {
            throw new RequestException(POST_TOO_MANY_PUBLISHING_ERRORS);
        }
    }

    private MediaResponse.MediaConfigureTimelineResponse publishImage(ImagePost imagePost, IGClient client) throws ExecutionException, InterruptedException {
        File imageFile = cloudService.getImageFileByCode(imagePost.getCode());

        if (!imageFile.exists()) {
            throw new RequestException(String.format(FILE_NOT_FOUND_ERROR_MSG, imagePost.getCode()));
        }

        return client.actions().timeline().uploadPhoto(imageFile, getCaption(imagePost)).get();
    }

    private MediaResponse.MediaConfigureTimelineResponse publishVideo(VideoPost videoPost, IGClient client) throws ExecutionException, InterruptedException {
        File videoFile = cloudService.getVideoFileByCode(videoPost.getCode());
        File coverFile = cloudService.getImageFileByCode(videoPost.getCode());

        if (!videoFile.exists() || !coverFile.exists()) {
            throw new RequestException(String.format(FILE_NOT_FOUND_ERROR_MSG, videoPost.getCode()));
        }

        return client.actions().timeline().uploadVideo(videoFile, coverFile, getCaption(videoPost)).get();
    }

    private String getCaption(Post post) {
        return post.getDescription();
//        StringUtils.hasText(post.getDescription())
//                ? post.getDescription() + "\n\n" + String.format(SOURCE_STRING_TEMPLATE, post.getCode())
//                : String.format(SOURCE_STRING_TEMPLATE, post.getCode());
    }
}
