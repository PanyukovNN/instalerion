package org.union.promoter.service.publishingstrategy.postdefiningstrategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.union.common.model.post.PublicationType;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Resolver for {@link PostDefiningStrategy}
 */
@Slf4j
@Service
public class PostDefiningStrategyResolver {

    private final Map<PublicationType, PostDefiningStrategy> postDefiningStrategyMap = new HashMap<>();

    @Autowired
    public PostDefiningStrategyResolver(Set<PostDefiningStrategy> postDefiningStrategies) {
        postDefiningStrategies.forEach(strategy -> postDefiningStrategyMap.put(strategy.getType(), strategy));
    }

    /**
     * Return publishing strategy by type
     *
     * @param type type of post defining strategy
     * @return post defining strategy
     */
    public PostDefiningStrategy resolveStrategy(PublicationType type) {
        return postDefiningStrategyMap.get(type);
    }
}
