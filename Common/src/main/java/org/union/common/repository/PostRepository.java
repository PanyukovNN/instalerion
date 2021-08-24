package org.union.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.union.common.model.post.Post;

import java.util.Optional;

/**
 * Posts repository
 */
@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    /**
     * Check post existence
     *
     * @param code unique code of post
     * @param producingChannelId id of producing channel
     * @return exists
     */
    // TODO create index
    boolean existsByCodeAndProducingChannelId(String code, String producingChannelId);

    /**
     * Find unpublished post by producing channel id with highest rating
     *
     * @param producingChannelId producing channel id
     * @return video post
     */
    Optional<Post> findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanOrderByRatingDesc(String producingChannelId, int errorCountLimit);
}
