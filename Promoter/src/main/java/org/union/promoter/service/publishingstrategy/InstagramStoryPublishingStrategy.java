package org.union.promoter.service.publishingstrategy;

import com.github.instagram4j.instagram4j.models.media.reel.item.ReelMetadataItem;
import com.github.instagram4j.instagram4j.models.media.reel.item.StoryHashtagsItem;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.union.common.Constants.STORY_PUBLISHING_STARTED_MSG;
import static org.union.common.Constants.STORY_SUCCESSFULLY_PUBLISHED_MSG;

/**
 * Strategy to publish story in an instagram
 */
@Service
public class InstagramStoryPublishingStrategy extends InstagramBasePublishingStrategy {

    private final InstaService instaService;

    public InstagramStoryPublishingStrategy(PostService postService,
                                            CloudService cloudService,
                                            InstaService instaService,
                                            DateTimeHelper dateTimeHelper,
                                            ProducingChannelService producingChannelService) {
        super(postService,
                cloudService,
                instaService,
                dateTimeHelper,
                producingChannelService);
        this.instaService = instaService;
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

        return instaService.uploadPhotoStory(client, imageFile, storyHashtagsItems);
    }

    @Override
    protected MediaResponse uploadVideo(List<String> hashtags, File videoFile, File coverFile, InstaClient client) throws ExecutionException, InterruptedException {
        List<ReelMetadataItem> metadata = new ArrayList<>();
        if (!CollectionUtils.isEmpty(hashtags)) {
            String randomHashtag = hashtags.get(new Random().nextInt(hashtags.size()));
            StoryHashtagsItem storyHashtagsItem = StoryHashtagsItem.builder()
                    .tag_name(randomHashtag)
                    .build();

            metadata.add(storyHashtagsItem);
        }

        return instaService.uploadVideoStory(client, videoFile, coverFile, metadata);
    }

    @Override
    protected void logStartPublishing(String postId, String producingChannelId) {
        logger.info(String.format(STORY_PUBLISHING_STARTED_MSG, postId, producingChannelId));
    }

    @Override
    protected void setPostPublicationDateTime(Post post, LocalDateTime now) {
        post.getPublishedTimeByType().put(PublicationType.INSTAGRAM_STORY, now);
    }

    @Override
    protected void setLastPublicationDateTime(ProducingChannel producingChannel, LocalDateTime now) {
        producingChannel.getPublicationTimeMap().put(PublicationType.INSTAGRAM_STORY, now);
    }

    @Override
    protected void logSuccessPublishing(String postId, String producingChannelId) {
        logger.info(String.format(STORY_SUCCESSFULLY_PUBLISHED_MSG, postId, producingChannelId));
    }

    @Override
    public PublicationType getType() {
        return PublicationType.INSTAGRAM_STORY;
    }
}
