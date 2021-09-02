package org.union.promoter.service.publishingstrategy.story;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.*;
import org.union.promoter.service.publishingstrategy.InstagramPublishingStrategy;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Strategy to publish story in an instagram
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class InstagramBaseStoryPublishingStrategy extends InstagramPublishingStrategy {

    public InstagramBaseStoryPublishingStrategy(PostService postService,
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
    protected abstract Post definePost(PublishingRequest request);

    @Override
    protected MediaResponse uploadPhoto(Post post, File imageFile, IGClient client) throws InterruptedException, ExecutionException {
        return client
                .actions()
                .story()
                .uploadPhoto(imageFile)
                .get();
    }

    @Override
    protected MediaResponse uploadVideo(Post post, File videoFile, File coverFile, IGClient client) throws ExecutionException, InterruptedException {
        return client
                .actions()
                .story()
                .uploadVideo(videoFile, coverFile)
                .get();
    }
}
