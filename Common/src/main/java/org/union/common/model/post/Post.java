package org.union.common.model.post;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
     * Number of publishing errors
     */
    private int publishingErrorCount;

    /**
     * Rating of post, calculated by formula (likes + comments)/views
     * To calculate rate post must be published not earlier that 2 hours from now
     */
    private double rating;

    /**
     * Increase publishing errors counter
     */
    public void increasePublishingErrors() {
        this.publishingErrorCount++;
    }
}
