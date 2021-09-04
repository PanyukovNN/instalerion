package org.union.promoter.service.publishingstrategy;

import com.github.instagram4j.instagram4j.models.media.reel.item.ReelMetadataItem;
import com.github.instagram4j.instagram4j.models.media.reel.item.StoryHashtagsItem;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;
import org.union.common.service.*;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.union.common.Constants.STORY_PUBLISHING_STARTED_MSG;
import static org.union.common.Constants.STORY_SUCCESSFULLY_PUBLISHED_MSG;

/**
 * Strategy to publish story in an instagram
 */
@Service
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramStoryPublishingStrategy extends InstagramBasePublishingStrategy {

    public InstagramStoryPublishingStrategy(PostService postService,
                                            CloudService cloudService,
                                            InstaService instaService,
                                            DateTimeHelper dateTimeHelper,
                                            ImagePostService imagePostService,
                                            VideoPostService videoPostService,
                                            ProducingChannelService producingChannelService) {
        super(postService,
                cloudService,
                instaService,
                dateTimeHelper,
                imagePostService,
                videoPostService,
                producingChannelService);
    }

    @Override
    protected MediaResponse uploadPhoto(List<String> hashtags, File imageFile, InstaClient client) throws InterruptedException, ExecutionException {
        List<String> randomHashtags = new ArrayList<>(hashtags);
        Collections.shuffle(randomHashtags);

        List<ReelMetadataItem> storyHashtagsItems = randomHashtags.stream()
                .map(hashtag -> StoryHashtagsItem.builder()
                        .tag_name(hashtag)
                        .build())
                .collect(Collectors.toList());

        return client.uploadPhotoStory(imageFile, storyHashtagsItems);
    }

    @Override
    protected MediaResponse uploadVideo(List<String> hashtags, File videoFile, File coverFile, InstaClient client) throws ExecutionException, InterruptedException {
        List<String> randomHashtags = new ArrayList<>(hashtags);
        Collections.shuffle(randomHashtags);

        List<ReelMetadataItem> storyHashtagsItems = randomHashtags.stream()
                .map(hashtag -> StoryHashtagsItem.builder()
                        .tag_name(hashtag)
                        .build())
                .collect(Collectors.toList());

        return client.uploadVideoStory(videoFile, coverFile, storyHashtagsItems);
    }

    @Override
    protected void logStartPublishing(String postId, String producingChannelId) {
        logger.info(String.format(STORY_PUBLISHING_STARTED_MSG, postId, producingChannelId));
    }

    @Override
    protected void setLastPublicationDateTime(ProducingChannel producingChannel, LocalDateTime now) {
        producingChannel.getPublicationTimeMap().put(PublicationType.INSTAGRAM_STORY, now);
    }

    @Override
    protected void logSuccessPublishing(String postId, String producingChannelId) {
        logger.info(String.format(STORY_SUCCESSFULLY_PUBLISHED_MSG, postId, producingChannelId));
    }
}
