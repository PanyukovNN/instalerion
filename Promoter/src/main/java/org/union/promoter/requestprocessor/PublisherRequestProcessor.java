package org.union.promoter.requestprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishingRequest;
import org.union.promoter.requestprocessor.useaspect.ProducingChannelUse;
import org.union.promoter.service.StrategyFactory;
import org.union.promoter.service.publishingstrategy.PublishingStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategy;

/**
 * Request processor for post publishing
 */
@Service
@RequiredArgsConstructor
public class PublisherRequestProcessor {

    private final StrategyFactory strategyFactory;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param request loading request
     * @throws Exception any exception
     */
    @Transactional
    @ProducingChannelUse
    public void processPublishRequest(PublishingRequest request) throws Exception {
        PostDefiningStrategy postDefiningStrategy = strategyFactory.getPostDefiningStrategy(request.getPostDefiningStrategyType());
        Post post = postDefiningStrategy.definePost(request.getProducingChannelId());

        PublishingStrategy publishingStrategy = strategyFactory.getPublishingStrategy(request.getPublishingStrategyType());
        publishingStrategy.publish(post);
    }
}
