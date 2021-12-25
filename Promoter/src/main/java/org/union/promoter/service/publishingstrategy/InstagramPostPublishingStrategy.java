package org.union.promoter.service.publishingstrategy;

import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;
import org.union.common.service.*;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.union.common.Constants.*;

/**
 * Strategy to publish a post in an instagram feed
 */
@Service
public class InstagramPostPublishingStrategy extends InstagramBasePublishingStrategy {

    private final InstaService instaService;

    public InstagramPostPublishingStrategy(PostService postService,
                                           CloudService cloudService,
                                           InstaService instaService,
                                           DateTimeHelper dateTimeHelper,
                                           ProducingChannelService producingChannelService) {
        super(postService,
                cloudService,
                instaService,
                dateTimeHelper,
                producingChannelService);
        this.instaService = instaService;
    }

    @Override
    protected MediaResponse uploadPhoto(List<String> hashtags, File imageFile, InstaClient client) throws InterruptedException, ExecutionException {
        return instaService.uploadPhotoPost(client, imageFile, getCaption(hashtags));
    }

    @Override
    protected MediaResponse uploadVideo(List<String> hashtags, File videoFile, File coverFile, InstaClient client) throws IOException {
        return instaService.uploadVideoPost(client, videoFile, coverFile, getCaption(hashtags));
    }

    @Override
    protected void logStartPublishing(String postId, String producingChannelId) {
        logger.info(String.format(POST_PUBLISHING_STARTED_MSG, postId, producingChannelId));
    }

    @Override
    protected void setPostPublicationDateTime(Post post, LocalDateTime now) {
        post.getPublishedTimeByType().put(org.union.common.model.post.PublicationType.INSTAGRAM_POST, now);
    }

    @Override
    protected void setLastPublicationDateTime(ProducingChannel producingChannel, LocalDateTime now) {
        producingChannel.getPublicationTimeMap().put(org.union.common.model.post.PublicationType.INSTAGRAM_POST, now);
    }

    @Override
    protected void logSuccessPublishing(String postId, String producingChannelId) {
        logger.info(String.format(POST_SUCCESSFULLY_PUBLISHED_MSG, postId, producingChannelId));
    }

    private String getCaption(List<String> hashtags) {
        if (CollectionUtils.isEmpty(hashtags)) {
            return EMPTY;
        }

        List<String> randomHashtags = new ArrayList<>(hashtags);
        Collections.shuffle(randomHashtags);

        return randomHashtags.stream()
                .limit(POST_HASHTAG_NUMBER)
                .map(tag -> "#" + tag)
                .collect(Collectors.joining(" "));
    }

    @Override
    public PublicationType getType() {
        return PublicationType.INSTAGRAM_POST;
    }
}
