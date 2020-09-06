package com.example.caustudy;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.Models.Post;

import java.util.ArrayList;
import java.util.List;

public class PostAdaptor extends RecyclerView.Adapter<PostAdaptor.PostViewHolder> {

    private ArrayList<Post> datas = new ArrayList<>();

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.onBind(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void addItem(Post post) {
        datas.add(post);
    }

    private OnItemClickListener mListener = null ;

    public interface OnItemClickListener {
        void onItemClick(View v, int position) ;
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener ;
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView user_name;
        private TextView contents;
        private TextView date;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.item_post_user_name);
            title = itemView.findViewById(R.id.item_post_title);
            contents = itemView.findViewById(R.id.item_post_contents);
            date = itemView.findViewById(R.id.item_post_date);

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
        void onBind(Post post) {
            title.setText(post.getTitle());
            user_name.setText(post.getUid());
            contents.setText(post.getContents());
            date.setText(post.getDate());
        }
    }
}
/*
class Post2 {
    private String title, username, contents, date;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = "제목: "+title;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = "작성자: "+ username;
    }

    public String getContents() {
        return contents;
    }
    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = "작성일: "+date;
    }

 */