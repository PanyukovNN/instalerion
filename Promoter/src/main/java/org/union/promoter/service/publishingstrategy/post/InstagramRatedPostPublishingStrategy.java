package org.union.promoter.service.publishingstrategy.post;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.*;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramRatedPostPublishingStrategy extends InstagramBasePostPublishingStrategy {

    private final PostService postService;

    public InstagramRatedPostPublishingStrategy(PostService postService,
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
        this.postService = postService;
    }

    @Override
    protected Post definePost(PublishingRequest request) {
        // TODO create algorithm to find most rated post
        return null;
    }
}
