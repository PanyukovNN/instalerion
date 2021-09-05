package org.union.common.service;

import com.github.instagram4j.instagram4j.IGAndroidDevice;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.union.common.exception.RequestException;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.union.common.Constants.IG_CLIENT_EXPIRING_HOURS;
import static org.union.common.Constants.PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG;

/**
 * Service to work with instagram
 */
@Service
@RequiredArgsConstructor
public class InstaService {

    @Value("${device.number:0}")
    public int deviceNumber;

    private final Map<String, InstaClient> clientContext = new HashMap<>();

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
    public synchronized InstaClient getClient(ProducingChannel producingChannel) throws IGLoginException {
        if (producingChannelService.isBlocked(producingChannel)) {
            throw new RequestException(String.format(PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG,
                    producingChannel.getId(),
                    producingChannelService.getUnblockingFormattedDateTime(producingChannel)));
        }

        try {
            InstaClient client = clientContext.get(producingChannel.getId());

            if (client == null
                    || client.getIGClient() == null
                    || !client.getIGClient().isLoggedIn()
                    || isSessionExpired(client)) {
                IGClient iGClient = login(producingChannel);

                client = new InstaClient(iGClient, dateTimeHelper.getCurrentDateTime());

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

    private boolean isSessionExpired(InstaClient client) {
        return client.getLoginTime().isBefore(
                dateTimeHelper.getCurrentDateTime().minusHours(IG_CLIENT_EXPIRING_HOURS));
    }

    private IGClient login(ProducingChannel producingChannel) throws IGLoginException {
        return login(producingChannel.getLogin(), producingChannel.getPassword());
    }

    private IGClient login(String login, String encryptedPassword) throws IGLoginException {
        IGClient iGclient = IGClient.builder()
                .username(login)
                .password(encryptionUtil.getTextEncryptor().decrypt(encryptedPassword))
                .login();

        iGclient.setDevice(IGAndroidDevice.GOOD_DEVICES[deviceNumber]);
        // increase timeouts
        OkHttpClient httpClient = iGclient
                .getHttpClient()
                .newBuilder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        iGclient.setHttpClient(httpClient);

        return iGclient;
    }

    /**
     * Get localDateTime from TimelineMedia taken_at time
     *
     * @param media TimelineMedia entity
     * @return localDateTime
     */
    public LocalDateTime getTimelineMediaDateTime(TimelineMedia media) {
        return Instant.ofEpochMilli(media.getTaken_at() * 1000)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
