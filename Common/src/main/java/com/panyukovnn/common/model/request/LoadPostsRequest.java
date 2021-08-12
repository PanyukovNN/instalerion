package com.panyukovnn.common.model.request;

import lombok.*;

import java.io.Serializable;

/**
 * Request to run loader module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoadPostsRequest implements Serializable {

    private String consumerId;
}
