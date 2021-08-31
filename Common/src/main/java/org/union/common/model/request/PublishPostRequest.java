package org.union.common.model.request;

import lombok.*;

/**
 * Request to run publisher module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PublishPostRequest extends KafkaRequest {

    private String postId;
    private String mediaType;
}
