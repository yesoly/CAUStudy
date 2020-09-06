package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.caustudy.MemberManagement.Dialog_member_evaluation;
import com.example.caustudy.MemberManagement.MemberManagementActivity;
import com.example.caustudy.MemberManagement.MemberViewAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MakeTodoActivity extends AppCompatActivity {
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    String study_key;
    Button new_todo;
    List<String> listNum = new ArrayList<>();
    List<String> listTopic = new ArrayList<>();
    List<String> listTime = new ArrayList<>();
    List<String> listCheck = new ArrayList<>();
    TodoAdapter adapter;
    RecyclerView recyclerView;

    public static Context checkContext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_todo);
        checkContext = this;
        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        new_todo = findViewById(R.id.new_todo_btn);
        recyclerView = findViewById(R.id.todo_list);

        new_todo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_Todo dialog_todo = new Dialog_Todo(MakeTodoActivity.this);
                dialog_todo.callFunction(study_key);
            }
        });
        set_listview();

        adapter.setOnItemClickListener(new TodoAdapter.OnItemClickListener() {
            @Override
            public void onCheckClick(View v, int position, Button btn) {
                String pos = listNum.get(position);
                String chk = listCheck.get(position);
                if (chk == "1") {
                    studyRef.child(study_key).child("todo_list").child(pos).child("status").setValue("0").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MakeTodoActivity.this, "출석 취소", Toast.LENGTH_SHORT).show();
                            btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.colorPrimary));
                            listCheck.set(position, "0");
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
                else{
                    studyRef.child(study_key).child("todo_list").child(pos).child("status").setValue("1").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(MakeTodoActivity.this, "출석 성공", Toast.LENGTH_SHORT).show();
                            listCheck.set(position, "1");
                            btn.setBackgroundColor(btn.getContext().getResources().getColor(R.color.colorAccent));
                            adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }

    void set_listview(){
        adapter = new TodoAdapter();
        Log.d("set_listview","Started");
        recyclerView.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MakeTodoActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        studyRef.child(study_key).child("todo_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    listNum.clear();
                    listTopic.clear();
                    listTime.clear();
                    listCheck.clear();
                }
                Log.d("set_listview_test1",dataSnapshot.getKey());

                for (DataSnapshot study : dataSnapshot.getChildren()) {
                    Log.d("set_listview_test2",study.getKey());
                        String num = study.child("num").getValue().toString();
                        String topic = study.child("topic").getValue().toString();
                        String time = study.child("time").getValue().toString();
                        String status = study.child("status").getValue().toString();
                        listNum.add(num);
                        listTopic.add(topic);
                        listTime.add(time);
                        listCheck.add(status);
                }

                for (int i = 0; i < listNum.size(); i++) {
                    // 각 List의 값들을 data 객체에 set 해줍니다.
                    Todo data = new Todo();
                    data.setNum(listNum.get(i));
                    data.setTopic(listTopic.get(i));
                    data.setStatus(listCheck.get(i));
                    data.setTime(listTime.get(i));
                    adapter.addItem(data);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }
}
