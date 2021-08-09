package com.panyukovnn.instaloader.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.panyukovnn.common.model.ConsumeChannel;
import com.panyukovnn.common.model.Customer;
import com.panyukovnn.common.model.VideoPost;
import com.panyukovnn.common.repository.CustomerRepository;
import com.panyukovnn.common.repository.VideoPostRepository;
import com.panyukovnn.common.service.CloudService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.panyukovnn.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;
import static com.panyukovnn.common.Constants.TRANSFORM_TO_VIDEO_POST_ERROR_MSG;

/**
 * Service for loading posts
 */
@Service
@RequiredArgsConstructor
public class LoaderService {

    @Value("${post.hours}")
    private int postHours;

    @Value("${post.limit}")
    private int postLimit;

    private final CloudService cloudService;
    private final EncryptionUtil encryptionUtil;
    private final CustomerRepository customerRepository;
    private final VideoPostRepository videoPostRepository;

    /**
     * Load video posts from customer consume channels to database
     *
     * @param customerId id of customer
     * @throws IOException exception
     * @throws ExecutionException exception
     * @throws InterruptedException exception
     * @throws NotFoundException exception
     */
    public void loadVideoPosts(String customerId) throws IOException, ExecutionException, InterruptedException, NotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, customerId)));

        // Login to instagram account
        IGClient client = IGClient.builder()
                .username(customer.getLogin())
                .password(encryptionUtil.getTextEncryptor().decrypt(customer.getPassword()))
                .login();

        // Load posts from consume channels
        for (ConsumeChannel consumeChannel : customer.getConsumeChannels()) {
            processConsumeChannel(client, consumeChannel);
        }

        customerRepository.save(customer);
    }

    private void processConsumeChannel(IGClient client, ConsumeChannel consumeChannel) throws InterruptedException, ExecutionException, IOException {
        String consumeChannelName = consumeChannel.getName();

        UserAction userAction = client.actions().users().findByUsername(consumeChannelName).get();

        List<TimelineVideoMedia> timelineVideoMedias = client.sendRequest(new FeedUserRequest(userAction.getUser().getPk()))
                .get()
                .getItems()
                .stream()
                .limit(postLimit)
                .filter(TimelineVideoMedia.class::isInstance)
                .map(TimelineVideoMedia.class::cast)
                .filter(video -> getDateTime(video.getTaken_at() * 1000)
                        .isAfter(LocalDateTime.now().minusHours(postHours)))
                .collect(Collectors.toList());

        List<VideoPost> videoPosts = timelineVideoMedias.stream()
                .map(this::getVideoPost)
                .collect(Collectors.toList());

        cloudService.saveVideoPosts(videoPosts);

        consumeChannel.setVideoPosts(videoPosts);
    }

    private LocalDateTime getDateTime(long mills) {
        return Instant.ofEpochMilli(mills)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private VideoPost getVideoPost(TimelineVideoMedia video) {
        VideoPost videoPost = new VideoPost();

        try {
            videoPost.setCode(video.getCode());
            videoPost.setDescription(video.getCaption().getText());
            videoPost.setUrl(video.getVideo_versions().get(0).getUrl());
            videoPost.setCoverUrl(video.getImage_versions2().getCandidates().get(0).getUrl());
        } catch (Exception e) {
            System.out.println(String.format(TRANSFORM_TO_VIDEO_POST_ERROR_MSG, e.getMessage()));
        }

        videoPostRepository.save(videoPost);

        return videoPost;
    }
}
