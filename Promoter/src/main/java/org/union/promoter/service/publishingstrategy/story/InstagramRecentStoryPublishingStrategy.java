package org.union.promoter.service.publishingstrategy.story;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.*;

/**
 * Strategy to publish story in an instagram
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramRecentStoryPublishingStrategy extends InstagramBaseStoryPublishingStrategy {

    private final PostService postService;

    public InstagramRecentStoryPublishingStrategy(PostService postService,
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
        Post post = postService.findMostRecentStory(request.getProducingChannelId())
                .orElse(null);

        checkPost(request, post);

        return post;
    }
}
