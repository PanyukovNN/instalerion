package org.union.promoter.service.publishingstrategy.post;

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

import static org.apache.logging.log4j.util.Strings.EMPTY;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class InstagramBasePostPublishingStrategy extends InstagramPublishingStrategy {

    public InstagramBasePostPublishingStrategy(PostService postService,
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
                .timeline()
                .uploadPhoto(imageFile, getCaption(post))
                .get();
    }

    @Override
    protected MediaResponse uploadVideo(Post post, File videoFile, File coverFile, IGClient client) throws ExecutionException, InterruptedException {
        return client
                .actions()
                .timeline()
                .uploadVideo(videoFile, coverFile, getCaption(post))
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
