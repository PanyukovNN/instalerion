package org.union.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Proxy server info for producing channel
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "proxy_server")
public class ProxyServer {

    @Id
    private String id;

    /**
     * Ip address
     */
    private String ip;

    /**
     * Port
     */
    private int port;

    /**
     * Proxy https login
     */
    private String login;

    /**
     * Proxy https password
     */
    private String password;

    /**
     * Producing channel id
     */
    private String producingChannelId;

    /**
     * Does proxy alive
     */
    private boolean alive = true;

    /**
     * ctor
     *
     * @param ip ip
     * @param port port
     * @param login login
     * @param password password
     * @param producingChannelId producing channel id
     */
    public ProxyServer(String ip, int port, String login, String password, String producingChannelId) {
        this.ip = ip;
        this.port = port;
        this.login = login;
        this.password = password;
        this.producingChannelId = producingChannelId;
    }
}
