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
     * Date time of the post publishing
     */
    private LocalDateTime publishDateTime;

    /**
     * Date time when post was taken
     */
    private LocalDateTime takenAt;

    /**
     * Number of publishing errors
     */
    private int publishingErrorCount;

    /**
     * Rating of post
     * Negative rating is "null rating"
     * Zero rating is "impossible to calculate"
     */
    private double rating;

    /**
     * Increase publishing errors counter
     */
    public void increasePublishingErrors() {
        this.publishingErrorCount++;
    }
}
