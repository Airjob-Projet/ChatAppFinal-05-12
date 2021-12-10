package com.airjob.chatappfinal_05_12.model;

import java.util.List;

public class ChatlistModel {
    public List<String> id;

    public ChatlistModel() {
    }

    public ChatlistModel(List<String> id) {
        this.id = id;
    }

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }
}
