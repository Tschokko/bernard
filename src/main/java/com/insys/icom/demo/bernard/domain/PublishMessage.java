package com.insys.icom.demo.bernard.domain;

import com.google.gson.annotations.SerializedName;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishMessage implements Message {
    private Long requestId;
    private String topic;

    @Override
    public MessageType getMessageType() {
        return MessageType.PUBLISH;
    }

    @Override
    public List toList() {
        return null;
    }

    @Override
    public String toJson() {
        return null;
    }

    @Data
    @Builder
    public static class EventArguments {
        @SerializedName("device_id")
        private String deviceId;
        private String event;
        private OffsetDateTime timestamp;
    }
}
