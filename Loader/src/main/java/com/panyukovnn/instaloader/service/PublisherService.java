package com.panyukovnn.instaloader.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.VideoPost;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.common.service.CloudService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;

import static com.panyukovnn.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
public class PublisherService {

    private final CloudService cloudService;
    private final EncryptionUtil encryptionUtil;
    private final CustomerRepository customerRepository;

    public void uploadVideo(String customerId, VideoPost videoPost) throws NotFoundException, IGLoginException {
        if (videoPost.getPublishDateTime() == null) {
            // TODO бросать ошибку, что видео уже опубликовано
            return;
        }

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, customerId)));

        // Login to instagram account
        IGClient client = IGClient.builder()
                .username(customer.getLogin())
                .password(encryptionUtil.getTextEncryptor().decrypt(customer.getPassword()))
                .login();

        File videoFile = cloudService.getVideoFileByCode(videoPost.getCode());
        File coverFile = cloudService.getPhotoFileByCode(videoPost.getCode());

        String descriptionWithSource = videoPost.getDescription() + getSource(videoPost.getCode());

        client.actions().timeline().uploadVideo(videoFile, coverFile, descriptionWithSource);

        videoPost.setPublishDateTime(LocalDateTime.now());

        customerRepository.save(customer);
    }

    private String getSource(String code) {
        return String.format("\n\nИсточник: https://www.instagram.com/p/%s/", code);
    }
}
