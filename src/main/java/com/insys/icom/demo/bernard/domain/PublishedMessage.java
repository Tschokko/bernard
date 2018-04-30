package com.insys.icom.demo.bernard.domain;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PublishedMessage implements Message {
    private Long requestId;
    private Long publication;

    @Override
    public MessageType getMessageType() {
        return MessageType.PUBLISHED;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List toList() {
        List list = new ArrayList();
        list.add(getMessageType().getValue());
        list.add(requestId);
        list.add(publication);
        return list;
    }

    @Override
    public String toJson() {
        return new Gson().toJson(toList());
    }
}
