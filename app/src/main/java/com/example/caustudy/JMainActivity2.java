package com.example.caustudy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.Models.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JMainActivity2 extends AppCompatActivity implements View.OnClickListener {

    public static Context mContext;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    private RecyclerView mPostRecyclerView;
    private PostAdaptor mAdapter;
    List<String> listKey = new ArrayList<>();
    List<String> listTitle = new ArrayList<>();
    List<String> listUser = new ArrayList<>();
    List<String> listContent = new ArrayList<>();
    List<String> listDate = new ArrayList<>();
    private String study_key, study_name, email, name, id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        study_name = intent.getStringExtra("study_name");
        setContentView(R.layout.activity_j_main);
        mPostRecyclerView = findViewById(R.id.main_recyclerview);
        findViewById(R.id.jmain_post_edit).setOnClickListener(this);
        set_listview();



    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, PostActivity2.class);
        intent.putExtra("study_key", study_key );
        intent.putExtra("study_name", study_name );
        startActivity(intent);

    }

    void set_listview(){
        mAdapter = new PostAdaptor();
        Log.d("set_listview","Started");
        mPostRecyclerView.setAdapter(mAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(JMainActivity2.this);
        mPostRecyclerView.setLayoutManager(linearLayoutManager);

        studyRef.child(study_key).child("assignment_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    listKey.clear();
                    listTitle.clear();
                    listUser.clear();
                    listContent.clear();
                    listDate.clear();
                }
                for (DataSnapshot study : dataSnapshot.getChildren()) {
                    //title, user_name, contents, date
                    String key = study.getKey();
                    String title = study.child("title").getValue().toString();
                    String username = study.child("user_name").getValue().toString();
                    String contents = study.child("content").getValue().toString();
                    String date = study.child("date").getValue().toString();

                    listKey.add(key);
                    listTitle.add(title);
                    listUser.add(username);
                    listContent.add(contents);
                    listDate.add(date);
                }

                for (int i = 0; i < listTitle.size(); i++) {
                    // 각 List의 값들을 data 객체에 set 해줍니다.
                    Post post = new Post();
                    post.setTitle(listTitle.get(i));
                    post.setUid(listUser.get(i));
                    post.setContents(listContent.get(i));
                    post.setDate(listDate.get(i));
                    mAdapter.addItem(post);
                }

                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // adapter의 값이 변경되었다는 것을 알려줍니다.
        mAdapter.setOnItemClickListener(new PostAdaptor.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Intent intent = new Intent(mContext, PostViewActivity2.class);
                intent.putExtra("study_key",study_key);
                intent.putExtra("title", listTitle.get(position) );
                intent.putExtra("content", listContent.get(position));
                intent.putExtra("uid",listUser.get(position));
                startActivity(intent);
            }
        });
        mAdapter.notifyDataSetChanged();
    }
}
