package com.example.caustudy.ui.searchstudy;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    private ArrayList<Data> listData = new ArrayList<>();
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study, parent, false);
        return new ItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나씩 출력
        holder.onBind(listData.get(position));

    }

    @Override
    public int getItemCount() {
        // Item 수
        return listData.size();
    }

    public void addItem(Data data) {
        listData.add(data);
    }

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView period;
        private TextView time;
        private TextView leader;
        private TextView org;
        private TextView info;

        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);
            period = itemView.findViewById(R.id.item_period);
            time = itemView.findViewById(R.id.item_time);
            leader = itemView.findViewById(R.id.item_leader);
            org = itemView.findViewById(R.id.item_org);
            info = itemView.findViewById(R.id.item_info);

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

        void onBind(Data data) {
            title.setText(data.getTitle());
            period.setText(data.getPeriod());
            time.setText(data.getTime());
            leader.setText(data.getLeader());
            org.setText(data.getOrg());
            info.setText(data.getInfo());
        }
    }
}

class Data {

    private String title, period, time, leader, org, info;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
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