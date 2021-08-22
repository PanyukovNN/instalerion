package org.union.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.model.post.ImagePost;
import org.union.common.repository.ImagePostRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImagePostService {

    public final ImagePostRepository imagePostRepository;

    public ImagePost save(ImagePost post) {
        return imagePostRepository.save(post);
    }

    public List<ImagePost> findAll() {
        return imagePostRepository.findAll();
    }

    public Optional<ImagePost> findById(String postId) {
        return imagePostRepository.findById(postId);
    }
}
