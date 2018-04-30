package com.insys.icom.demo.bernard.domain;

import com.google.gson.Gson;

import com.insys.icom.demo.bernard.commons.exceptions.InvalidMessageException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
public class MessageParser {
    private List raw;

    private MessageParser(List raw) {
        this.raw = raw;
    }

    public static MessageParser fromJson(String json) {
        List raw = new Gson().fromJson(json, List.class);

        if (raw == null || raw.size() == 0)
            throw new InvalidMessageException("Error parsing message. Expected a JSON list.");
        if (!(raw.get(0) instanceof Double))
            throw new InvalidMessageException("Error parsing message. Expected a message type as first element.");
        Double messageType = (Double)raw.get(0);
        if (MessageType.valueOf(messageType.intValue()) == null)
            throw new InvalidMessageException("Error parsing message. Expected a message type of type int.");

        return new MessageParser(raw);
    }

    public MessageType getMessageType() {
        Double messageType = (Double)raw.get(0);
        return MessageType.valueOf(messageType.intValue());
    }

    public HelloMessage getHelloMessage() {
        if (getMessageType() != MessageType.HELLO)
            throw new InvalidMessageException("Error parsing message. Expected a HELLO message.");
        return HelloMessage.builder().realm((String)raw.get(1)).build();
    }

    public PublishMessage getPublishMessage() {
        if (getMessageType() != MessageType.PUBLISH)
            throw new InvalidMessageException("Error parsing message. Expected a PUBLISH message.");
        if (!(raw.get(1) instanceof Double))
            throw new InvalidMessageException("Error parsing PUBLISH message. Expected id value for requestId.");
        if (!(raw.get(2) instanceof String))
            throw new InvalidMessageException("Error parsing PUBLISH message. Expected string value for topic.");
        Double requestId = (Double)raw.get(1);
        String topic = (String)raw.get(2);
        return PublishMessage.builder().requestId(requestId.longValue()).topic(topic).build();
    }
}
