package org.union.common.model.post;

import lombok.*;

/**
 * Rating of post
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PostRating {

    private double value;
    private boolean impossibleToCalculate = false;

    /**
     * ctor
     *
     * @param value rating value
     */
    public PostRating(double value) {
        this.value = value;
    }
}
