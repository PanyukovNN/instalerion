package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import org.springframework.data.domain.Sort;
import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;

/**
 * Post defining strategy
 */
public interface PostDefiningStrategy {

    /**
     * Defines post for special strategy
     *
     * @param producingChannelId id of producing channel
     * @param postSortingStrategy strategy of post sorting
     * @return post instance
     */
    Post definePost(String producingChannelId, Sort postSortingStrategy);

    PublicationType getType();
}
