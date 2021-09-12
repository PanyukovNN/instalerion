package org.union.common.model.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Media information
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MediaInfo {

    /**
     * Instagram media identifier
     */
    private long mediaId;

    /**
     * From instagram4j
     */
    private MediaType type;

    /**
     * Url of an image (a cover for video posts)
     */
    private String imageUrl;

    /**
     * Url of a video (null for photo posts)
     */
    private String videoUrl;

    /**
     * Video duration (null for photo posts)
     */
    private long duration;
}
