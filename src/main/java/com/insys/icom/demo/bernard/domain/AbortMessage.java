package com.insys.icom.demo.bernard.domain;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * @author Lars Gielsok, MaibornWolff GmbH
 */
@Data
@Builder
public class AbortMessage implements Message {
    public final static String ERR_INVALID_SESSION = "ERR_INVALID_SESSION";
    public final static String ERR_PROTOCOL_VIOLATION = "ERR_PROTOCOL_VIOLATION";
    public final static String ERR_NO_SUCH_REALM = "ERR_NO_SUCH_REALM";

    private String reason;
    private Details details;

    @Override
    public MessageType getMessageType() {
        return MessageType.ABORT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List toList() {
        List list = new ArrayList();
        list.add(getMessageType().getValue());
        list.add(reason);
        list.add(details);
        return list;
    }

    @Override
    public String toJson() {
        return new Gson().toJson(toList());
    }

    @Data
    @Builder
    public static class Details {
        private String message;
    }
}
