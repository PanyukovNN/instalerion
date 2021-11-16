package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.exception.RequestException;
import org.union.common.model.post.Post;
import org.union.common.service.PostService;
import org.union.common.service.publishingstrategy.PostDefiningStrategyType;

import static org.union.common.Constants.POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

/**
 * Most rated post defining strategy
 */
@Service
@RequiredArgsConstructor
public class RatedPostDefiningStrategy extends BasePostDefiningStrategy {

    private final PostService postService;

    @Override
    public PostDefiningStrategyType getType() {
        return PostDefiningStrategyType.MOST_RATED_POST;
    }

    @Override
    public Post definePost(String producingChannelId) {
        // if can't find most rated post, then try to find most recent
        Post post = postService.findMostRatedPost(producingChannelId)
                .orElse(postService.findMostRecentPost(producingChannelId)
                        .orElseThrow(() -> new RequestException(String.format(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG, producingChannelId))));

        checkPost(post);

        return post;
    }
}
