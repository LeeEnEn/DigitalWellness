package com.example.digitalwellness;

public class Friend {

    String uid, name, url;

    public Friend(String uid, String name, String url) {
        this.uid = uid;
        this.name = name;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
