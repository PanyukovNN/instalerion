package org.union.promoter.service.loadingstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.promoter.service.publishingstrategy.postdefiningstrategy.PostDefiningStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolver for {@link LoadingStrategy}
 */
@Slf4j
@Service
public class LoadingStrategyResolver {

    private final Map<LoadingStrategyType, LoadingStrategy> loadingStrategyMap = new HashMap<>();

    @Autowired
    public LoadingStrategyResolver(Set<LoadingStrategy> loadingStrategies) {
        loadingStrategies.forEach(strategy -> loadingStrategyMap.put(strategy.getType(), strategy));
    }

    /**
     * Return loading strategy by type
     *
     * @param type type of strategy
     * @return strategy
     */
    public LoadingStrategy resolveStrategy(LoadingStrategyType type) {
        return loadingStrategyMap.get(type);
    }
}
