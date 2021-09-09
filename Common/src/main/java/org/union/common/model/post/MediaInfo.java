package org.union.common.model.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Image post
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "post")
public class MediaInfo extends Post {

    /**
     * Instagram media identifier
     */
    private long mediaId;

    /**
     * From instagram4j
     * 1 - image
     * 2 - video
     */
    private String type;

    /**
     * Url of an image (a cover for video posts)
     */
    private String imageUrl;
}
