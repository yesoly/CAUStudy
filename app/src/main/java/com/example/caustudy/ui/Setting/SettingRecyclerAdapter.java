package com.example.caustudy.ui.Setting;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;

import java.util.ArrayList;

public class SettingRecyclerAdapter extends RecyclerView.Adapter<com.example.caustudy.ui.Setting.SettingRecyclerAdapter.ItemViewHolder> {

    private ArrayList<com.example.caustudy.ui.Setting.SettingData> listData = new ArrayList<>();
    @NonNull
    @Override
    public com.example.caustudy.ui.Setting.SettingRecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting, parent, false);
        return new com.example.caustudy.ui.Setting.SettingRecyclerAdapter.ItemViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull com.example.caustudy.ui.Setting.SettingRecyclerAdapter.ItemViewHolder holder, int position) {
        // Item을 하나씩 출력
        Log.d("Adapter, onBindViewHolder",String.valueOf(position));
        holder.onBind(listData.get(position));
    }

    @Override
    public int getItemCount() {
        // Item 수
        return listData.size();
    }

    public void addItem(com.example.caustudy.ui.Setting.SettingData data) {
        listData.add(data);
    }

    private com.example.caustudy.ui.Setting.SettingRecyclerAdapter.OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(com.example.caustudy.ui.Setting.SettingRecyclerAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView title;


        ItemViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.item_title);


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

        void onBind(com.example.caustudy.ui.Setting.SettingData data) {
            title.setText(data.getTitle());

        }
    }
}

class SettingData {

    private String title, period, time, leader, org, info;

    public SettingData(String title) {
        this.title = title;


    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

}