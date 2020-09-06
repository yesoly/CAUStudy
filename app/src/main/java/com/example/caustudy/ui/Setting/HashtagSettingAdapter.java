package com.example.caustudy.ui.Setting;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.R;

import java.util.ArrayList;

public class HashtagSettingAdapter extends RecyclerView.Adapter<HashtagSettingAdapter.ViewHolder> {

    private ArrayList<Item_Hashtag_Setting> listApplier = new ArrayList<>();
    @NonNull
    @Override
    public com.example.caustudy.ui.Setting.HashtagSettingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hashtag, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull com.example.caustudy.ui.Setting.HashtagSettingAdapter.ViewHolder holder, int position) {
        // Item을 하나씩 출력
        holder.onBind(listApplier.get(position));

    }

    @Override
    public int getItemCount() {
        // Item 수
        return listApplier.size();
    }

    public void addItem(Item_Hashtag_Setting item) {
        listApplier.add(item);
    }

    private com.example.caustudy.ui.Setting.HashtagSettingAdapter.OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onDeleteClick(View v, int position);
    }
    public void setOnItemClickListener(com.example.caustudy.ui.Setting.HashtagSettingAdapter.OnItemClickListener listener) {
        this.mListener = listener ;
    }


    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView email;
        private ImageView accept;
        private ImageView delete;

        ViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.item_name);
            email = itemView.findViewById(R.id.item_email);
            delete = itemView.findViewById(R.id.apply_delete);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition() ;
                    if (pos != RecyclerView.NO_POSITION) {
                        // 리스너 객체의 메서드 호출.
                        if (mListener != null) {
                            mListener.onDeleteClick(v, pos) ;
                            listApplier.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    }
                }
            });
        }

        void onBind(Item_Hashtag_Setting item) {
            name.setText(item.getName());
        }
    }
}
class Item_Hashtag_Setting {
    private String name, email;

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
}