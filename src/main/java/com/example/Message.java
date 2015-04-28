package com.example;

/**
 * Created by rafaelandara on 4/27/15.
 */

// Basic chat protocol message.

public class Message {

    public enum MessageType {
        HELLO, BYE, SEARCH, TEXT, ERROR, INVITE, ACCEPT, DENY
    }

    private String content;
    private String sender;
    private String receiver;
    private MessageType messageType;

    public Message() {

    }

    public Message(String sender, String receiver, String content, MessageType type) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.messageType = type;
    }

    public String getContent() {
        return this.content;
    }

    public String getSender() {
        return this.sender;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public void setContent(String message) {
        this.content = message;
    }

    public void setSender(String username) {
        this.sender = username;
    }

    public void setReceiver(String username) {
        this.receiver = username;
    }

    public void setMessageType(MessageType type) {
        this.messageType = type;
    }

    public boolean isEmpty() {
        return this.sender == null || this.receiver == null || this.messageType == null;
    }

}
