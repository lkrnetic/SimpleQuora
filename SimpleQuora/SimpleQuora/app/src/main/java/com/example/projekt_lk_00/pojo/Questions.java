package com.example.projekt_lk_00.pojo;

import java.util.List;

public class Questions {

    private Question[] questions;
    private String status;
    public Questions(Question[] questions, String status){
        this.questions = questions;
        this.status = status;
    }
    public Question[] getQuestions() {
        return questions;
    }

    public void setQuestion(Question[] questions) {
        this.questions = questions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
