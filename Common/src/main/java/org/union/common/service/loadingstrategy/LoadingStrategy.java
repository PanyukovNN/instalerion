package org.union.common.service.loadingstrategy;

import org.union.common.model.request.LoadingRequest;

public interface LoadingStrategy {

    void load(LoadingRequest request) throws Exception;
}
