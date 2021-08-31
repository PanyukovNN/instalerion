package org.union.promoter.requestprocessor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.union.common.model.request.LoadPostsRequest;
import org.union.common.service.UseContext;
import org.union.common.service.loadingstrategy.LoadingStrategy;
import org.union.common.service.loadingstrategy.LoadingVolume;
import org.union.promoter.service.StrategyResolver;

import static org.union.common.Constants.STANDARD_LOADING_VOLUME;

/**
 * Request processor for posts loading
 */
@Service
@RequiredArgsConstructor
public class LoaderRequestProcessor {

    private final StrategyResolver strategyResolver;

    /**
     * Load posts from consuming channels to database and cloud
     *
     * @param request loading request
     * @throws Exception exception
     */
    @Transactional
    public void load(LoadPostsRequest request) throws Exception {
        try {
            UseContext.setInUse(request.getProducingChannelId());

            LoadingVolume loadingVolume = request.getLoadingVolume();
            LoadingStrategy strategy = strategyResolver.getLoadingStrategy(request.getStrategyType());
            strategy.setLoadingVolume(loadingVolume != null ? loadingVolume : STANDARD_LOADING_VOLUME);

            strategy.load(request.getProducingChannelId());
        } finally {
            UseContext.release(request.getProducingChannelId());
        }
    }
}
