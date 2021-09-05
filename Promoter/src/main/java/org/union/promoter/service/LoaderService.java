package org.union.promoter.service;

import com.github.instagram4j.instagram4j.actions.users.UserAction;
import com.github.instagram4j.instagram4j.models.feed.Reel;
import com.github.instagram4j.instagram4j.models.media.reel.item.StoryHashtagsItem;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserRequest;
import com.github.instagram4j.instagram4j.requests.feed.FeedUserStoryRequest;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserResponse;
import com.github.instagram4j.instagram4j.responses.feed.FeedUserStoryResponse;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.union.common.model.ConsumingChannel;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.service.DateTimeHelper;
import org.union.common.service.InstaService;
import org.union.common.service.PostService;
import org.union.common.service.loadingstrategy.LoadingVolume;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Service for loader logic
 */
@Service
@RequiredArgsConstructor
public class LoaderService {

    private final PostService postService;
    private final InstaService instaService;
    private final DateTimeHelper dateTimeHelper;

    /**
     * Returns filtered list of loaded TimelineMedia
     *
     * @param producingChannel producing channel
     * @param client instagram client
     * @param consumingChannel consuming channel
     * @param loadingVolume volume of loading posts
     * @return list of loaded TimelineMedia
     * @throws InterruptedException exception
     * @throws ExecutionException exception
     */
    public List<TimelineMedia> loadConsumingChannelPosts(ProducingChannel producingChannel,
                                                         InstaClient client,
                                                         ConsumingChannel consumingChannel,
                                                         LoadingVolume loadingVolume) throws InterruptedException, ExecutionException {
        String consumeChannelName = consumingChannel.getName();

        //TODO create inner method
        UserAction userAction = client.getIGClient()
                .actions()
                .users()
                .findByUsername(consumeChannelName)
                .get();

        List<TimelineMedia> timelineItems = new ArrayList<>();

        // id of last loaded post for pagination
        String maxId = null;
        int leftToLoadPosts = loadingVolume.getAmount();
        boolean continueLoading = true;

        // while has posts to load or loading is allowed
        while(leftToLoadPosts > 0 && continueLoading) {
            // Loads first 12 posts
            //TODO create inner method
            FeedUserResponse feedUserResponse = client.getIGClient()
                    .sendRequest(new FeedUserRequest(userAction.getUser().getPk(), maxId))
                    .get();

            FeedUserStoryResponse userStoryResponse = client.getIGClient()
                    .sendRequest(new FeedUserStoryRequest(userAction.getUser().getPk()))
                    .get();

            StoryHashtagsItem

            Reel reel = userStoryResponse.getReel();

            reel.getItems();


            // Set max_id for next pagination request
            maxId = feedUserResponse.getNext_max_id();
            continueLoading = feedUserResponse.isMore_available();

            List<TimelineMedia> responseItems = feedUserResponse.getItems();

            // filter posts by time/database existence/advertising
            List<TimelineMedia> filteredResponseItems = responseItems.stream()
                    .filter(item -> {
                        // filter posts taken more that 2 hours from now and earlier than current time minus postDays
                        LocalDateTime takenAt = instaService.getTimelineMediaDateTime(item);
                        LocalDateTime now = dateTimeHelper.getCurrentDateTime();

                        return takenAt.isAfter(now.minusDays(loadingVolume.getDays()));
                    })
                    .filter(post -> !postService.exists(post.getCode(), producingChannel.getId()))
                    .filter(post -> post.getCode() != null)
                    .filter(post -> !isAdvertising(post, consumeChannelName))
                    .collect(Collectors.toList());

            timelineItems.addAll(filteredResponseItems);

            // if even one post is filtered - stop loading
            if (responseItems.size() > filteredResponseItems.size()) {
                break;
            }

            // decrease number of posts, which needs to be loaded
            leftToLoadPosts -= responseItems.size();
        }

        return timelineItems;
    }

    /**
     * Check does media contain advertising (usertags or outer links)
     *
     * @param media media
     * @param consumingChannelName name of consuming channel
     * @return is contain ad
     */
    private boolean isAdvertising(TimelineMedia media, String consumingChannelName) {
        // check usertags
        if (media.getUsertags() != null
                && !CollectionUtils.isEmpty(media.getUsertags().getIn())) {
            return true;
        }

        if (media.getCaption() != null) {
            String captionText = media.getCaption().getText();

            if (StringUtils.isEmpty(captionText)) {
                return false;
            }

            // remove links on consuming chanel
            captionText = captionText.replace("@" + consumingChannelName, "")
                    .replace("https://www.instagram.com/" + consumingChannelName + "/", "");

            // check outer links
            return captionText.contains("@")
                    || captionText.contains("http://")
                    || captionText.contains("https://")
                    || captionText.contains("родолжение")
                    || captionText.contains("итать далее");
        }

        return false;
    }
}
