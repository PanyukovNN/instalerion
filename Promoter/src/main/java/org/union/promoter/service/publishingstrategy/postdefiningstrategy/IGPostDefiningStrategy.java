package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.union.common.exception.RequestException;
import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;
import org.union.common.service.PostService;

import static org.union.common.Constants.*;

/**
 * Most rated post defining strategy
 */
@Service
@RequiredArgsConstructor
public class IGPostDefiningStrategy implements PostDefiningStrategy {

    private final PostService postService;

    @Override
    public PublicationType getType() {
        return PublicationType.INSTAGRAM_POST;
    }

    @Override
    public Post definePost(String producingChannelId, Sort postSortingStrategy) {
        PublicationType type = getType();

        Post post = postService.findByPublicationType(producingChannelId, type, postSortingStrategy)
                .orElseThrow(() -> new RequestException(String.format(POST_FOR_PUBLICATION_NOT_FOUND_ERROR_MSG, producingChannelId)));

        checkPost(post);

        return post;
    }

    /**
     * Check post availability for publishing
     *
     * @param post post
     */
    protected void checkPost(Post post) {
        if (post.getPublishedTimeByType() != null
                && post.getPublishedTimeByType().get(PublicationType.INSTAGRAM_POST) != null) {
            throw new RequestException(POST_ALREADY_PUBLISHED_ERROR_MSG);
        }

        if (post.getPublishingErrorCount() >= PUBLISHING_ERROR_COUNT_LIMIT) {
            throw new RequestException(POST_TOO_MANY_PUBLISHING_ERRORS);
        }
    }
}
