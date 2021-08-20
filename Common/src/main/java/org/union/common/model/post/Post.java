package org.union.common.model.post;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Base class of posts
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class Post {

    @Id
    private String id;

    /**
     * Producing channel id
     */
    private String producingChannelId;

    /**
     * Url of a file
     */
    private String url;

    /**
     * Post author description
     */
    private String description;

    /**
     * Unique instagram code
     */
    private String code;

    private PostMediaType postMediaType;

    /**
     * Date time of the post publishing
     */
    private LocalDateTime publishDateTime;

    /**
     * Number of publishing errors
     */
    private int publishingErrorCount;

    /**
     * Increase publishing errors counter
     */
    public void increasePublishingErrors() {
        this.publishingErrorCount++;
    }
}
