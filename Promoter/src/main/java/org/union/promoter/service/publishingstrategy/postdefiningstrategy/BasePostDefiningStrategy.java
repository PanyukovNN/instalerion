package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import org.union.common.exception.RequestException;
import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;

import static org.union.common.Constants.*;

/**
 * Abstract post defining strategy
 */
public abstract class BasePostDefiningStrategy implements PostDefiningStrategy {

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

    /**
     * Check story availability for publishing
     *
     * @param post story
     */
    protected void checkStory(Post post) {
        if (post.getPublishedTimeByType() != null
                && post.getPublishedTimeByType().get(PublicationType.INSTAGRAM_STORY) != null) {
            throw new RequestException(STORY_ALREADY_PUBLISHED_ERROR_MSG);
        }

        if (post.getPublishingErrorCount() >= PUBLISHING_ERROR_COUNT_LIMIT) {
            throw new RequestException(POST_TOO_MANY_PUBLISHING_ERRORS);
        }
    }
}
