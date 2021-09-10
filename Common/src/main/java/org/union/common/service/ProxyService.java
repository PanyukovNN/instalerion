package org.union.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.union.common.model.ProxyServer;
import org.union.common.repository.ProxyServerRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service for working with proxy service
 */
@Service
@RequiredArgsConstructor
public class ProxyService {

    private final ProxyServerRepository proxyRepository;

    /**
     * Save a proxy server
     *
     * @param proxyServer proxy server
     */
    public ProxyServer save(ProxyServer proxyServer) {
        if (proxyServer == null) {
            //TODO throw exception
            return null;
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
        proxyRepository.deleteById(id);
    }
}
