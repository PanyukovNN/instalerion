package com.panyukovnn.common.model;

import com.panyukovnn.common.model.post.ImagePost;
import com.panyukovnn.common.model.post.VideoPost;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Channel from which posts will be consumed
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "consuming_channel")
public class ConsumingChannel {

    @Id
    private String id;

    /**
     * Instagram channel name
     */
    private String name;

    /**
     * List of video posts
     */
    @DBRef
    private List<VideoPost> videoPosts;

    /**
     * List of image posts
     */
    @DBRef
    private List<ImagePost> imagePosts;

    /**
     * ctor
     *
     * @param name name
     */
    public ConsumingChannel(String name) {
        this.name = name;
    }

    /**
     * Is new instance
     *
     * @return is new instance
     */
    public boolean isFromDb() {
        return this.id != null;
    }
}
