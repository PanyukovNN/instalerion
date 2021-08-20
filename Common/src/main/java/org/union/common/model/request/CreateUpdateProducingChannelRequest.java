package org.union.common.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateUpdateProducingChannelRequest {

    private String producingChannelId;
    private String login;
    private String password;
    private List<String> consumingChannelNames;
    private int postingPeriod;
    private String customerId;
}
