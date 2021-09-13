package org.union.common.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.actions.media.MediaAction;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.github.instagram4j.instagram4j.models.media.UploadParameters;
import com.github.instagram4j.instagram4j.models.media.reel.item.ReelMetadataItem;
import com.github.instagram4j.instagram4j.models.media.timeline.TimelineMedia;
import com.github.instagram4j.instagram4j.requests.media.MediaConfigureTimelineRequest;
import com.github.instagram4j.instagram4j.requests.media.MediaInfoRequest;
import com.github.instagram4j.instagram4j.responses.IGResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaInfoResponse;
import com.github.instagram4j.instagram4j.responses.media.MediaResponse;
import com.github.instagram4j.instagram4j.utils.SerializableCookieJar;
import lombok.RequiredArgsConstructor;
import okhttp3.Authenticator;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.union.common.exception.ProxyException;
import org.union.common.exception.RequestException;
import org.union.common.model.InstaClient;
import org.union.common.model.ProducingChannel;
import org.union.common.model.ProxyServer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.union.common.Constants.*;

/**
 * Service to work with instagram
 */
@Service
@RequiredArgsConstructor
public class InstaService {

    private final Logger logger = LoggerFactory.getLogger(InstaService.class);

    private static final String CHANNEL_SESSIONS_PATH = "channel_sessions/";
    private static final String COOKIE_POSTFIX = "_cookie";
    private static final String TXT_FORMAT = ".txt";
    private final Map<String, InstaClient> clientContext = new HashMap<>();

    private final ProxyService proxyService;
    private final DateTimeHelper dateTimeHelper;
    private final EncryptionUtil encryptionUtil;
    private final ProducingChannelService producingChannelService;

    @PostConstruct
    private void postConstruct() {
        File channelSessionsPath = new File(CHANNEL_SESSIONS_PATH);
        if (!channelSessionsPath.exists()) {
            //noinspection ResultOfMethodCallIgnored
            channelSessionsPath.mkdir();
        }
    }

