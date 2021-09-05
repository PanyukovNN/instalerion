package org.union.promoter.service.publishingstrategy;

import org.union.common.model.post.Post;

/**
 * Publishing strategy
 */
public interface PublishingStrategy {

    /**
     * Publish story to producing channel
     *
     * @param post post
     * @throws Exception any exception
     */
    void publish(Post post) throws Exception;
}
