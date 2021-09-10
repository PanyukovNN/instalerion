package org.union.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.union.common.model.post.PublicationType;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Map of publication type and its last publishing date time
     */
    private Map<PublicationType, LocalDateTime> publicationTimeMap = new HashMap<>();

    /**
     * Date time of last loading
     */
    private LocalDateTime lastLoadingDateTime;

    /**
     * Map of posting period in minutes by publication type
     */
    private Map<PublicationType, Integer> publishingPeriodMap = new HashMap<>();

    /**
     * Customer
     */
    @DBRef
    private Customer customer;

    /**
     * Is enabled
     */
    private boolean enabled = true;

    /**
     * Time when blocking started
     */
    private LocalDateTime blockingTime;

    /**
     * Producing channel subject
     */
    private ChannelSubject channelSubject;

    /**
     * List of hashtags (without # symbol)
     */
    private List<String> hashtags;

    /**
     * Proxy server info
     */
    private ProxyServer proxyServer;
}
