package com.example.digitalwellness;

public class User {
    private String username, email, url, uid;
    private boolean friend;


    public User(String username, String email, String url, String uid, boolean friend) {
        this.username = username;
        this.email = email;
        this.url = url;
        this.uid = uid;
        this.friend = friend;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return this.uid;
    }
}
