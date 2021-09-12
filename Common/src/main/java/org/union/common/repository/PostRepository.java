package org.union.common.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import org.union.common.model.post.Post;
import org.union.common.model.post.PublicationType;

import java.time.LocalDateTime;
import java.util.List;

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
     * @return does post exists
     */
    // TODO create index
    boolean existsByCodeAndProducingChannelId(String code, String producingChannelId);

    /**
     * Find unpublished post by producing channel id with highest (not null) rating
     *
     * @param producingChannelId id of producing channel
     * @param publicationType type of publication
     * @param errorCountLimit limit of errors
     * @param pageable page info
     * @return most rated post
     */
    @Query("{ " +
            "'producingChannelId' : ?0, " +
            "'publishedTimeByType.?1' : null, " +
            "'publishingErrorCount' : { '$lt' : ?2 }, " +
            "'rating' : {$ne : null} " +
            " }")
    Page<Post> findMostRated(String producingChannelId, PublicationType publicationType, int errorCountLimit, Pageable pageable);

    /**
     * Find unpublished post by producing channel id downloaded most recently
     *
     * @param producingChannelId producing channel id
     * @param publicationType type of publication
     * @param errorCountLimit limit of errors
     * @param pageable page info
     * @return post
     */
    @Query("{ " +
            "'producingChannelId' : ?0, " +
            "'publishedTimeByType.?1' : null, " +
            "'publishingErrorCount' : { '$lt' : ?2 }, " +
            " }")
    Page<Post> findMostRecentPost(String producingChannelId, PublicationType publicationType, int errorCountLimit, Pageable pageable);

    /**
     * Find unpublished story by producing channel id downloaded most recently
     * (duration less than 15 seconds)
     *
     * @param producingChannelId producing channel id
     * @param publicationType type of publication
     * @param errorCountLimit limit of errors
     * @param pageable page info
     * @return post
     */
    @Query("{ " +
            "'producingChannelId' : ?0, " +
            "'publishedTimeByType.?1' : null, " +
            "'publishingErrorCount' : { '$lt' : ?2 }, " +
            "'$or' : [ { 'duration' : null }, { 'duration' : { '$lte' : 15 } } ] " +
            " }")
    Page<Post> findMostRecentStory(String producingChannelId, PublicationType publicationType, int errorCountLimit, Pageable pageable);

    /**
     * Find posts published in producing channel by publicationType
     *
     * @param producingChannelId id of producing channel
     * @param publicationType type of publication
     * @return list of published posts
     */
    @Query("{ " +
            "'producingChannelId' : ?0, " +
            "'publishedTimeByType.?1' : {$ne : null} " +
            " }")
    List<Post> findPublishedByProducingChannelAndPublicationType(String producingChannelId, PublicationType publicationType);

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
