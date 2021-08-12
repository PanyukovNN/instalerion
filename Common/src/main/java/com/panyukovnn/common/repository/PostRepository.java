package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.post.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Video posts repository
 */
@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    boolean existsByCodeAndCustomerId(String code, String customerId);

    /**
     * Find first unpublished video post by customer id
     *
     * @param customerId customer id
     * @return video post
     */
    Post findFirstByCustomerIdAndPublishDateTimeIsNull(String customerId);
}
