package org.union.common.service.loadingstrategy;

public interface LoadingStrategy {

    void load(String id) throws Exception;

    void setLoadingVolume(LoadingVolume loadingVolume);
}
