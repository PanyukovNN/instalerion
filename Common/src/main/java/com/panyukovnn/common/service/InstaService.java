package com.panyukovnn.common.service;

import com.github.instagram4j.instagram4j.IGClient;
import com.github.instagram4j.instagram4j.exceptions.IGLoginException;
import com.panyukovnn.common.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class InstaService {

    private final Map<String, IGClient> clientContext = new HashMap<>();

    private final EncryptionUtil encryptionUtil;

    public IGClient getClient(Customer customer) throws IGLoginException {
        IGClient client = clientContext.get(customer.getId());

        if (client == null || !client.isLoggedIn()) {
            client = login(customer);

            clientContext.put(customer.getId(), client);
        }

        return client;
    }

    private IGClient login(Customer customer) throws IGLoginException {
        return IGClient.builder()
                .username(customer.getLogin())
                .password(encryptionUtil.getTextEncryptor().decrypt(customer.getPassword()))
                .login();
    }
}
