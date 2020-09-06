package com.example.caustudy.ui.Setting;

import com.example.caustudy.R;

public class Setting_SingerItem {
    private String title;
    private String period;
    private String time;
    private String organization;
    private int image;

    public Setting_SingerItem() {

    }

    public Setting_SingerItem(String title, String period, String time, String organization) {
        this.title = title;
        this.image = R.drawable.ic_group_black_24dp;
    }


    public String getTitle() {return this.title;}
    public String getPeriod() {return this.period;}
    public String getTime() {return this.time;}
    public String getOrganization() {return this.organization;}
    public int getImage() {
        return image;
    }
}
