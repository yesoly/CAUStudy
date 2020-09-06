package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.StringTokenizer;

public class MakeStudyActivity extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener callbackMethod;
    private TimePickerDialog.OnTimeSetListener callbackMethod2;

    ArrayAdapter<CharSequence> adapter_large, adapter_small; //어댑터를 선언
    String email, name;
    TextView tv_start, tv_end, tv_time;
    EditText study_name_e, organization_e, tag_e, info_e;
    String day = "";
    String study_name, organization, tag, info, l_cate, s_cate, s_period, e_period, time;
    Button create_btn;
    Spinner small_category, large_category;
    StudyModel study;
    long count=0;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("사용자");
    DatabaseReference studyRef = database.getReference("Study");
    DatabaseReference tagRef = database.getReference("Hashtags");
    //DatabaseReference studyRef = database.getReference("StudyList");

    private ToggleButton _toggleSun, _toggleMon, _toggleTue, _toggleWed, _toggleThu, _toggleFri, _toggleSat;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_study);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        study_name_e=(EditText)findViewById(R.id.name);
        organization_e=(EditText) findViewById(R.id.organization);
        tag_e=(EditText)findViewById(R.id.tag);
        info_e=(EditText) findViewById(R.id.info);
        create_btn=(Button)findViewById(R.id.create);
        large_category=(Spinner)findViewById(R.id.large_category);
        small_category=(Spinner)findViewById(R.id.small_category);

        _toggleSun = (ToggleButton) findViewById(R.id.toggle_sun);
        _toggleMon = (ToggleButton) findViewById(R.id.toggle_mon);
        _toggleTue = (ToggleButton) findViewById(R.id.toggle_tue);
        _toggleWed = (ToggleButton) findViewById(R.id.toggle_wed);
        _toggleThu = (ToggleButton) findViewById(R.id.toggle_thu);
        _toggleFri = (ToggleButton) findViewById(R.id.toggle_fri);
        _toggleSat = (ToggleButton) findViewById(R.id.toggle_sat);


        if (user != null){
            for (UserInfo profile : user.getProviderData()){
                email = profile.getEmail();
            }
        }

        tv_start = (TextView)findViewById(R.id.start_day);
        tv_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startOnClickHandler(v);
            }
        });

        tv_end = (TextView)findViewById(R.id.end_day);
        tv_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endOnClickHandler(v);
            }
        });

        tv_time = (TextView)findViewById(R.id.time);
        tv_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeOnClickHandler(v);
            }
        });

        adapter_large = ArrayAdapter.createFromResource(this, R.array.spinner_do, android.R.layout.simple_spinner_dropdown_item);
        large_category.setAdapter(adapter_large);
        large_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter_large.getItem(i).equals("전공")) {
                    l_cate = "전공";
                    adapter_small = ArrayAdapter.createFromResource(MakeStudyActivity.this, R.array.spinner_do_major, android.R.layout.simple_spinner_dropdown_item);
                    adapter_small.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    small_category.setAdapter(adapter_small);
                    small_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            s_cate = adapter_small.getItem(i).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adapter_large.getItem(i).equals("어학")) {
                    l_cate = "어학";
                    adapter_small = ArrayAdapter.createFromResource(MakeStudyActivity.this, R.array.spinner_do_language, android.R.layout.simple_spinner_dropdown_item);
                    adapter_small.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    small_category.setAdapter(adapter_small);
                    small_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            s_cate = adapter_small.getItem(i).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adapter_large.getItem(i).equals("시험")) {
                    l_cate = "시험";
                    adapter_small = ArrayAdapter.createFromResource(MakeStudyActivity.this, R.array.spinner_do_exam, android.R.layout.simple_spinner_dropdown_item);
                    adapter_small.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    small_category.setAdapter(adapter_small);
                    small_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            s_cate = adapter_small.getItem(i).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //스터디 등록을 누르면 스터디 등록
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerStudy();
                study_name_e.setText(null);
                organization_e.setText(null);
                tag_e.setText(null);
                info_e.setText(null);
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    public void startOnClickHandler(View view){
        callbackMethod = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                s_period = String.format("%d-%d-%d", year, monthOfYear+1, dayOfMonth);
                tv_start.setText(s_period);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2020, 5, 1);
        dialog.show();
    }
    public void endOnClickHandler(View view){
        callbackMethod = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                e_period = String.format("%d-%d-%d", year, monthOfYear+1, dayOfMonth);
                tv_end.setText(e_period);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2020, 5, 1);
        dialog.show();
    }
    public void timeOnClickHandler(View view){
        callbackMethod2 = new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time = String.format("%d:%d", hourOfDay, minute);
                tv_time.setText(time);
            }
        };
        TimePickerDialog dialog = new TimePickerDialog(this, callbackMethod2, 8, 10, true);
        dialog.show();
    }

    public void checkDay(){
        boolean[] week = {_toggleMon.isChecked(), _toggleTue.isChecked(), _toggleWed.isChecked(),
                _toggleThu.isChecked(), _toggleFri.isChecked(), _toggleSat.isChecked(), _toggleSun.isChecked()};
        if (week[0] == true){
            day = day + "월";
        }
        if (week[1] == true){
            day = day + "화";
        }
        if (week[2] == true){
            day = day + "수";
        }
        if (week[3] == true){
            day = day + "목";
        }
        if (week[4] == true){
            day = day + "금";
        }
        if (week[5] == true){
            day = day + "토";
        }
        if (week[6] == true){
            day = day + "일";
        }
    }
    void set_leader_user() {
        if (user != null) {
            Log.d("test","userAuth ok");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (user.getEmail().equals(ds.child("email").getValue().toString())) {
                            String key = ds.getKey();
                            name = ds.child("username").getValue().toString();

                            if (count >= 9) {
                                userRef.child(key).child("taken_study").child("0" + (count+1)).setValue(study_name);
                            } else {
                                userRef.child(key).child("taken_study").child("00" + (count+1)).setValue(study_name);
                            }
                        }
                        else {
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    long make_key_value() {
        studyRef.child(l_cate).child(s_cate).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("count","entered count");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("count","count increased");
                    count++;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return count;
    }
    public void registerStudy() {
        checkDay();
        make_key_value();
        study_name = study_name_e.getText().toString();
        organization = organization_e.getText().toString();
        tag = tag_e.getText().toString();
        info = info_e.getText().toString();
        study = new StudyModel(study_name, organization, l_cate, s_cate, name, email, info, s_period, e_period, day, time);
        //StringTokenizer stringTokenizer = new StringTokenizer(tag, "#");

        Log.d("registerStudy ", "Started");
        // 카운트 세는 함수 안에다가 코드를 넣었다.
        count = 0;
        studyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("count","entered count");
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Log.d("count","count increased");
                    count++;
                }

                Toast.makeText(MakeStudyActivity.this, "신규 스터디가 생성되었습니다!", Toast.LENGTH_LONG).show();

                // input data in user DB
                set_leader_user();
                // input data in study DB
                StringTokenizer stringTokenizer = new StringTokenizer(email, "@");
                String id = stringTokenizer.nextToken(); //@ 분리
                StringTokenizer hashTokenizer = new StringTokenizer(tag, "#");
                if (count >= 9) {
                    //studyRef.child(l_cate).child(s_cate).child("0" + (count + 1)).setValue(study);
                    studyRef.child("0" + (count + 1)).setValue(study);
                    //studyRef.child(l_cate).child(s_cate).child("0" + (count + 1)).child("member_list").child(id).setValue(email);
                    studyRef.child("0" + (count + 1)).child("member_list").child(id).setValue(email);
                    studyRef.child("0" + (count + 1)).child("applier_limit").setValue("10");
                    studyRef.child("0" + (count + 1)).child("detail_info").setValue(" ");

                    while(hashTokenizer.hasMoreTokens()) {
                        String hashtag = hashTokenizer.nextToken();
                        hashtag = hashtag.trim();
                        studyRef.child("0" + (count + 1)).child("hashtag").child(hashtag).setValue(001);
                        tagRef.child(hashtag).child("0" + (count + 1)).setValue(study_name);
                        // 해쉬태그 히스토리 추

                        final String[] tag_value = new String[1];
                        final int[] tag_value_int = new int[1];
                        final String tagName = hashtag;
                        Log.d("해시태그 히스토리 검색",user_id+ " " + tagName );
                        userRef.child(user_id).child("hashtag_history").child(tagName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    tag_value[0] = dataSnapshot.getValue().toString();
                                    Log.d("key and value", dataSnapshot.getKey() + " " + tag_value[0]);
                                    tag_value_int[0] = Integer.parseInt(tag_value[0]);
                                    tag_value_int[0] += 1;
                                    Log.d("tag_value_int",Integer.toString(tag_value_int[0]));
                                } else {
                                    tag_value_int[0] = 1;
                                }
                                userRef.child(user_id).child("hashtag_history").child(tagName).setValue(Integer.toString(tag_value_int[0]));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                } else {
                    //studyRef.child(l_cate).child(s_cate).child("00" + (count + 1)).setValue(study);
                    studyRef.child("00" + (count + 1)).setValue(study);
                    studyRef.child("00" + (count + 1)).child("detail_info").setValue(" ");

                    //studyRef.child(l_cate).child(s_cate).child("00" + (count + 1)).child("member_list").child(id).setValue(email);
                    studyRef.child("00" + (count + 1)).child("member_list").child(id).setValue(email);
                    while(hashTokenizer.hasMoreTokens()) {
                        String hashtag = hashTokenizer.nextToken();
                        hashtag = hashtag.trim();
                        studyRef.child("00" + (count + 1)).child("hashtag").child(hashtag).setValue(1);
                        tagRef.child(hashtag).child("00" + (count + 1)).setValue(study_name);

                        // 해쉬태그 히스토리 추가
                        final String[] tag_value = new String[1];
                        final int[] tag_value_int = new int[1];
                        final String tagName = hashtag;
                        Log.d("해시태그 히스토리 검색",user_id+ " " + tagName );
                        userRef.child(user_id).child("hashtag_history").child(tagName).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() != null) {
                                    tag_value[0] = dataSnapshot.getValue().toString();
                                    Log.d("key and value", dataSnapshot.getKey() + " " + tag_value[0]);
                                    tag_value_int[0] = Integer.parseInt(tag_value[0]);
                                    tag_value_int[0] += 1;
                                    Log.d("tag_value_int",Integer.toString(tag_value_int[0]));
                                } else {
                                    tag_value_int[0] = 1;
                                }
                                userRef.child(user_id).child("hashtag_history").child(tagName).setValue(Integer.toString(tag_value_int[0]));
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });
                    }
                }

                // HashTag history 추가
                /*
                studyRef.child("00" + (count + 1)).child("hashtag").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final String[] tag_value = new String[1];
                            final int[] tag_value_int = new int[1];
                            final String tag_key = ds.getKey();
                            if (ds.getKey() != null) {
                                userRef.child(name).child("hashtag_history").child(tag_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue() != null) {
                                            tag_value[0] = dataSnapshot.getValue().toString();

                                            Log.d("key and value", dataSnapshot.getKey() + " " + tag_value[0]);
                                            tag_value_int[0] = Integer.parseInt(tag_value[0]);
                                            tag_value_int[0] += 1;
                                            Log.d("tag_value_int",Integer.toString(tag_value_int[0]));


                                        } else {
                                            tag_value_int[0] = 1;
                                        }
                                        userRef.child(name).child("hashtag_history").child(tag_key).setValue(Integer.toString(tag_value_int[0]));

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                                Log.d("DB insert test ",tag_key + " " + Integer.toString(tag_value_int[0]));

                            }
                            // check

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                }); */

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}