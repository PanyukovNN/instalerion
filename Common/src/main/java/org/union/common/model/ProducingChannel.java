package org.union.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Channel where posts will be published
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "producing_channel")
public class ProducingChannel {

    @Id
    private String id;

    /**
     * Channel login
     */
    private String login;

    /**
     * Channel password
     */
    private String password;

    /**
     * Consuming channels list
     */
    @DBRef
    private List<ConsumingChannel> consumingChannels;

    /**
     * Date time of last posting
     */
    private LocalDateTime lastPostingDateTime;

    /**
     * Date time of last loading
     */
    private LocalDateTime lastLoadingDateTime;

    /**
     * Posting period in minutes
     */
    private int postingPeriod;

    /**
     * Customer
     */
    @DBRef
    private Customer customer;

    /**
     * Is enabled
     */
    private boolean enabled = true;
}
