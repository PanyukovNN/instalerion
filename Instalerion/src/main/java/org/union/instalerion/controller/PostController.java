package org.union.instalerion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.union.common.exception.NotFoundException;
import org.union.common.model.post.Post;
import org.union.common.service.PostService;

import java.util.List;

import static org.union.common.Constants.POST_NOT_FOUND_ERROR_MSG;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/all")
    public List<Post> getAllPosts() {
        return postService.findAll();
    }

    @GetMapping("/{id}")
    public Post getById(@PathVariable String id) {
        return postService.findById(id)
                .orElseThrow(() -> new NotFoundException(POST_NOT_FOUND_ERROR_MSG));
    }

    @GetMapping("/remove-all")
    public String removeAllPosts() {
        postService.removeAll();

        return "All posts removed successfully.";
    }

    @GetMapping("/with-errors")
    public List<Post> getAllWithErrors() {
        return postService.findAllWithErrors();
    }
}
