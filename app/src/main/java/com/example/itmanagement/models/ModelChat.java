package com.example.itmanagement.models;

public class ModelChat {
    String message, receiver, sender, timestamp;
    boolean isSeen;

    public ModelChat() {
    }


    public ModelChat(String message, String receiver, String sender, String timestamp, boolean isSeen) {
        this.message = message;
        this.receiver = receiver;
        this.sender = sender;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }

    public String getSender() {
        return sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSeen() {
        return isSeen;
    }
}
