package com.panyukovnn.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class VideoPost {

    private String url;
    private String coverUrl;
    private String description;
}
