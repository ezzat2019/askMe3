package com.example.programmer.askme;

public class UserItem {
    private String profile_image, name;

    public UserItem() {
    }

    public UserItem(String profile_image, String name) {
        this.profile_image = profile_image;
        this.name = name;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
