package com.example.web_socket.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @ClassName  ChatMessage
 * @Description
 * @author  lixueyun
 * @Date  2019/2/28 18:49
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
public class ChatMessage {

    private String content;
    private String sender;
    private String receiver;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }


}
