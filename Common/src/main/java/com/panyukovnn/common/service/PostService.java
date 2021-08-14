package com.panyukovnn.common.service;

import com.panyukovnn.common.model.post.ImagePost;
import com.panyukovnn.common.model.post.Post;
import com.panyukovnn.common.model.post.VideoPost;
import com.panyukovnn.common.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.panyukovnn.common.Constants.PUBLISHING_ERROR_COUNT_LIMIT;

@Service
@RequiredArgsConstructor
public class PostService {

    public final PostRepository postRepository;

    public boolean exists(String code, String producingChannelId) {
        return postRepository.existsByCodeAndProducingChannelId(code, producingChannelId);
    }

    public VideoPost save(VideoPost post) {
        return postRepository.save(post);
    }

    public ImagePost save(ImagePost post) {
        return postRepository.save(post);
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
        return postRepository.findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanEqual(producingChannelId, PUBLISHING_ERROR_COUNT_LIMIT);
    }
}
