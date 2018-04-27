package com.insys.icom.demo.bernard.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
@Data
@Builder
public class HelloMessage implements Message {
    private String realm;

    @Override
    public MessageType getMessageType() {
        return MessageType.HELLO;
    }

    @Override
    public List toList() {
        return null;
    }

    @Override
    public String toJson() {
        return null;
    }
}
