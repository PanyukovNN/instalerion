package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.union.common.model.post.Post;
import org.union.common.service.PostService;

/**
 * Most rated post defining strategy
 */
@Service
@RequiredArgsConstructor
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RatedPostDefiningStrategy extends BasePostDefiningStrategy {

    private final PostService postService;

    @Override
    public Post definePost(String producingChannelId) {
        Post post = postService.findMostRecentStory(producingChannelId)
                .orElse(null);

        checkPost(producingChannelId, post);

        return post;
    }
}
