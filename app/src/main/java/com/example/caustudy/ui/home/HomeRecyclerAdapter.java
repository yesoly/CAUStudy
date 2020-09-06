package com.example.caustudy.ui.home;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;

import java.util.ArrayList;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<com.example.caustudy.ui.home.HomeRecyclerAdapter.ItemViewHolder> {

    private ArrayList<com.example.caustudy.ui.home.HomeData> listData = new ArrayList<>();
    @NonNull
    @Override
    public com.example.caustudy.ui.home.HomeRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home, parent, false);
        return new com.example.caustudy.ui.home.HomeRecyclerAdapter.ItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull com.example.caustudy.ui.home.HomeRecyclerAdapter.ItemViewHolder holder, int position) {
        // Item을 하나씩 출력
        Log.d("Adapter, onBindViewHolder",String.valueOf(position));
        holder.onBind(listData.get(position));

    }

    @Override
    public int getItemCount() {
        // Item 수
        return listData.size();
    }

    public void addItem(com.example.caustudy.ui.home.HomeData data) {
        listData.add(data);
    }

    private com.example.caustudy.ui.home.HomeRecyclerAdapter.OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(com.example.caustudy.ui.home.HomeRecyclerAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView progress;
        private TextView next_time;
        private TextView next_location;
        private TextView next_assignment;
        private TextView recent_notice;
        private TextView title;

        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);
            progress = itemView.findViewById(R.id.view_next_time);
            next_time = itemView.findViewById(R.id.view_next_time);
            next_location = itemView.findViewById(R.id.view_next_location);
            next_assignment = itemView.findViewById(R.id.view_next_assignment);
            recent_notice = itemView.findViewById(R.id.view_recent_notice);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onItemClick(v, pos) ;
                        }
                    }
                }
            });
        }

        void onBind(com.example.caustudy.ui.home.HomeData data) {
            title.setText(data.title);
            progress.setText("test");
            next_time.setText(data.next_time.substring(0,data.next_time.length()-1));
            next_location.setText(data.next_location);
            next_assignment.setText(data.next_assignment);
            recent_notice.setText(data.recent_notice);
        }
    }
}

class HomeData {

    public String title, period, time, leader, org, info;
    public String next_location, next_time, next_assignment, recent_notice;

    public HomeData () {

    }
    public HomeData(String title, String period, String time, String leader, String org, String info) {
        this.title = title;
        this.period = title;
        this.time = time;
        this.leader = leader;
        this.org = org;
        this.info = info;
    }


    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {return next_location;}

    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = "진행 기간: "+ period;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = "시간: "+ time;
    }

    public String getLeader() {
        return leader;
    }
    public void setLeader(String leader) {
        this.leader = "스터디장 이메일: "+leader;
    }

    public String getOrg() {
        return org;
    }
    public void setOrg(String org) {
        this.org = "소속: "+ org;
    }

    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = "소개: "+info;
    }
}