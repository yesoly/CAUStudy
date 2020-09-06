package com.example.caustudy.MemberManagement;

public class RatingHistoryViewItem {
    private String name, email, l_dept, s_dept;
    private String rating;
    private String feedback_text;
    private String study_name;

    public String getRate() {
        return rating;
    }
    public String getFeedback() {
        return feedback_text;
    }
    public String getStudyName() {
        return study_name;
    }
    public void setStudyName(String name) {
        study_name = name;
    }
    public void setRate(String rate) {
        rating = rate;
    }
    public void setFeedback(String feedback) {
        feedback_text = feedback;
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

    public String getL_dept() {
        return l_dept;
    }
    public void setL_dept(String l_dept) {
        this.l_dept = l_dept;
    }

    public String getS_dept() {
        return s_dept;
    }
    public void setS_dept(String s_dept) {
        this.s_dept = s_dept;
    }
}
