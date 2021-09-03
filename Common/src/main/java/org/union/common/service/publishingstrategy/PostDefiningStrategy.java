package org.union.common.service.publishingstrategy;

import org.union.common.model.post.Post;

/**
 * Strategy to define post
 */
public interface PostDefiningStrategy {

    /**
     * Defines post for special strategy
     *
     * @param producingChannelId id of producing channel
     * @return post instance
     */
    Post definePost(String producingChannelId);
}
