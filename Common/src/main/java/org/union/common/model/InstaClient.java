package org.union.common.model;

import com.github.instagram4j.instagram4j.IGClient;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Instagram client to wire with producing channel
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "insta_client")
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
     * Index of device
     */
    private int deviceIndex;
}
