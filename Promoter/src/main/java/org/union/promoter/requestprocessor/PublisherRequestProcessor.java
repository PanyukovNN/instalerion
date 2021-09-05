package org.union.promoter.requestprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishingRequest;
import org.union.common.service.UseContext;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategy;
import org.union.promoter.service.publishingstrategy.PublishingStrategy;
import org.union.promoter.service.StrategyResolver;

/**
 * Request processor for post publishing
 */
@Service
@RequiredArgsConstructor
public class PublisherRequestProcessor {

    private final StrategyResolver strategyResolver;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param request loading request
     * @throws Exception exception
     */
    @Transactional
    public void processPublishRequest(PublishingRequest request) throws Exception {
        UseContext.checkInUse(request.getProducingChannelId());

        try {
            UseContext.setInUse(request.getProducingChannelId());

            PostDefiningStrategy postDefiningStrategy = strategyResolver.getPostDefiningStrategy(request.getPostDefiningStrategyType());
            Post post = postDefiningStrategy.definePost(request.getProducingChannelId());

            PublishingStrategy publishingStrategy = strategyResolver.getPublishingStrategy(request.getPublishingStrategyType());
            publishingStrategy.publish(post);
        } finally {
            UseContext.release(request.getProducingChannelId());
        }
    }
}
