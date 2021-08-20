package org.union.common.model.post;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Image post
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "post")
public class ImagePost extends Post {
}
