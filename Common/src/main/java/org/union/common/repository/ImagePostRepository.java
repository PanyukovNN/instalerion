package org.union.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.union.common.model.post.ImagePost;
import org.union.common.model.post.Post;

import java.util.Optional;

/**
 * Image posts repository
 */
@Repository
public interface ImagePostRepository extends MongoRepository<ImagePost, String> {

}
