package com.example.caustudy.ui.Setting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.caustudy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

// 설정 - 관심해쉬태그 설정 액티비티


public class Setting_HashtagActivity extends AppCompatActivity {
    List<String> listTag = new ArrayList<>();

    RecyclerView recyclerView;
    private HashtagSettingAdapter adapter;
    String number;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("StudyList");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    private Button add_tag;
    public static Context mContext;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_hashtag);
        add_tag = findViewById(R.id.btn_add_hashtag);
        Intent intent = getIntent();
        recyclerView = findViewById(R.id.hashtag_list);
        mContext = this;

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(Setting_HashtagActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);


        show_data();

        // 새로운 태그 추가 버튼. 여기서 notifyDataSetChanged() 가 제대로 안먹히는 문제 있음
        add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_Hashtag dialog_hashtag = new Dialog_Hashtag(Setting_HashtagActivity.this);
                dialog_hashtag.callFunction();
            }

        });
    }

    public void show_data(){
        adapter = new HashtagSettingAdapter();
        recyclerView.setAdapter(adapter);
        // DB 불러와서 RecyclerView에 띄우
        userRef.child(user_id).child("hashtag").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String tagName = ds.getKey();
                    Item_Hashtag_Setting item = new Item_Hashtag_Setting();
                    item.setName(tagName);
                    adapter.addItem(item);
                    adapter.notifyDataSetChanged();
                    listTag.add(tagName);
                    //get_study_info(L_cate, S_cate);
                    Log.d("MyStudyFragment:check_mystudy_list: ",String.valueOf(number));
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError databaseError) {

            }
        });
        // 삭제 버튼 구현. 이거도 이상한게, 여러개 연속해서 삭제할때에 제대로 작동안함
        adapter.setOnItemClickListener(new HashtagSettingAdapter.OnItemClickListener() {
            @Override
            public void onDeleteClick(View v, int position) {
                String tagName = listTag.get(position);
                userRef.child(user_id).child("hashtag").child(tagName).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        adapter.notifyDataSetChanged();
                    }
                });
                adapter.notifyDataSetChanged();
            }
        });
    }
}

