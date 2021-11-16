package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.exception.RequestException;
import org.union.common.model.post.Post;
import org.union.common.service.PostService;
import org.union.common.service.publishingstrategy.PostDefiningStrategyType;

import static org.union.common.Constants.STORY_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG;

/**
 * Most recent story defining strategy
 */
@Service
@RequiredArgsConstructor
public class RecentStoryDefiningStrategy extends BasePostDefiningStrategy {

    private final PostService postService;

    @Override
    public PostDefiningStrategyType getType() {
        return PostDefiningStrategyType.MOST_RECENT_STORY;
    }

    @Override
    public Post definePost(String producingChannelId) {
        Post post = postService.findMostRecentStory(producingChannelId)
                .orElseThrow(() -> new RequestException(String.format(STORY_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG, producingChannelId)));

        checkStory(post);

        return post;
    }
}
