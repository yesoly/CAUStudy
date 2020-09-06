package com.example.caustudy;

public class StudyModel {

    public String study_name = "";
    public String L_category = "";
    public String S_category = "";
    public String leader_name = "";
    public String leader_email = "";
    public String organization = "";
    public String info = "";
    public String study_day;
    public String study_time = "";
    public String s_period = "";
    public String e_period = "";
    public StudyModel() {

    }
    public StudyModel(String study_name, String organization, String L_category,String S_category,String leader_name, String leader_email, String info, String s_period,String e_period, String study_day, String study_time) {
        this.study_name = study_name;
        this.L_category = L_category;
        this.S_category = S_category;
        this.leader_name = leader_name;
        this.leader_email = leader_email;
        this.organization = organization;
        this.info = info;
        this.s_period = s_period;
        this.e_period = e_period;
        this.study_day = study_day;
        this.study_time = study_time;
    }
}