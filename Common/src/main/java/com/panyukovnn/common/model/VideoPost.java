package com.panyukovnn.common.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Video post
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "video_post")
public class VideoPost {

    @Id
    private String id;

    /**
     * Customer id
     */
    private String customerId;

    /**
     * Url of a video
     */
    private String url;

    /**
     * Url of a cover
     */
    private String coverUrl;

    /**
     * Post author description
     */
    private String description;

    /**
     * Unique instagram code
     */
    private String code;

    /**
     * Consume channel, from which video post was loaded
     */
    private ConsumeChannel consumeChannel;

    /**
     * Date time of the post publishing
     */
    private LocalDateTime publishDateTime;
}
