package com.insys.icom.demo.bernard.facade;

import com.insys.icom.demo.bernard.domain.AbortMessage;
import com.insys.icom.demo.bernard.domain.HelloMessage;
import com.insys.icom.demo.bernard.domain.MessageParser;
import com.insys.icom.demo.bernard.domain.MessageType;
import com.insys.icom.demo.bernard.domain.WelcomeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private List<WebSocketSessionInfo> sessions = new CopyOnWriteArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        log.debug("Received data: [" + message.getPayload() + "]");

        MessageParser messageParser = MessageParser.fromJson(message.getPayload());

        if (messageParser.getMessageType() == MessageType.HELLO) {
            HelloMessage helloMessage = messageParser.getHelloMessage();

            sessions.forEach(sessionInfo -> {
                if (sessionInfo.getSession() == session) {
                    sessionInfo.setRealm(helloMessage.getRealm());
                }
            });

            String response = WelcomeMessage.builder()
                    .sessionId(new Long(session.getId()))
                    .details(WelcomeMessage.Details.builder()
                            .sessionTimeout(120)
                            .eventsTopic("events::devices")
                            .build())
                    .build()
                    .toJson();
            session.sendMessage(new TextMessage(response));
            log.debug("Send data: " + response);
        }
        else {
            String response = AbortMessage.builder()
                    .reason(AbortMessage.ERR_PROTOCOL_VIOLATION)
                    .details(AbortMessage.Details.builder()
                            .message("Expected a HELLO message first")
                            .build())
                    .build()
                    .toJson();
            session.sendMessage(new TextMessage(response));
            log.debug("Send data: " + response);
        }

    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        //the messages will be broadcasted to all users.
        sessions.add(WebSocketSessionInfo.builder().session(session).build());
        log.debug("Session started: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        //super.afterConnectionClosed(session, status);
        sessions.forEach(sessionInfo -> {
            if (sessionInfo.getSession() == session) {
                sessions.remove(sessionInfo);
            }
        });

        log.debug("Session terminated: " + session.getId() + ", Reason: " + status.getReason());
    }
}
