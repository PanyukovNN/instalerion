package org.union.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.exception.ProxyException;
import org.union.common.model.ProducingChannel;
import org.union.common.model.ProxyServer;
import org.union.common.repository.ProxyServerRepository;

import java.util.List;
import java.util.Optional;

import static org.union.common.Constants.*;

/**
 * Service for working with proxy service
 */
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final ProxyServerRepository proxyRepository;
    private final ProducingChannelService producingChannelService;

    /**
     * Save a proxy server
     *
     * @param proxyServer proxy server
     */
    public ProxyServer save(ProxyServer proxyServer) {
        if (proxyServer == null) {
            throw new ProxyException(NULL_FOR_SAVE_ERROR_MSG);
        }

        if (proxyServer.getId() == null) {
            proxyRepository.findByIpAndPort(proxyServer.getIp(), proxyServer.getPort())
                    .ifPresent(ps -> proxyServer.setId(ps.getId()));
        }

        return proxyRepository.save(proxyServer);
    }

    /**
     * Find any unattached proxy server
     *
     * @return optional of proxy server
     */
    public Optional<ProxyServer> findAnyUnattached() {
        return proxyRepository.findFirstByProducingChannelIdIsNull();
    }

    /**
     * Find all proxy servers
     *
     * @return list of proxy servers
     */
    public List<ProxyServer> findAll() {
        return proxyRepository.findAll();
    }

    /**
     * Delete entity by id
     *
     * @param id id
     */
    public void removeById(String id) {
        if (id == null) {
            return;
        }

        proxyRepository.deleteById(id);
    }

    /**
     * Format full proxy address from ProxyServer
     *
     * @param proxyServer proxy server entity
     * @return full proxy address
     */
    public String getFullProxyAddress(ProxyServer proxyServer) {
        if (proxyServer == null) {
            throw new IllegalArgumentException(PROXY_SERVER_IS_NULL_ERROR_MSG);
        }

        return String.format(PROXY_SERVER_ADDRESS_FORMAT, proxyServer.getIp(), proxyServer.getPort());
    }

    /**
     * Attach new proxy server to producing channel
     *
     * @param producingChannel producing channel entity
     */
    public void attachNewProxy(ProducingChannel producingChannel) {
        ProxyServer unattachedProxyServer = this.findAnyUnattached()
                .orElseThrow(() -> new ProxyException(NOT_FOUND_UNATTACHED_PROXY_SERVER_ERROR_MSG));

        ProxyServer lastProxyServer = producingChannel.getProxyServer();
        this.removeById(lastProxyServer.getId());

        producingChannel.setProxyServer(unattachedProxyServer);
        unattachedProxyServer.setProducingChannelId(producingChannel.getId());
        this.save(unattachedProxyServer);
        producingChannelService.save(producingChannel);
    }
}
