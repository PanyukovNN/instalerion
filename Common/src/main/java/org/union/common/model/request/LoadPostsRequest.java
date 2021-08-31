package org.union.common.model.request;

import lombok.*;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.common.service.loadingstrategy.LoadingVolume;

import java.io.Serializable;

/**
 * Request to run loader module
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LoadPostsRequest extends KafkaRequest implements Serializable {

    private String producingChannelId;
    private LoadingStrategyType strategyType;
    private LoadingVolume loadingVolume;
}
