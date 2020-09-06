package com.example.caustudy.ui.MyStudy;

import android.widget.ImageView;

import com.example.caustudy.R;

public class MyStudy_SingerItem {
    private String title;
    private String period;
    private String time;
    private String organization;
    private int image;

    public MyStudy_SingerItem() {

    }

    public MyStudy_SingerItem(String title, String period, String time, String organization) {
        this.title = title;
        this.period = period;
        this.time = time;
        this.organization = organization;
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
