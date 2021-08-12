package com.panyukovnn.common.model;

import com.panyukovnn.common.model.post.ImagePost;
import com.panyukovnn.common.model.post.VideoPost;
import lombok.*;
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

    /**
     * List of image posts
     */
    private List<ImagePost> imagePosts;
}
