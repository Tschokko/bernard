package com.insys.icom.demo.bernard.facade;

import com.google.common.base.Strings;

import org.springframework.web.socket.WebSocketSession;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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

    public static Optional<WebSocketSessionInfo> findBySession(List<WebSocketSessionInfo> sessionInfoList, WebSocketSession session) {
        for (WebSocketSessionInfo sessionInfo : sessionInfoList) {
            if (sessionInfo.getSession() != null && sessionInfo.getSession().getId().equals(session.getId()))
                return Optional.of(sessionInfo);
        }
        return Optional.empty();
    }
}
