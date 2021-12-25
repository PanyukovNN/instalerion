package org.union.promoter.service.publishingstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.union.common.model.post.PublicationType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolver for {@link PublishingStrategy}
 */
@Slf4j
@Service
public class PublishingStrategyResolver {

    private final Map<PublicationType, PublishingStrategy> publishingStrategyMap = new HashMap<>();

    @Autowired
    public PublishingStrategyResolver(Set<PublishingStrategy> publishingStrategies) {
        publishingStrategies.forEach(strategy -> publishingStrategyMap.put(strategy.getType(), strategy));
    }

    /**
     * Returns publishing strategy by type
     *
     * @param strategyType type of strategy
     * @return strategy
     */
    public PublishingStrategy resolveStrategy(PublicationType strategyType) {
        return publishingStrategyMap.get(strategyType);
    }
}
