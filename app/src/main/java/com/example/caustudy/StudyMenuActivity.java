package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caustudy.MemberManagement.MemberManagementActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StudyMenuActivity extends AppCompatActivity {
    private String study_key, study_name, leader_email;
    TextView title;
    private FirebaseUser userAuth;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_menu);

        title = findViewById(R.id.textView_studyname);
        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        study_name = intent.getStringExtra("study_name");
        title.setText(study_name);

        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();

        studyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (study_name.equals(ds.child("study_name").getValue().toString())) {
                        leader_email = ds.child("leader_email").getValue().toString();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ListView listview ;
        ListViewAdapter adapter;

        // Adapter 생성
        adapter = new ListViewAdapter() ;

        // 리스트뷰 참조 및 Adapter달기
        listview = (ListView) findViewById(R.id.listview1);
        listview.setAdapter(adapter);

        // 첫 번째 아이템 추가.    public void addItem(Drawable icon, String title, String desc) {
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_insert_comment_black_24dp),
                "공지사항", "스터디 알림 사항을 확인해보세요!") ;
        // 두 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_edit_black_24dp),
                "상세 스터디 정보", "마크다운 에디터를 이용해서 스터디 정보를 작성해보세요!.") ;
        // 세 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_insert_drive_file_black_24dp),
                "스터디 준비 과제", "미리 준비해 오시면 좋아요!") ;
        // 네 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_question_answer_black_24dp),
                "스터디장에게 문의", "스터디장에게 이메일 문의 보내") ;
        // 다섯 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_event_available_black_24dp),
                "출석부 관리", "스터디장만 접근 권한이 있습니다.") ;
        // 여섯 번째 아이템 추가.
        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_people_blue_24dp),
                "스터디원 관리", "스터디장만 접근 권한이 있습니다.") ;

        adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_settings_black_24dp),
                "스터디 관리", "스터디장만 접근 권한이 있습니다.") ;
        // 위에서 생성한 listview에 클릭 이벤트 핸들러 정의.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                if (position == 0){
                    Intent intent = new Intent(StudyMenuActivity.this, JMainActivity.class);
                    intent.putExtra("study_key", study_key );
                    intent.putExtra("study_name", study_name );
                    startActivity(intent);
                }
                if (position == 1){
                    //상세스터디 마크다운에디터
                    Intent intent = new Intent(StudyMenuActivity.this, CoreActivity.class);
                    intent.putExtra("study_key", study_key );
                    startActivity(intent);
                }
                if (position == 2){
                    Intent intent = new Intent(StudyMenuActivity.this, JMainActivity2.class);
                    intent.putExtra("study_key", study_key );
                    intent.putExtra("study_name", study_name );
                    startActivity(intent);


                }
                if (position == 3){
                    //1:1 문의
                    Intent intent = new Intent(StudyMenuActivity.this, QuestionActivity.class);
                    intent.putExtra("address",leader_email);
                    startActivity(intent);
                }
                if (position == 4){
                    //출석부 관리
                    Intent intent = new Intent(StudyMenuActivity.this, AttendantActivity.class);
                    intent.putExtra("study_key", study_key );
                    startActivity(intent);
                }


                if (position == 5){
                    //스터디 관리
                    if (leader_email.equals(userAuth.getEmail())) {
                        Intent intent = new Intent(StudyMenuActivity.this, MemberManagementActivity.class);
                        intent.putExtra("study_key", study_key);
                        intent.putExtra("study_name", study_name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "스터디원 관리에 접근할 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }

                if (position == 6){
                    //스터디 관리
                    if (leader_email.equals(userAuth.getEmail())) {
                        Intent intent = new Intent(StudyMenuActivity.this, SettingStudyActivity.class);
                        intent.putExtra("study_key", study_key);
                        intent.putExtra("study_name", study_name);
                        startActivity(intent);
                    } else {
                        Toast.makeText(getApplicationContext(), "스터디 관리에 접근할 권한이 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }) ;

    }
}
