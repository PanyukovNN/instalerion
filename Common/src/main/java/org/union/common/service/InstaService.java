package org.union.common.service;

import com.github.instagram4j.instagram4j.IGAndroidDevice;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.reel.item.ReelMetadataItem;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.media.MediaInfoRequest;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.stereotype.Service;
import org.union.common.exception.DeviceException;
import org.union.common.exception.RequestException;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.union.common.Constants.IG_CLIENT_EXPIRING_HOURS;
import static org.union.common.Constants.PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG;

/**
 * Service to work with instagram
 */
@Service
@RequiredArgsConstructor
public class InstaService {

    private final List<Integer> deviceIndexes = IntStream.range(0, 6)
            .boxed()
            .collect(Collectors.toList());
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
                int deviceIndex = getDeviceIndex(client);

                IGClient iGClient = login(producingChannel, deviceIndex);

                client = new InstaClient(iGClient, dateTimeHelper.getCurrentDateTime(), producingChannel.getId(), deviceIndex);

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

    private int getDeviceIndex(InstaClient client) {
        if (client != null) {
            return client.getDeviceIndex();
        } else {
            if (deviceIndexes.isEmpty()) {
                throw new DeviceException("Не хватает устройств для каналов потребления.");
            }
            deviceIndexes.remove(0);
            return deviceIndexes.get(0);
        }
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

    /**
     * Uploads image post
     *
     * @param client instagram client
     * @param imageFile file of image
     * @param caption description
     * @return response
     */
    public MediaResponse uploadPhotoPost(InstaClient client, File imageFile, String caption) throws ExecutionException, InterruptedException {
        return client
                .getIGClient()
                .actions()
                .timeline()
                .uploadPhoto(imageFile, caption)
                .get();
    }

    /**
     * Uploads video post
     *
     * @param client instagram client
     * @param videoFile file of video
     * @param coverFile file of cover
     * @param caption description
     * @return response
     */
    public MediaResponse uploadVideoPost(InstaClient client, File videoFile, File coverFile, String caption) throws ExecutionException, InterruptedException {
        return client
                .getIGClient()
                .actions()
                .timeline()
                .uploadVideo(videoFile, coverFile, caption)
                .get();
    }

    /**
     * Uploads image story
     *
     * @param client instagram client
     * @param imageFile file of image
     * @param metadata story metadata
     * @return response
     */
    public MediaResponse uploadPhotoStory(InstaClient client, File imageFile, List<ReelMetadataItem> metadata) throws ExecutionException, InterruptedException {
        return client
                .getIGClient()
                .actions()
                .story()
                .uploadPhoto(imageFile, metadata)
                .get();
    }

    /**
     * Uploads video story
     *
     * @param client instagram client
     * @param videoFile file of video
     * @param coverFile file of cover
     * @param metadata story metadata
     * @return response
     */
    public MediaResponse uploadVideoStory(InstaClient client, File videoFile, File coverFile, List<ReelMetadataItem> metadata) throws ExecutionException, InterruptedException {
        return client
                .getIGClient()
                .actions()
                .story()
                .uploadVideo(videoFile, coverFile, metadata)
                .get();
    }

    /**
     * Returns info about instagram media
     *
     * @param client instagram client
     * @param mediaId id of media
     * @return media info response
     */
    public MediaInfoResponse requestMediaInfo(InstaClient client, long mediaId) throws ExecutionException, InterruptedException {
        return client
                .getIGClient()
                .sendRequest(new MediaInfoRequest(String.valueOf(mediaId)))
                .get();
    }

    private boolean isSessionExpired(InstaClient client) {
        return client.getLoginTime().isBefore(
                dateTimeHelper.getCurrentDateTime().minusHours(IG_CLIENT_EXPIRING_HOURS));
    }

    private IGClient login(ProducingChannel producingChannel, int deviceIndex) throws IGLoginException {
        return login(producingChannel.getLogin(), producingChannel.getPassword(), deviceIndex);
    }

    private IGClient login(String login, String encryptedPassword, int deviceIndex) throws IGLoginException {
        IGClient iGclient = IGClient.builder()
                .username(login)
                .password(encryptionUtil.getTextEncryptor().decrypt(encryptedPassword))
                .login();

        iGclient.setDevice(IGAndroidDevice.GOOD_DEVICES[deviceIndex]);
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
}
