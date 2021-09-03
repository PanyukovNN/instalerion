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

import static org.union.common.Constants.STORY_PUBLISHING_STARTED_MSG;
import static org.union.common.Constants.STORY_SUCCESSFULLY_PUBLISHED_MSG;

/**
 * Strategy to publish story in an instagram
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramStoryPublishingStrategy extends InstagramBasePublishingStrategy {

    public InstagramStoryPublishingStrategy(PostService postService,
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
        return client.uploadPhotoStory(imageFile);
    }

    @Override
    protected MediaResponse uploadVideo(Post post, File videoFile, File coverFile, InstaClient client) throws ExecutionException, InterruptedException {
        return client.uploadVideoStory(videoFile, coverFile);
    }

    @Override
    protected void logStartPublishing(String postId, String producingChannelId) {
        logger.info(String.format(STORY_PUBLISHING_STARTED_MSG, postId, producingChannelId));
    }

    @Override
    protected void logSuccessPublishing(String postId, String producingChannelId) {
        logger.info(String.format(STORY_SUCCESSFULLY_PUBLISHED_MSG, postId, producingChannelId));
    }
}
