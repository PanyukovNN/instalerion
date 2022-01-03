package org.union.promoter.service.publishingstrategy;

import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;

/**
 * Publishing strategy
 */
public interface PublishingStrategy {

    PublicationType getType();

    /**
     * Publish story to producing channel
     *
     * @param post post
     */
    void publish(Post post);
}
