package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import org.union.common.model.post.Post;
import org.union.common.service.publishingstrategy.PostDefiningStrategyType;

/**
 * Strategy to define post
 */
public interface PostDefiningStrategy {

    PostDefiningStrategyType getType();

    /**
     * Defines post for special strategy
     *
     * @param producingChannelId id of producing channel
     * @return post instance
     */
    Post definePost(String producingChannelId);
}
