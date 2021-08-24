package org.union.common.service;

import com.github.instagram4j.instagram4j.IGAndroidDevice;
import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import org.union.common.model.ProducingChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service to work with instagram
 */
@Service
@RequiredArgsConstructor
public class InstaService {

    @Value("${device.number:0}")
    public int deviceNumber;

    private final Map<String, IGClient> clientContext = new HashMap<>();

    private final EncryptionUtil encryptionUtil;

    public synchronized IGClient getClient(ProducingChannel producingChannel) throws IGLoginException {
        IGClient client = clientContext.get(producingChannel.getId());

        if (client == null || !client.isLoggedIn()) {
            client = login(producingChannel);
            client.setDevice(IGAndroidDevice.GOOD_DEVICES[deviceNumber]);

            clientContext.put(producingChannel.getId(), client);
        }

        return client;
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
}