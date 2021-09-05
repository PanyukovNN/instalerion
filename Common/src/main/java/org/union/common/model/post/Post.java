package org.union.common.model.post;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class of posts
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private String id;

    /**
     * Producing channel id
     */
    private String producingChannelId;

    /**
     * Url of an image (a cover for video posts)
     */
    private String imageUrl;

    /**
     * Post author description
     */
    private String description;

    /**
     * Unique instagram code
     */
    private String code;

    /**
     * From instagram4j
     * 1 - image
     * 2 - video
     */
    private String mediaType;

    /**
     * Map of post publishing time by type
     */
    private Map<PublicationType, LocalDateTime> publishedTimeByType = new HashMap<>();

    /**
     * Date time when post was taken
     */
    private LocalDateTime takenAt;

    /**
     * Number of publishing errors
     */
    private int publishingErrorCount;

    /**
     * Message of error if publication failed
     */
    private String publishingErrorMsg;

    /**
     * Rating of post
     */
    private PostRating rating;

    /**
     * Instagram media identifier
     */
    private long mediaId;

    /**
     * Increase publishing errors counter
     */
    public void increasePublishingErrors() {
        this.publishingErrorCount++;
    }
}
