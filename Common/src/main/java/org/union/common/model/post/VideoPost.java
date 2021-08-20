package org.union.common.model.post;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Video post
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "post")
public class VideoPost extends Post {

//    @Id
//    private String id;

    /**
     * Url of a cover
     */
    private String coverUrl;
}
