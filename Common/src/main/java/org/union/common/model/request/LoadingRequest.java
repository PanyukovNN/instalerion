package org.union.common.model.request;

import lombok.*;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.loadingstrategy.LoadingVolume;

import java.io.Serializable;

import static org.union.common.Constants.STANDARD_LOADING_VOLUME;

/**
 * Request to run loader module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoadingRequest extends KafkaRequest implements Serializable {

    private String producingChannelId;
    private LoadingStrategyType strategyType;
    private LoadingVolume loadingVolume = STANDARD_LOADING_VOLUME;
}
