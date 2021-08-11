package com.panyukovnn.common.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * Channel from which posts will be consumed
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consume_channel")
public class ConsumeChannel{

    @Id
    private String id;

    /**
     * Instagram channel name
     */
    private String name;

    /**
     * List of video posts
     */
    private List<VideoPost> videoPosts;
}
