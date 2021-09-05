package org.union.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.union.common.model.post.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Posts repository
 */
@Repository
public interface PostRepository extends MongoRepository<Post, String> {

    /**
     * Check post existence
     *
     * @param code unique code of post
     * @param producingChannelId id of producing channel
     * @return exists
     */
    // TODO create index
    boolean existsByCodeAndProducingChannelId(String code, String producingChannelId);

    /**
     * Find unpublished post by producing channel id with highest rating
     *
     * @param producingChannelId producing channel id
     * @return video post
     */
    Optional<Post> findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanOrderByRatingDesc(String producingChannelId, int errorCountLimit);

    /**
     * Find unpublished post by producing channel id downloaded most recently
     *
     * @param producingChannelId producing channel id
     * @return video post
     */
    Optional<Post> findFirstByProducingChannelIdAndPublishDateTimeIsNullAndPublishingErrorCountLessThanOrderByTakenAtDesc(String producingChannelId, int errorCountLimit);

    /**
     * Find unpublished post by producing channel id downloaded most recently
     *
     * @param producingChannelId producing channel id
     * @param errorCountLimit limit of errors
     * @param pageable page info
     * @return video post
     */
    @Query("{ " +
            "'producingChannelId' : ?0, " +
            "'publishDateTime' : null, " +
            "'publishingErrorCount' : { '$lt' : ?1 }, " +
            "'$or' : [ { 'duration' : null }, { 'duration' : { '$lte' : 15 } } ] " +
            " }")
    Page<Post> findMostRecentStory(String producingChannelId, int errorCountLimit, Pageable pageable);

    List<Post> findByProducingChannelIdAndPublishDateTimeIsNotNull(String producingChannelId);

    /**
     * Find unrated post by producing channel id
     *
     * @param producingChannelId id of producing channel
     * @param earlier posts must be earlier than specified date time
     * @param errorCountLimit limit of errors
     * @param pageable page info
     * @return page of posts
     */
    @Query("{ " +
            "'producingChannelId' : ?0, " +
            "'publishDateTime' : null, " +
            "'rating.value' : 0, " +
            "'rating.impossibleToCalculate' : false, " +
            "'takenAt' : { '$lte' : ?1 }, " +
            "'publishingErrorCount' : { '$lt' : ?2 }, " +
            " }")
    Page<Post> findLastUnratedPosts(String producingChannelId, LocalDateTime earlier, int errorCountLimit, Pageable pageable);

    /**
     * Returns posts where errors count more than defined parameter
     *
     * @param errorsCount count of errors
     * @return list of posts
     */
    List<Post> findByPublishingErrorCountGreaterThanEqual(int errorsCount);
}
