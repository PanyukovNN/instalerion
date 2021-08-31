package org.union.common.service.loadingstrategy;

import lombok.*;

import java.io.Serializable;

/**
 * Volume of loading posts per consuming channel
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoadingVolume implements Serializable {

    /**
     * How old could be post
     */
    private int days;

    /**
     * Hom many post could be downloaded from a consuming channel
     */
    private int amount;
}
