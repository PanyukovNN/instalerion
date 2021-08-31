package org.union.promoter.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.union.common.service.loadingstrategy.LoadingStrategy;
import org.union.common.service.loadingstrategy.LoadingStrategyType;
import org.union.promoter.kafka.LoaderKafkaListener;
import org.union.promoter.service.loadingstrategy.InstagramPostLoadingStrategy;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static org.union.common.Constants.STRATEGY_RESOLVING_ERROR_MSG;

/**
 * Resolver to get strategy by type
 */
@Service
@RequiredArgsConstructor
public class StrategyResolver {

    private final Logger logger = LoggerFactory.getLogger(LoaderKafkaListener.class);
    private final Map<LoadingStrategyType, Class<? extends LoadingStrategy>> loadingStrategyMap = new HashMap<>();

    private final ApplicationContext context;

    @PostConstruct
    public void postConstruct() {
        loadingStrategyMap.put(LoadingStrategyType.INSTAGRAM_POSTS, InstagramPostLoadingStrategy.class);
    }

    /**
     * Returns loading strategy by type
     *
     * @param strategyType type of strategy
     * @return loading strategy
     */
    public LoadingStrategy getLoadingStrategy(LoadingStrategyType strategyType) {
        try {
            Class<? extends LoadingStrategy> strategyClass = loadingStrategyMap.get(strategyType);

            return context.getBean(strategyClass);
        } catch (Exception e) {
            logger.error(STRATEGY_RESOLVING_ERROR_MSG);

            throw e;
        }
    }
}
