package org.union.promoter.service.publishingstrategy;

import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.union.common.Constants;
import org.union.common.exception.NotFoundException;
import org.union.common.exception.RequestException;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.MediaType;
import org.union.common.model.post.Post;
import org.union.common.model.post.VideoPost;
import org.union.common.service.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.union.common.Constants.*;

/**
 * Abstract strategy class for an instagram posting
 */
@RequiredArgsConstructor
public abstract class InstagramBasePublishingStrategy implements PublishingStrategy {

    protected static final Logger logger = LoggerFactory.getLogger(InstagramBasePublishingStrategy.class);

    private final PostService postService;
    private final CloudService cloudService;
    private final InstaService instaService;
    private final DateTimeHelper dateTimeHelper;
    private final ImagePostService imagePostService;
    private final VideoPostService videoPostService;
    private final ProducingChannelService producingChannelService;

    @Override
    public void publish(Post post) throws Exception {
        String producingChannelId = post.getProducingChannelId();

        logStartPublishing(post.getId(), producingChannelId);
        try {
            ProducingChannel producingChannel = producingChannelService.findById(post.getProducingChannelId())
                    .orElseThrow(() -> new org.union.common.exception.NotFoundException(String.format(Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, post.getProducingChannelId())));

            // Login to instagram account
            InstaClient client = instaService.getClient(producingChannel);

            processPublishing(producingChannel.getHashtags(), post, client);

            LocalDateTime now = dateTimeHelper.getCurrentDateTime();
            setPostPublicationDateTime(post, now);
            postService.save(post);

            setLastPublicationDateTime(producingChannel, now);
            producingChannelService.save(producingChannel);

            logSuccessPublishing(post.getId(), producingChannelId);
        } catch (Exception e) {
            post.increasePublishingErrors();
            post.setPublishingErrorMsg(e.getMessage());
            postService.save(post);

            throw e;
        }
    }

    /**
     * Publish post wrapping
     * If an instagram returns error "Transcode not finished yet.",
     * then make at most 3 tries to publish.
     *
     * @param hashtags list of hashtags
     * @param post post
     * @param client instagram client
     * @throws Exception exception
     */
    private void processPublishing(List<String> hashtags, Post post, InstaClient client) throws Exception {
        int tries = TRANSCODE_NOT_FINISHED_TRIES;
        while (tries > 0) {
            try {
                publishPost(hashtags, post, client);

                break;
            } catch (Exception e) {
                if (e.getMessage().contains(TRANSCODE_NOT_FINISHED_YET_ERROR_MSG)) {
                    logger.info(TRANSCODE_NOT_FINISHED_YET_ERROR_MSG);

                    tries--;
                } else {
                    throw e;
                }
            }
        }
    }

    private void publishPost(List<String> hashtags, Post post, InstaClient client) throws ExecutionException, InterruptedException {
        MediaResponse response;

        if (post.getMediaType().equals(MediaType.IMAGE.getValue())) {
            response = publishImage(hashtags, post.getId(), client);
        } else if (post.getMediaType().equals(MediaType.VIDEO.getValue())) {
            response = publishVideo(hashtags, post.getId(), client);
        } else {
            throw new RequestException(String.format(UNRECOGNIZED_MEDIA_TYPE_ERROR_MSG, post.getId()));
        }

        checkResponse(response, client, post);
    }

    private MediaResponse publishImage(List<String> hashtags, String postId, InstaClient client) throws ExecutionException, InterruptedException {
        ImagePost imagePost = imagePostService.findById(postId)
                .orElseThrow(() -> new org.union.common.exception.NotFoundException(String.format(IMAGE_POST_NOT_FOUND_ERROR_MSG, postId)));

        File imageFile = cloudService.getImageFileByCode(imagePost.getCode());

        if (!imageFile.exists()) {
            throw new RequestException(String.format(FILE_NOT_FOUND_ERROR_MSG, imagePost.getCode()));
        }

        return uploadPhoto(hashtags, imageFile, client);
    }

    private MediaResponse publishVideo(List<String> hashtags, String postId, InstaClient client) throws ExecutionException, InterruptedException {
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

        return uploadVideo(hashtags, videoFile, coverFile, client);
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
    private void checkResponse(MediaResponse response, InstaClient client, Post post) throws ExecutionException, InterruptedException {
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
        MediaInfoResponse mediaInfoResponse = client.requestMediaInfo(mediaId);

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

    /**
     * Uploads photo
     *
     * @param hashtags list of hashtags
     * @param imageFile file of image
     * @param client instagram client
     * @return media response
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    protected abstract MediaResponse uploadPhoto(List<String> hashtags, File imageFile, InstaClient client) throws InterruptedException, ExecutionException;

    /**
     * Uploads photo video
     *
     * @param hashtags list of hashtags
     * @param videoFile file of video
     * @param coverFile file of video cover
     * @param client instagram client
     * @return media response
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    protected abstract MediaResponse uploadVideo(List<String> hashtags, File videoFile, File coverFile, InstaClient client) throws ExecutionException, InterruptedException;

    /**
     * Log special message when publishing started
     *
     * @param postId id of post
     * @param producingChannelId id of producing channel
     */
    protected abstract void logStartPublishing(String postId, String producingChannelId);

    /**
     * Set date time of post publication
     *
     * @param post post
     * @param now current time
     */
    protected abstract void setPostPublicationDateTime(Post post, LocalDateTime now);

    /**
     * Set date time of last publication in producing channel
     *
     * @param producingChannel producing channel
     * @param now current time
     */
    protected abstract void setLastPublicationDateTime(ProducingChannel producingChannel, LocalDateTime now);

    /**
     * Log special message after successful publishing
     *
     * @param postId id of post
     * @param producingChannelId id of producing channel
     */
    protected abstract void logSuccessPublishing(String postId, String producingChannelId);
}
