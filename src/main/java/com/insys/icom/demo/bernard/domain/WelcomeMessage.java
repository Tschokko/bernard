package com.insys.icom.demo.bernard.domain;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Data
public class WelcomeMessage implements Message {
    private Long sessionId;
    private Details details;

    @Override
    public MessageType getMessageType() {
        return MessageType.WELCOME;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List toList() {
        List list = new ArrayList();
        list.add(getMessageType().getValue());
        list.add(sessionId);
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
        @SerializedName("session_timeout")
        private Integer sessionTimeout;
        @SerializedName("events_topic")
        private String eventsTopic;
    }
}
