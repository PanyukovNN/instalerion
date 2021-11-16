package org.union.promoter.service.loadingstrategy;

import org.union.common.model.request.LoadingRequest;
import org.union.common.service.loadingstrategy.LoadingStrategyType;

public interface LoadingStrategy {

    LoadingStrategyType getType();

    void load(LoadingRequest request) throws Exception;
}
