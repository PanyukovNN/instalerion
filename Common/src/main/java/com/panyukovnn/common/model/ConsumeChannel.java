package com.panyukovnn.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * Channel from which posts will be consumed
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "consume_channel")
public class ConsumeChannel {

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
