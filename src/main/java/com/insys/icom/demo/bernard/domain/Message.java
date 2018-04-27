package com.insys.icom.demo.bernard.domain;

import java.util.List;

public interface Message {
    MessageType getMessageType();
    List toList();
    String toJson();
}
