package com.example.projekt_lk_00.pojo;

public class Answer {
    private String answer_text;
    private String username;
    private String user_id;
    private String question_id;
    public Answer(String username, String answer_text, String user_id, String question_id){
        this.username = username;
        this.answer_text = answer_text;
        this.user_id = user_id;
        this.question_id = question_id;
    }
    public String getAnswer_text() {
        return answer_text;
    }

    public void setAnswer_text(String answer_text) {
        this.answer_text = answer_text;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }
}
