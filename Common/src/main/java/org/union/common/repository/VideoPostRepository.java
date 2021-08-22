package org.union.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.union.common.model.post.Post;
import org.union.common.model.post.VideoPost;

import java.util.Optional;

/**
 * Video posts repository
 */
@Repository
public interface VideoPostRepository extends MongoRepository<VideoPost, String> {

}
