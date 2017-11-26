package com.example.servicetransfer.web.ssl;

import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import java.security.Principal;

@Slf4j
public class WhitelistVerifier implements HostnameVerifier {
    @Override
    public boolean verify(String s, SSLSession sslSession) {
        try {
            Principal peerPrincipal = sslSession.getPeerPrincipal();
            String peerHost = sslSession.getPeerHost();
            String name = peerPrincipal.getName();
            log.info("peer=" + name + "@" + peerHost);
            return peerPrincipal != null;
        } catch (SSLPeerUnverifiedException e) {
            log.info(e.getMessage());
            return false;
        }
    }
}
