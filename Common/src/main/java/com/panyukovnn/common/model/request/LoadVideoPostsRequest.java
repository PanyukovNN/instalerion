package com.panyukovnn.common.model.request;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoadVideoPostsRequest implements Serializable {

    private String consumerId;
}
