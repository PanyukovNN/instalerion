package org.union.instalerion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.union.common.exception.NotFoundException;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.Customer;
import org.union.common.model.ProducingChannel;
import org.union.common.model.dto.ProducingChannelDto;
import org.union.common.model.post.Post;
import org.union.common.model.request.ChangeConsumingChannelsRequest;
import org.union.common.model.request.CreateUpdateProducingChannelRequest;
import org.union.common.service.ConsumingChannelService;
import org.union.common.service.CustomerService;
import org.union.common.service.PostService;
import org.union.common.service.ProducingChannelService;

import java.util.List;
import java.util.stream.Collectors;

import static org.union.common.Constants.*;

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
}