    /**
     * Returns logged in instagram client
     *
     * @param producingChannel producing channel
     * @return logged in instagram client
     * @throws IGLoginException exception while log in
     */
    public synchronized InstaClient getClient(ProducingChannel producingChannel) throws Exception {
        if (producingChannelService.isBlocked(producingChannel)) {
            throw new RequestException(String.format(PRODUCING_CHANNEL_TEMPORARY_BLOCKED_MSG,
                    producingChannel.getId(),
                    producingChannelService.getUnblockingFormattedDateTime(producingChannel)));
        }

        try {
            InstaClient client = clientContext.get(producingChannel.getId());

            if (producingChannel.getProxyServer() == null) {
                ProxyServer proxyServer = proxyService.findAnyUnattached()
                        .orElseThrow(() -> new ProxyException(NOT_FOUND_UNATTACHED_PROXY_SERVER_ERROR_MSG));

                producingChannel.setProxyServer(proxyServer);
                proxyServer.setProducingChannelId(producingChannel.getId());
                proxyService.save(proxyServer);
                producingChannelService.save(producingChannel);
            }

            if (client == null
                    || client.getIGClient() == null
                    || !client.getIGClient().isLoggedIn()
                    || isSessionExpired(client)) {
                IGClient iGClient = login(producingChannel);

                client = new InstaClient(iGClient, dateTimeHelper.getCurrentDateTime(), producingChannel.getId());

                clientContext.put(producingChannel.getId(), client);
            }

            return client;
        } catch (Exception e) {
            // if log in is blocked
            if (e.getMessage().contains("Please wait a few minutes before you try again")) {
                producingChannelService.setBlock(producingChannel);
            }

            throw e;
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
     * @param client    instagram client
     * @param imageFile file of image
     * @param caption   description
     * @return response
     */
    public MediaResponse uploadPhotoPost(InstaClient client, File imageFile, String caption) {
        return client
                .getIGClient()
                .actions()
                .timeline()
                .uploadPhoto(imageFile, caption)
                .join();
    }

    /**
     * Upload video post
     *
     * @param client    instagram client
     * @param videoFile file of video
     * @param coverFile file of cover
     * @param caption   description
     * @return response
     */
    public MediaResponse uploadVideoPost(InstaClient client, File videoFile, File coverFile, String caption) throws IOException {
        byte[] videoData = Files.readAllBytes(videoFile.toPath());
        byte[] coverData = Files.readAllBytes(coverFile.toPath());

        String upload_id = String.valueOf(System.currentTimeMillis());
        IGResponse igResponse = client.getIGClient()
                .actions()
                .upload()
                .videoWithCover(videoData, coverData,
                        UploadParameters.forTimelineVideo(upload_id, false))
                .join();

        if (igResponse.getStatusCode() == 200) {
            try {
                Thread.sleep(PUBLISHING_SLEEP_SECONDS * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return client.getIGClient().actions().upload().finish(upload_id)
                .thenCompose(response -> MediaAction.configureMediaToTimeline(client.getIGClient(), upload_id, new MediaConfigureTimelineRequest.MediaConfigurePayload().caption(caption)))
                .join();
    }

    /**
     * Upload image story
     *
     * @param client    instagram client
     * @param imageFile file of image
     * @param metadata  story metadata
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
     * Upload video story
     *
     * @param client    instagram client
     * @param videoFile file of video
     * @param coverFile file of cover
     * @param metadata  story metadata
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
     * Return info about instagram media
     *
     * @param client  instagram client
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

    private IGClient login(ProducingChannel producingChannel) throws IOException, ClassNotFoundException {
        return login(producingChannel.getLogin(), producingChannel.getPassword(), producingChannel.getProxyServer());
    }

    private IGClient login(String login, String encryptedPassword, ProxyServer proxyServer) throws IOException, ClassNotFoundException {
        File clientFile = new File(CHANNEL_SESSIONS_PATH + login + TXT_FORMAT);
        File cookieFile = new File(CHANNEL_SESSIONS_PATH + login + COOKIE_POSTFIX + TXT_FORMAT);

        // configure http client
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .readTimeout(60_000, TimeUnit.SECONDS)
                .writeTimeout(60_000, TimeUnit.SECONDS)
                .connectTimeout(60_000, TimeUnit.SECONDS)
                .callTimeout(60_000, TimeUnit.SECONDS)
                .proxy(createProxy(proxyServer))
                .proxyAuthenticator(createProxyAuthenticator(proxyServer))
                .cookieJar(new SerializableCookieJar());

        if (clientFile.exists()
                && cookieFile.exists()) {
            IGClient igClient = IGClient.deserialize(clientFile, cookieFile, httpClientBuilder);
            logger.info(String.format(LOGIN_SESSION_DESERIALIZED_MSG, login));

            return igClient;
        }

        IGClient iGclient = IGClient.builder()
                .username(login)
                .password(encryptionUtil.getTextEncryptor().decrypt(encryptedPassword))
                .client(httpClientBuilder.build())
                .login();

        iGclient.serialize(clientFile, cookieFile);
        logger.info(String.format(LOGGED_IN_AND_SERIALIZED_MSG, login));

        return iGclient;
    }

    private Proxy createProxy(ProxyServer proxyServer) {
        if (proxyServer == null) {
            return null;
        }

        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer.getIp(), proxyServer.getPort()));
    }

    private Authenticator createProxyAuthenticator(ProxyServer proxyServer) {
        if (proxyServer == null) {
            return null;
        }

        return (route, response) -> {
            if (responseCount(response) >= 3) {
                return null;
            }

            String credential = Credentials.basic(proxyServer.getLogin(), proxyServer.getPassword());

            return response.request().newBuilder().header("Proxy-Authorization", credential).build();
        };
    }

    private int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }

        return result;
    }
}
