package com.insys.icom.demo.bernard.facade;

import com.insys.icom.demo.bernard.commons.exceptions.InvalidMessageException;
import com.insys.icom.demo.bernard.commons.exceptions.ThrowingTechnicalException;
import com.insys.icom.demo.bernard.domain.AbortMessage;
import com.insys.icom.demo.bernard.domain.HelloMessage;
import com.insys.icom.demo.bernard.domain.Message;
import com.insys.icom.demo.bernard.domain.MessageParser;
import com.insys.icom.demo.bernard.domain.MessageType;
import com.insys.icom.demo.bernard.domain.PublishMessage;
import com.insys.icom.demo.bernard.domain.PublishedMessage;
import com.insys.icom.demo.bernard.domain.WelcomeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import javax.websocket.Session;

@Component
public class SocketHandler extends TextWebSocketHandler {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private List<WebSocketSessionInfo> sessions = new CopyOnWriteArrayList<>();

    private static <T extends WebSocketSessionInfo> Consumer<T> throwingTechnicalExceptionWrapper(ThrowingTechnicalException<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws InterruptedException, IOException {
        log.debug("Received data: [" + message.getPayload() + "]");


        try {
            MessageParser messageParser = MessageParser.fromJson(message.getPayload());

            // Find the session and if present continue processing, otherwise abort session
            Optional<WebSocketSessionInfo> sessionInfoOptional = WebSocketSessionInfo.findBySession(sessions, session);
            if (sessionInfoOptional.isPresent()) {
                WebSocketSessionInfo sessionInfo = sessionInfoOptional.get();

                // If session info has no realm, the session has to be initialized first.
                if (!sessionInfo.hasRealm()) {
                    // To initialize a session we expect a HELLO message first, otherwise abort session
                    if (messageParser.getMessageType() == MessageType.HELLO) {
                        HelloMessage helloMessage = messageParser.getHelloMessage();
                        if (realmExists(helloMessage.getRealm())) {
                            sessionInfo.setRealm(helloMessage.getRealm());
                            sendWelcomeMessage(session);
                        } else {
                            sendAbortMessage(session, AbortMessage.ERR_NO_SUCH_REALM, "Invalid or unknown realm.");
                        }
                    } else {
                        sendAbortMessage(session, AbortMessage.ERR_PROTOCOL_VIOLATION, "Expected a HELLO message first.");
                    }
                }
                else {
                    // Check for a valid message type, other abort the session
                    switch(messageParser.getMessageType()) {
                        case PING:
                        {
                            // Reset timeout by setting new last connected time
                            sessionInfo.setLastConnected(OffsetDateTime.now());
                            break;
                        }
                        case PUBLISH:
                        {
                            PublishMessage publishMessage = messageParser.getPublishMessage();
                            log.debug("Received publish message: topic=" + publishMessage.getTopic());
                            sendPublishedMessage(session, publishMessage.getRequestId());
                            break;
                        }
                        default:
                        {
                            sendAbortMessage(session, AbortMessage.ERR_PROTOCOL_VIOLATION, "Expected a valid message.");
                            break;
                        }
                    }
                }
            } else {
                sendAbortMessage(session, AbortMessage.ERR_INVALID_SESSION, "Invalid session.");
            }
        }
        catch (InvalidMessageException ex) {
            sendAbortMessage(session, AbortMessage.ERR_PROTOCOL_VIOLATION, ex.getMessage());
        }
    }

    private void sendMessage(WebSocketSession session, Message message) throws IOException {
        String json = message.toJson();
        session.sendMessage(new TextMessage(json));
        log.debug("Send message: "  + json);
    }

    private void sendAbortMessage(WebSocketSession session, String reason, String errorMessage) throws IOException {
        Message message = AbortMessage.builder()
                .reason(reason)
                .details(AbortMessage.Details.builder()
                        .message(errorMessage)
                        .build())
                .build();
        sendMessage(session, message);
        session.close(CloseStatus.PROTOCOL_ERROR);
    }

    private void sendWelcomeMessage(WebSocketSession session) throws IOException {
        Message message = WelcomeMessage.builder()
                .sessionId(new Long(session.getId()))
                .details(WelcomeMessage.Details.builder()
                        .sessionTimeout(120)
                        .eventsTopic("events::devices")
                        .build())
                .build();
        sendMessage(session, message);
    }

    private void sendPublishedMessage(WebSocketSession session, Long requestId) throws IOException {
        Message message = PublishedMessage.builder()
                .requestId(requestId)
                .publication(getRandomLong())
                .build();
        sendMessage(session, message);
    }

    private long getRandomLong() {
        long leftLimit = 1L;
        long rightLimit = Long.MAX_VALUE;
        return leftLimit + (long) (Math.random() * (rightLimit - leftLimit));
    }

    private boolean realmExists(String realm) {
        log.debug("Checking realm: " + realm);
        return realm.equals("1000@devices.iot.insys-icom.com") || realm.equals("2000@devices.iot.insys-icom.com");
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
