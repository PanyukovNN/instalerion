package org.union.common.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SetProxyRequest {

    private String producingChannelId;
    private String ip;
    private int port;
    private String login;
    private String password;
}
