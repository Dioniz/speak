package com.diodev.speak.models;

import java.util.Date;

public class Message {

    public static final int TYPE_MESSAGE = 0;
    public static final int TYPE_MESSAGE_OWN = 1;

    public static final int TYPE_LOG = 2;
    public static final int TYPE_ACTION = 3;

    private String roomId;
    private String username;
    private String message;
    private Date date;
    private int type;

    public Message(String roomId, String username, String message, Date date, int type) {
        this.roomId = roomId;
        this.username = username;
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String essage) {
        message = essage;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
