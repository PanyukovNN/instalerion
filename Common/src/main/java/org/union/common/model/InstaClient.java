package org.union.common.model;

import com.github.instagram4j.instagram4j.IGClient;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Instagram client to wire with producing channel
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class InstaClient {

    /**
     * Instagram4j client
     */
    private IGClient iGClient;

    /**
     * Time of last log in
     */
    private LocalDateTime loginTime;

    /**
     * Producing channel id
     */
    private String producingChannelId;

    /**
     * Proxy server of producing channel (necessary in some scenarios)
     */
    private ProxyServer proxyServer;
}
