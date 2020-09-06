package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.caustudy.MemberManagement.ApplyViewAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class SettingStudyActivity extends AppCompatActivity {

    List<String> listName = new ArrayList<>();
    List<String> listEmail = new ArrayList<>();
    List<String> list_L = new ArrayList<>();
    List<String> list_S = new ArrayList<>();

    Switch fin;
    RecyclerView recyclerView;
    private ApplyViewAdapter adapter;
    private String study_key, study_name, email, name, l_dept, s_dept;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");

    private Button setNextSchedule, editMarkdown, schedule_btn;
    private TextView next_location_view, next_time_view;
    String next_location;
    String next_time;
    public static Context mContext;


    class SwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                studyRef.child(study_key).child("apply_status").setValue(1);
            else
                studyRef.child(study_key).child("apply_status").removeValue();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_study);
        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        study_name = intent.getStringExtra("study_name");
        next_location_view = findViewById(R.id.nextLocationView);
        next_time_view = findViewById(R.id.nextTimeView);
        next_location = "설정된 장소가 없습니다.";
        next_time = "";
        mContext = this;
        //스터디 모집 상태 가져오기

        editMarkdown = findViewById(R.id.edit_btn);
        editMarkdown.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //상세스터디 마크다운에디터
                Intent intent = new Intent(SettingStudyActivity.this, EditorActivity.class);
                intent.putExtra("study_key", study_key );
                startActivity(intent);
            }
        });

        schedule_btn = findViewById(R.id.study_schedule_btn);
        schedule_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //상세스터디 마크다운에디터
                Intent intent = new Intent(SettingStudyActivity.this, MakeTodoActivity.class);
                intent.putExtra("study_key", study_key );
                startActivity(intent);
            }
        });

        // 다음 모임 시간, 장소 텍스트뷰 업데이트
        studyRef.child(study_key).child("study_day").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("ds.getvalue",dataSnapshot.getValue().toString());
                String study_day = dataSnapshot.getValue().toString();
                for(int i = 0; i < study_day.length(); i++){
                    String day = study_day.substring(i);
                    Log.d(day, day);
                    switch (day){
                        case "월":
                            next_time += getDay.getMonday() + "\n";
                            break;
                        case "화":
                            next_time += getDay.getTuesday() + "\n";
                            break;
                        case "수":
                            next_time += getDay.getWednesday() + "\n";
                            break;
                        case "목":
                            next_time += getDay.getThursday() + "\n";
                            break;
                        case "금":
                            next_time += getDay.getFriday() + "\n";
                            break;
                        case "토":
                            next_time += getDay.getSaturday() + "\n";
                            break;
                        case "일":
                            next_time += getDay.getSunday() + "\n";
                            break;
                    }
                    studyRef.child(study_key).child("next_schedule").child("next_time").setValue(next_time).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ((SettingStudyActivity) SettingStudyActivity.mContext).refresh_nextSchedule_view();

                        }
                    });
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        refresh_nextSchedule_view();
        setNextSchedule = findViewById(R.id.schedule_setting_btn);
        setNextSchedule.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Dialog_nextScheduleSetting dialog_nextScheduleSet = new Dialog_nextScheduleSetting(SettingStudyActivity.this);
                dialog_nextScheduleSet.callFunction(study_key);

            }
        });

    }


    public void refresh_nextSchedule_view() {

        Log.d("refresh_nextSchedule_view","start");
        studyRef.child(study_key).child("next_schedule").child("next_location").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {

                Log.d("ds.getKey() test :",ds.getKey());
                if (ds.getValue() != null) {
                    next_location_view.setText(ds.getValue().toString());
                    Log.d("locationsteted", "Succes");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        studyRef.child(study_key).child("next_schedule").child("next_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                Log.d("ds.getKey() test :",ds.getKey());
                if (ds.getValue() != null) {
                    next_time_view.setText(ds.getValue().toString());
                    Log.d("timesteted", "Succes");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



}
