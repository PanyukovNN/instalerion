package org.union.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.model.post.VideoPost;
import org.union.common.repository.VideoPostRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VideoPostService {

    public final VideoPostRepository videoPostRepository;

    public VideoPost save(VideoPost post) {
        return videoPostRepository.save(post);
    }

    public List<VideoPost> findAll() {
        return videoPostRepository.findAll();
    }

    public Optional<VideoPost> findById(String postId) {
        return videoPostRepository.findById(postId);
    }
}
