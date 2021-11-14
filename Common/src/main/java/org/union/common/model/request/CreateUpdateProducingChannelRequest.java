package org.union.common.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.union.common.model.ChannelSubject;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateUpdateProducingChannelRequest {

    private String producingChannelId;
    private String login;
    private String password;
    private List<String> consumingChannelNames;
    private int postPublishingPeriod;
    private int storyPublishingPeriod;
    private ChannelSubject subject;
    private List<String> hashtags;
    private String customerId;
}
