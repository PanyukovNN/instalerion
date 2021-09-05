package org.union.common.model.post;

import lombok.*;

/**
 * Rating of post
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostRating {

    private double value;
    private boolean impossibleToCalculate = false;
}
