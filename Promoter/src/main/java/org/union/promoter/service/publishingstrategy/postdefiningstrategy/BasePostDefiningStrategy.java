package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import org.union.common.exception.RequestException;
import org.union.common.model.post.Post;
import org.union.common.service.publishingstrategy.PostDefiningStrategy;

import static org.union.common.Constants.*;

/**
 * Abstract post defining strategy
 */
public abstract class BasePostDefiningStrategy implements PostDefiningStrategy {

    /**
     * Checks post availability for publishing
     *
     * @param post post
     */
    protected void checkPost(Post post) {
        if (post.getPublishDateTime() != null) {
            throw new RequestException(POST_ALREADY_PUBLISHED_ERROR_MSG);
        }

        if (post.getPublishingErrorCount() >= PUBLISHING_ERROR_COUNT_LIMIT) {
            throw new RequestException(POST_TOO_MANY_PUBLISHING_ERRORS);
        }
    }
}
