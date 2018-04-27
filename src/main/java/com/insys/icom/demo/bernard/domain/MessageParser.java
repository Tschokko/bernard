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
            throw new InvalidMessageException();
        if (!(raw.get(0) instanceof Double))
            throw new InvalidMessageException();
        Double messageType = (Double)raw.get(0);
        if (MessageType.valueOf(messageType.intValue()) == null)
            throw new InvalidMessageException();

        return new MessageParser(raw);
    }

    public MessageType getMessageType() {
        Double messageType = (Double)raw.get(0);
        return MessageType.valueOf(messageType.intValue());
    }

    public HelloMessage getHelloMessage() {
        if (getMessageType() != MessageType.HELLO)
            throw new InvalidMessageException();
        return HelloMessage.builder().realm((String)raw.get(1)).build();
    }
}
