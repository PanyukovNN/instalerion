package org.union.promoter.service.loadingstrategy;

import com.github.instagram4j.instagram4j.models.media.timeline.TimelineImageMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineVideoMedia;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.post.MediaType;
import org.union.common.model.post.Post;
import org.union.common.model.post.PostRating;
import org.union.common.model.request.LoadingRequest;
import org.union.common.service.InstaService;
import org.union.common.service.PostService;
import org.union.common.service.ProducingChannelService;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.union.common.Constants.PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG;

@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class InstagramUpdateRatingLoadingStrategy implements LoadingStrategy {

    private final PostService postService;
    private final InstaService instaService;
    private final ProducingChannelService producingChannelService;

    @Override
    public void load(LoadingRequest request) throws Exception {
        ProducingChannel producingChannel = producingChannelService.findById(request.getProducingChannelId())
                .orElseThrow(() -> new NotFoundException(String.format(PRODUCING_CHANNEL_NOT_FOUND_ERROR_MSG, request.getProducingChannelId())));

        // Login to access instagram account
        InstaClient client = instaService.getClient(producingChannel);

        updatePostsRating(producingChannel, client);
    }

    private void updatePostsRating(ProducingChannel producingChannel, InstaClient client) throws ExecutionException, InterruptedException {
        List<Post> lastUnratedPost = postService.findLastUnratedPost(producingChannel.getId());

        for (Post post : lastUnratedPost) {
            MediaInfoResponse infoResponse = instaService.requestMediaInfo(client, post.getMediaInfo().getMediaId());

            if (infoResponse == null
                    || CollectionUtils.isEmpty(infoResponse.getItems())
                    || infoResponse.getItems().size() > 1) {
                post.setRating(new PostRating(0d, true));
            } else {
                TimelineMedia media = infoResponse.getItems().get(0);

                int viewCount = 0;
                if (media.getMedia_type().equals(MediaType.VIDEO.getValue())) {
                    viewCount = ((TimelineVideoMedia) media).getView_count();
                } else if (media.getMedia_type().equals(MediaType.IMAGE.getValue())) {
                    viewCount = ((TimelineImageMedia) media).getView_count();
                }
                post.setRating(postService.calculateRating(media, viewCount, post.getTakenAt()));
            }
        }

        postService.saveAll(lastUnratedPost);
    }
}
