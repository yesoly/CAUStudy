package com.example.caustudy;

public class User {
    public String email = "";
    public String username = "";
    public String hashtag = "";
    public String L_deptname = "";
    public String S_deptname = "";
    public User() {

    }
    public User(String email, String username, String L_deptname, String S_deptname, String hashtag) {
        this.email = email;
        this.username = username;
        this.L_deptname = L_deptname;
        this.S_deptname = S_deptname;
        this.hashtag = hashtag;
    }
    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }
    public User(String email, String username, String L_deptname, String S_deptname) {
        this.email = email;
        this.username = username;
        this.L_deptname = L_deptname;
        this.S_deptname = S_deptname;
    }
}