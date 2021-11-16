package org.union.promoter.service.publishingstrategy;

import org.union.common.model.post.Post;
import org.union.common.service.publishingstrategy.PublishingStrategyType;

/**
 * Publishing strategy
 */
public interface PublishingStrategy {

    PublishingStrategyType getType();

    /**
     * Publish story to producing channel
     *
     * @param post post
     * @throws Exception any exception
     */
    void publish(Post post) throws Exception;
}
