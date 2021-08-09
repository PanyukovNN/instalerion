package com.panyukovnn.common.repository;

import com.panyukovnn.common.model.VideoPost;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Video posts repository
 */
@Repository
public interface VideoPostRepository extends MongoRepository<VideoPost, String> {
}
