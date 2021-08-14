package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.post.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Video posts repository
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
     * Find first unpublished video post by producing channel id
     *
     * @param producingChannelId producing channel id
     * @return video post
     */
    Optional<Post> findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanEqual(String producingChannelId, int errorCountLimit);
}
