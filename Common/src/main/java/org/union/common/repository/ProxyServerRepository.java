package org.union.common.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.union.common.model.ProxyServer;

import java.util.Optional;

/**
 * Proxy servers repository
 */
@Repository
public interface ProxyServerRepository extends MongoRepository<ProxyServer, String> {

    Optional<ProxyServer> findFirstByProducingChannelIdIsNull();

    Optional<ProxyServer> findByIpAndPort(String ip, int port);
}
