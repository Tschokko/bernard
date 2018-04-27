package com.insys.icom.demo.bernard.domain;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
public enum MessageType {
    HELLO(1),
    WELCOME(2),
    ABORT(3),
    PING(4);

    @Getter
    private int value;

    private static Map<Integer, MessageType> map = new HashMap<>();

    MessageType(int value) {
        this.value = value;
    }

    static {
        for (MessageType messageType : MessageType.values()) {
            map.put(messageType.value, messageType);
        }
    }

    public static MessageType valueOf(int messageType) {
        return map.get(messageType);
    }
}
