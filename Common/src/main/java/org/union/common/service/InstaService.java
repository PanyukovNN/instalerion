package org.union.common.service;

import com.github.instagram4j.instagram4j.IGAndroidDevice;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import org.union.common.exception.RequestException;
import org.union.common.model.ProducingChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import static org.union.common.Constants.PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG;

/**
 * Service to work with instagram
 */
@Service
@RequiredArgsConstructor
public class InstaService {

    @Value("${device.number:0}")
    public int deviceNumber;

    private final Map<String, IGClient> clientContext = new HashMap<>();

    private final DateTimeHelper dateTimeHelper;
    private final EncryptionUtil encryptionUtil;
    private final ProducingChannelService producingChannelService;

    /**
     * Returns logged in instagram client
     *
     * @param producingChannel producing channel
     * @return logged in instagram client
     * @throws IGLoginException exception while log in
     */
    public synchronized IGClient getClient(ProducingChannel producingChannel) throws IGLoginException {
        if (producingChannelService.isBlocked(producingChannel)) {
            throw new RequestException(String.format(PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG,
                    producingChannel.getId(),
                    producingChannelService.getUnblockingFormattedDateTime(producingChannel)));
        }

        try {
            IGClient client = clientContext.get(producingChannel.getId());

            if (client == null || !client.isLoggedIn()) {
                client = login(producingChannel);
                client.setDevice(IGAndroidDevice.GOOD_DEVICES[deviceNumber]);

                clientContext.put(producingChannel.getId(), client);
            }

            return client;
        } catch (IGLoginException e) {
            // if log in is blocked
            if (e.getMessage().equals("Please wait a few minutes before you try again.")) {
                producingChannelService.setBlock(producingChannel);
            }

            throw e;
        }
    }

    private IGClient login(ProducingChannel producingChannel) throws IGLoginException {
        return login(producingChannel.getLogin(), producingChannel.getPassword());
    }

    private IGClient login(String login, String encryptedPassword) throws IGLoginException {
        return IGClient.builder()
                .username(login)
                .password(encryptionUtil.getTextEncryptor().decrypt(encryptedPassword))
                .login();
    }

    /**
     * Get localDateTime from TimelineMedia taken_at time
     *
     * @param mills milli seconds
     * @return localDateTime
     */
    public LocalDateTime getTimelineMediaDateTime(long mills) {
        return Instant.ofEpochMilli(mills)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
