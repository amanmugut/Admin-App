package com.example.admin;

public class TeacherData {
    private String name,email,post,image,uniquekey;

    public TeacherData() {
    }

    public TeacherData(String name, String email, String post, String image, String uniquekey) {
        this.name = name;
        this.email = email;
        this.post = post;
        this.image = image;
        this.uniquekey = uniquekey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUniquekey() {
        return uniquekey;
    }

    public void setUniquekey(String uniquekey) {
        this.uniquekey = uniquekey;
    }
}
