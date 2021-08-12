package com.panyukovnn.instaloader.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.panyukovnn.common.exception.RequestException;
import com.panyukovnn.common.model.ConsumingChannel;
import com.panyukovnn.common.model.ProducingChannel;
import com.panyukovnn.common.model.post.ImagePost;
import com.panyukovnn.common.model.post.PostMediaType;
import com.panyukovnn.common.model.post.VideoPost;
import com.panyukovnn.common.repository.ConsumingChannelRepository;
import com.panyukovnn.common.repository.ProducingChannelRepository;
import com.panyukovnn.common.repository.PostRepository;
import com.panyukovnn.common.service.CloudService;
import com.panyukovnn.common.service.DateTimeHelper;
import com.panyukovnn.common.service.InstaService;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.panyukovnn.common.Constants.*;

/**
 * Service for loading posts
 * TODO add verbose javadoc
 */
@Service
@RequiredArgsConstructor
public class LoaderService {

    @Value("${post.hours}")
    private int postHours;
    @Value("${post.limit}")
    private int postLimit;
    @Value("${min.loading.period.minutes}")
    private int minLoadingPeriod;

    private final CloudService cloudService;
    private final InstaService instaService;
    private final PostRepository postRepository;
    private final DateTimeHelper dateTimeHelper;
    private final ProducingChannelRepository producingChannelRepository;
    private final ConsumingChannelRepository consumingChannelRepository;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param customerId id of producing channel
     * @throws IOException exception
     * @throws ExecutionException exception
     * @throws InterruptedException exception
     * @throws NotFoundException exception
     */
    @Transactional
    public void load(String customerId) throws IOException, ExecutionException, InterruptedException, NotFoundException {
        ProducingChannel producingChannel = producingChannelRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, customerId)));

        checkOftenRequests(producingChannel);

        // Login to instagram account
        IGClient client = instaService.getClient(producingChannel);

        List<ConsumingChannel> consumingChannels = producingChannel.getConsumingChannels();

        // Load posts from consume channels
        for (ConsumingChannel consumingChannel : consumingChannels) {
            processConsumeChannel(producingChannel, client, consumingChannel);
        }

        producingChannel.setLastLoadingDateTime(LocalDateTime.now());
        consumingChannelRepository.saveAll(consumingChannels);
        producingChannelRepository.save(producingChannel);
    }

    private void checkOftenRequests(ProducingChannel producingChannel) {
        if (producingChannel.getLastPostingDateTime() != null) {
            int minutesDiff = dateTimeHelper.minuteFromNow(producingChannel.getLastLoadingDateTime());
            if (minLoadingPeriod > minutesDiff) {
                throw new RequestException(TOO_OFTEN_LOADING_REQUESTS_ERROR_MSG);
            }
        }
    }

    private void processConsumeChannel(ProducingChannel producingChannel, IGClient client, ConsumingChannel consumingChannel) throws InterruptedException, ExecutionException, IOException {
        String consumeChannelName = consumingChannel.getName();

        UserAction userAction = client.actions().users().findByUsername(consumeChannelName).get();

        // TODO переделать с sendRequest на нативную реализацию
        List<TimelineMedia> timelineItems = client.sendRequest(new FeedUserRequest(userAction.getUser().getPk()))
                .get()
                .getItems()
                .stream()
                .limit(postLimit)
                .filter(item -> getDateTime(item.getTaken_at() * 1000)
                        .isAfter(LocalDateTime.now().minusHours(postHours)))
                // TODO find more effective way
                .filter(videoPost -> !postRepository.existsByCodeAndProducingChannelId(videoPost.getCode(), producingChannel.getId()))
                .collect(Collectors.toList());

        processVideoPosts(producingChannel, consumingChannel, timelineItems);
        processImagePosts(producingChannel, consumingChannel, timelineItems);
    }

    private void processVideoPosts(ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<VideoPost> videoPosts = timelineItems.stream()
                .filter(TimelineVideoMedia.class::isInstance)
                .map(TimelineVideoMedia.class::cast)
                .map(videoItem -> getVideoPost(producingChannel, videoItem))
                .map(postRepository::save)
                .collect(Collectors.toList());

        cloudService.saveVideoPosts(videoPosts);
        consumingChannel.setVideoPosts(videoPosts);
    }

    private void processImagePosts(ProducingChannel producingChannel, ConsumingChannel consumingChannel, List<TimelineMedia> timelineItems) throws IOException {
        List<ImagePost> imagePosts = timelineItems.stream()
                .filter(TimelineImageMedia.class::isInstance)
                .map(TimelineImageMedia.class::cast)
                .map(imageItem -> getImagePost(producingChannel, imageItem))
                .map(postRepository::save)
                .collect(Collectors.toList());

        cloudService.saveImagePosts(imagePosts);
        consumingChannel.setImagePosts(imagePosts);
    }

    private LocalDateTime getDateTime(long mills) {
        return Instant.ofEpochMilli(mills)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private VideoPost getVideoPost(ProducingChannel producingChannel, TimelineVideoMedia video) {
        VideoPost videoPost = new VideoPost();

        try {
            videoPost.setCode(video.getCode());

            if (video.getCaption() != null) {
                videoPost.setDescription(video.getCaption().getText());
            } else {
                videoPost.setDescription("");
            }

            videoPost.setPostMediaType(PostMediaType.VIDEO);
            videoPost.setUrl(video.getVideo_versions().get(0).getUrl());
            videoPost.setCoverUrl(video.getImage_versions2().getCandidates().get(0).getUrl());
            videoPost.setProducingChannelId(producingChannel.getId());
        } catch (Exception e) {
            System.out.println(String.format(TRANSFORM_TO_VIDEO_POST_ERROR_MSG, e.getMessage()));
        }

        return videoPost;
    }

    private ImagePost getImagePost(ProducingChannel producingChannel, TimelineImageMedia image) {
        ImagePost imagePost = new ImagePost();

        try {
            imagePost.setCode(image.getCode());

            if (image.getCaption() != null) {
                imagePost.setDescription(image.getCaption().getText());
            } else {
                imagePost.setDescription("");
            }

            imagePost.setPostMediaType(PostMediaType.IMAGE);
            imagePost.setUrl(image.getImage_versions2().getCandidates().get(0).getUrl());
            imagePost.setProducingChannelId(producingChannel.getId());
        } catch (Exception e) {
            System.out.println(String.format(TRANSFORM_TO_IMAGE_POST_ERROR_MSG, e.getMessage()));
        }

        return imagePost;
    }
}
