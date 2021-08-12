package com.panyukovnn.instapublisher.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.panyukovnn.common.Constants;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.post.VideoPost;
import com.panyukovnn.common.model.request.UploadVideoRequest;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.common.repository.VideoPostRepository;
import com.panyukovnn.common.service.CloudService;
import com.panyukovnn.common.service.InstaService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

import static com.panyukovnn.common.Constants.*;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final CloudService cloudService;
    private final InstaService instaService;
    private final CustomerRepository customerRepository;
    private final VideoPostRepository videoPostRepository;

    public void uploadVideo(UploadVideoRequest request) throws NotFoundException, IGLoginException {
        VideoPost videoPost = videoPostRepository.findById(request.getVideoPostId()).orElse(null);

        if (videoPost == null) {
            System.out.println(String.format(VIDEO_POST_NOT_FOUND_ERROR_MSG, request.getVideoPostId()));

            return;
        }

        if (videoPost.getPublishDateTime() != null) {
            System.out.println(VIDEO_POST_ALREADY_PUBLISHED_ERROR_MSG);

            return;
        }

        Customer customer = customerRepository.findById(videoPost.getCustomerId())
                .orElseThrow(() -> new NotFoundException(String.format(Constants.CUSTOMER_NOT_FOUND_ERROR_MSG, videoPost.getCustomerId())));

        // Login to instagram account
        IGClient client = instaService.getClient(customer);

        File videoFile = cloudService.getVideoFileByCode(videoPost.getCode());
        File coverFile = cloudService.getPhotoFileByCode(videoPost.getCode());

        if (!videoFile.exists() || !coverFile.exists()) {
            System.out.println(String.format(VIDEO_FILE_NOT_FOUND_ERROR_MSG, videoPost.getCode()));

            return;
        }

        String descriptionWithSource = videoPost.getDescription() + getSource(videoPost.getCode());

        client.actions().timeline().uploadVideo(videoFile, coverFile, descriptionWithSource);

        //TODO change to update query
        videoPost.setPublishDateTime(LocalDateTime.now());
        videoPostRepository.save(videoPost);
    }

    private String getSource(String code) {
        return String.format("\n\nИсточник: https://www.instagram.com/p/%s/", code);
    }
}
