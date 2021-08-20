package org.union.common.model.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ChangeConsumingChannelsRequest {

    private String producingChannelId;
    private List<String> consumingChannelNames;
}
