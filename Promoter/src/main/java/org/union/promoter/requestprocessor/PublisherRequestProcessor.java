package org.union.promoter.requestprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.model.post.Post;
import org.union.common.model.request.PublishingRequest;
import org.union.promoter.requestprocessor.useaspect.ProducingChannelUse;
import org.union.promoter.service.publishingstrategy.PublishingStrategy;
import org.union.promoter.service.publishingstrategy.PublishingStrategyResolver;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategy;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategyResolver;
import org.union.promoter.service.publishingstrategy.sortingstrategy.PostSortingResolver;

/**
 * Request processor for post publishing
 */
@Service
@RequiredArgsConstructor
public class PublisherRequestProcessor {

    private final PostSortingResolver postSortingResolver;
    private final PublishingStrategyResolver publishingStrategyResolver;
    private final PostDefiningStrategyResolver postDefiningStrategyResolver;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param request loading request
     * @throws Exception any exception
     */
    @Transactional
    @ProducingChannelUse
    public void processPublishRequest(PublishingRequest request) throws Exception {
        Sort sort = postSortingResolver.resolveStrategy(request.getPostSortingStrategyType());

        PostDefiningStrategy postDefiningStrategy = postDefiningStrategyResolver.resolveStrategy(request.getPublicationType());
        Post post = postDefiningStrategy.definePost(request.getProducingChannelId(), sort);

        PublishingStrategy publishingStrategy = publishingStrategyResolver.resolveStrategy(request.getPublicationType());
        publishingStrategy.publish(post);
    }
}
