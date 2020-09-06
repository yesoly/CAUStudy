package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class AttendantActivity extends AppCompatActivity {
    private AttendantAdapter adapter;
    List<String> listName = new ArrayList<>();
    List<String> listEmail = new ArrayList<>();
    List<String> list_L = new ArrayList<>();
    List<String> list_S = new ArrayList<>();
    ArrayList<String> memList = new ArrayList<String>();
    private String study_key, email, name, l_dept, s_dept;
    RecyclerView recyclerView;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendant);
        recyclerView = findViewById(R.id.member_view);

        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        get_member();
    }

    public void get_member(){
        studyRef.child(study_key).child("member_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    memList.add(ds.getKey());
                }
                System.out.println(memList.size());
                set_adaptor();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

    }
    public String get_today(){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String date = format.format(Calendar.getInstance().getTime());
        return date;
    }

    public void set_adaptor(){
        String today = get_today();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AttendantActivity.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new AttendantAdapter();
        adapter.setOnItemClickListener(new AttendantAdapter.OnItemClickListener() {
            @Override
            public void onAttendClick(View v, int position) {
                String get_email = listEmail.get(position);
                StringTokenizer id_token = new StringTokenizer(get_email, "@");
                final String id = id_token.nextToken();
                studyRef.child(study_key).child("schedule").child(today).child(id).setValue(1);
                Toast.makeText(AttendantActivity.this, "출석 처리되었습니다.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onDelayClick(View v, int position) {
                String get_email = listEmail.get(position);
                StringTokenizer id_token = new StringTokenizer(get_email, "@");
                final String id = id_token.nextToken();
                studyRef.child(study_key).child("schedule").child(today).child(id).setValue(2);
                Toast.makeText(AttendantActivity.this, "지각 처리되었습니다.", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onAbsentClick(View v, int position) {
                String get_email = listEmail.get(position);
                StringTokenizer id_token = new StringTokenizer(get_email, "@");
                final String id = id_token.nextToken();
                studyRef.child(study_key).child("schedule").child(today).child(id).setValue(3);
                Toast.makeText(AttendantActivity.this, "결석 처리되었습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
        for(int i = 0; i < memList.size(); i++){
            String mem_id = memList.get(i);
            Log.d(mem_id, mem_id);
            userRef.child(mem_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null){
                        listEmail.clear();
                        listName.clear();
                        list_L.clear();
                        list_S.clear();
                    }

                    Log.d("ds : ",dataSnapshot.getKey());
                    email = dataSnapshot.child("email").getValue().toString();
                    name = dataSnapshot.child("username").getValue().toString();
                    l_dept = dataSnapshot.child("L_deptname").getValue().toString();
                    s_dept = dataSnapshot.child("S_deptname").getValue().toString();
                    listEmail.add(email);
                    listName.add(name);
                    list_L.add(l_dept);
                    list_S.add(s_dept);

                    Item_attendant item = new Item_attendant();
                    item.setName(name);
                    item.setEmail(email);
                    item.setL_dept(l_dept);
                    item.setS_dept(s_dept);
                    adapter.addItem(item);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }
}
