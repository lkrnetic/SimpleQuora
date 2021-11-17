package com.example.projekt_lk_00.pojo;

public class Question {
    private String question_text;
    private String user_id;
    private String id;
    public Question(String id, String user_id, String question_text){
        this.id = id;
        this.user_id = user_id;
        this.question_text = question_text;
    }
    public String getQuestion_text() {
        return question_text;
    }

    public void setQuestion_text(String question_text) {
        this.question_text = question_text;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
