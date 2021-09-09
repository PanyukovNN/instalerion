package org.union.common.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.union.common.model.post.Post;

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
     * List of posts
     */
    @DBRef
    private List<Post> posts;

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
