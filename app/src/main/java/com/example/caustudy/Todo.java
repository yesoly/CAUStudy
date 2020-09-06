package com.example.caustudy;

class Todo {
    private String num = "";
    private String topic = "";
    private String time = "";
    private String status = "";

    public Todo() {

    }
    public Todo(String num, String topic, String time, String status) {
        this.num = num;
        this.topic = topic;
        this.time = time;
        this.status = status;
    }

    public String getNum() {
        return num;
    }
    public void setNum(String num) {
        this.num = num + "주차";
    }

    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = "주제: "+ topic;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = "시간: "+ time;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}