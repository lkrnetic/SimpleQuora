package com.example.projekt_lk_00.pojo;

public class User {

    private String password;
    private String username;
    private int status;
    private int id;
    public User(int id, String username, String password, int status){
        this.id = id;
        this.username = username;
        this.password = password;
        this.status = status;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
