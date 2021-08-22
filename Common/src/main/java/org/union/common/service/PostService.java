package org.union.common.service;

import org.union.common.model.post.Post;
import org.union.common.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.Constants;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    public final PostRepository postRepository;

    public boolean exists(String code, String producingChannelId) {
        return postRepository.existsByCodeAndProducingChannelId(code, producingChannelId);
    }

    public Post save(Post post) {
        return postRepository.save(post);
    }

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(String postId) {
        return postRepository.findById(postId);
    }

    public Optional<Post> findAnyForPublication(String producingChannelId) {
        return postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThan(producingChannelId, Constants.PUBLISHING_ERROR_COUNT_LIMIT);
    }

    public void removeAll() {
        postRepository.deleteAll();
    }
}
