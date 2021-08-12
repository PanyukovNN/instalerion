package com.panyukovnn.common.model.post;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public abstract class Post {

    /**
     * Customer id
     */
    private String customerId;

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
}
