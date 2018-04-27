package com.insys.icom.demo.bernard.facade;

import com.google.common.base.Strings;

import org.springframework.web.socket.WebSocketSession;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
@Data
@Builder
public class WebSocketSessionInfo {
    private WebSocketSession session;
    private String realm;
    @Builder.Default private int sessionTimeout = 120;
    @Builder.Default private OffsetDateTime lastConnected = OffsetDateTime.now();

    public boolean hasRealm() {
        return !Strings.isNullOrEmpty(realm);
    }
}
