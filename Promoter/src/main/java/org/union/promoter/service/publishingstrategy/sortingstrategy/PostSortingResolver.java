package org.union.promoter.service.publishingstrategy.sortingstrategy;

import com.google.common.collect.ImmutableMap;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.union.common.service.publishingstrategy.PostSortingStrategyType;

import java.util.Map;

/**
 * Post sorting strategy manager
 */
@Service
public class PostSortingResolver {

    private final Map<PostSortingStrategyType, Sort> strategyMap = new ImmutableMap.Builder<PostSortingStrategyType, Sort>()
            .put(PostSortingStrategyType.MOST_RATED, Sort.by(Sort.Direction.DESC, "rating.value"))
            .put(PostSortingStrategyType.MOST_RECENT, Sort.by(Sort.Direction.DESC, "takenAt"))
            .build();

    /**
     * Define sorting strategy by type
     *
     * @param type strategy type
     * @return sorting strategy
     */
    public Sort resolveStrategy(PostSortingStrategyType type) {
        return strategyMap.get(type);
    }
}
