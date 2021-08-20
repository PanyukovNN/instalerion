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
public class PublishPostRequest {

    private String postId;
}
