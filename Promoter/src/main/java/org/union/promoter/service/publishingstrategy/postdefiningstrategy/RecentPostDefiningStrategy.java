package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.exception.RequestException;
import org.union.common.model.post.Post;
import org.union.common.service.PostService;
import org.union.common.service.publishingstrategy.PostDefiningStrategyType;

import static org.union.common.Constants.POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

/**
 * Most recent post defining strategy
 */
@Service
@RequiredArgsConstructor
public class RecentPostDefiningStrategy extends BasePostDefiningStrategy {

    private final PostService postService;

    @Override
    public PostDefiningStrategyType getType() {
        return PostDefiningStrategyType.MOST_RECENT_POST;
    }

    @Override
    public Post definePost(String producingChannelId) {
        Post post = postService.findMostRecentPost(producingChannelId)
                .orElseThrow(() -> new RequestException(String.format(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG, producingChannelId)));

        checkPost(post);

        return post;
    }
}
