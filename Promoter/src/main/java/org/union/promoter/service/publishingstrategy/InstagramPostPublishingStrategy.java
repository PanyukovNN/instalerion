package org.union.promoter.service.publishingstrategy;

import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.model.InstaClient;
import org.union.common.model.post.Post;
import org.union.common.service.*;

import java.io.File;
import java.util.concurrent.ExecutionException;

import static org.apache.logging.log4j.util.Strings.EMPTY;
import static org.union.common.Constants.POST_PUBLISHING_STARTED_MSG;
import static org.union.common.Constants.POST_SUCCESSFULLY_PUBLISHED_MSG;

/**
 * Strategy to publish a post in an instagram feed
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramPostPublishingStrategy extends InstagramBasePublishingStrategy {

    public InstagramPostPublishingStrategy(PostService postService,
                                           CloudService cloudService,
                                           InstaService instaService,
                                           DateTimeHelper dateTimeHelper,
                                           ImagePostService imagePostService,
                                           VideoPostService videoPostService,
                                           ProducingChannelService producingChannelService) {
        super(postService,
                cloudService,
                instaService,
                dateTimeHelper,
                imagePostService,
                videoPostService,
                producingChannelService);
    }

    @Override
    protected MediaResponse uploadPhoto(Post post, File imageFile, InstaClient client) throws InterruptedException, ExecutionException {
        return client.uploadPhotoPost(imageFile, getCaption(post));
    }

    @Override
    protected MediaResponse uploadVideo(Post post, File videoFile, File coverFile, InstaClient client) throws ExecutionException, InterruptedException {
        return client.uploadVideoPost(videoFile, coverFile, getCaption(post));

    }

    @Override
    protected void logStartPublishing(String postId, String producingChannelId) {
        logger.info(String.format(POST_PUBLISHING_STARTED_MSG, postId, producingChannelId));
    }

    @Override
    protected void logSuccessPublishing(String postId, String producingChannelId) {
        logger.info(String.format(POST_SUCCESSFULLY_PUBLISHED_MSG, postId, producingChannelId));
    }

    private String getCaption(Post post) {
        return EMPTY;
        //post.getDescription();
//        StringUtils.hasText(post.getDescription())
//                ? post.getDescription() + "\n\n" + String.format(SOURCE_STRING_TEMPLATE, post.getCode())
//                : String.format(SOURCE_STRING_TEMPLATE, post.getCode());
    }
}
