package com.airjob.chatappfinal_05_12.Model;

import com.google.firebase.firestore.Exclude;

public class ChatlistModel {
    public String id;

    public ChatlistModel() {
    }

    public ChatlistModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
