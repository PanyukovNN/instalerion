package org.union.instalerion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.union.common.model.ProxyServer;
import org.union.common.service.ProxyService;

import java.util.List;

/**
 * Controller for proxy servers
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/proxy-server")
public class ProxyServerController {

    private final ProxyService proxyService;

    @GetMapping("/all")
    public List<ProxyServer> getAllProxyServers() {
        return proxyService.findAll();
    }

    @PostMapping("/add-list")
    public String saveProxyServer(@RequestBody List<ProxyServer> proxyServers) {
        proxyServers.forEach(proxyService::save);

        //TODO move messages to constants
        return "Proxy servers saved successfully";
    }

    @GetMapping("/remove")
    public String removeById(@RequestParam String id) {
        proxyService.removeById(id);

        return "Proxy server removed successfully";
    }
}
