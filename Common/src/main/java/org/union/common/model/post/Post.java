package org.union.common.model.post;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Posts (image and video)
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post")
public class Post {

    @Id
    private String id;

    /**
     * Producing channel id
     */
    private String producingChannelId;

    /**
     * Post author description
     */
    private String description;

    /**
     * Unique instagram code
     */
    private String code;

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
     * Image/video media info
     */
    private MediaInfo mediaInfo;

    /**
     * Increase publishing errors counter
     */
    public void increasePublishingErrors() {
        this.publishingErrorCount++;
    }
}
