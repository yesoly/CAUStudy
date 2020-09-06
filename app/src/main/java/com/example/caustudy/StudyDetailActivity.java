package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caustudy.Models.Post;
import com.example.caustudy.ui.searchstudy.SearchStudyFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;

import io.noties.markwon.Markwon;
import io.noties.markwon.core.CorePlugin;
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin;

public class StudyDetailActivity extends AppCompatActivity {
    long count = 0;
    private String study_name;
    private String leader_email, auth_id;
    private String key;
    User applier;
    TextView tv_title, tv_period, tv_category, tv_time, tv_leader, tv_info, tv_org;
    String title, period, category, time, leader, info, org;
    private String auth_name, auth_email, auth_l_cate, auth_s_cate;
    Button apply, back;
    private TextView textView;
    private String mark_text;
    private FirebaseUser userAuth;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private PopupWindow mPopupWindow ;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference studyRef = database.getReference("Study");
    DatabaseReference databaseReference_user = database.getReference("사용자");
    private String study_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_detail);
        tv_title = (TextView)findViewById(R.id.tv_title);
        tv_period = (TextView)findViewById(R.id.tv_period);
        tv_category = (TextView)findViewById(R.id.tv_category);
        tv_leader = (TextView)findViewById(R.id.tv_leader);
        tv_time= (TextView)findViewById(R.id.tv_time);
        tv_info = (TextView)findViewById(R.id.tv_info);
        tv_org = (TextView)findViewById(R.id.tv_org);
        apply = (Button)findViewById(R.id.apply_btn);
        back = (Button)findViewById(R.id.back_btn);
        Intent intent = getIntent();
        study_name = intent.getStringExtra("study_name");
        study_key = intent.getStringExtra("study_key");

        get_study_info();
        set_view();
        textView = findViewById(R.id.text_markdown);

        // 근데 detail_info를 안 작성하면 에러뜨면서 꺼지는데? 파베 어려워요.,, 몰라요...

        studyRef.child(study_key).child("detail_info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.getValue() != null) {
                    mark_text = ds.getValue().toString();
                    simple();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });





        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                finish();
            }
        });

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View popupView = getLayoutInflater().inflate(R.layout.apply_pop_up, null);
                mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                mPopupWindow.setFocusable(true);
                mPopupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
                final Button okButton = (Button) popupView.findViewById(R.id.okButton);
                final Button cancelButton = (Button) popupView.findViewById(R.id.cancelButton);
                final TextView tv = (TextView)popupView.findViewById(R.id.apply_msg);
                String message = study_name + "에 스터디 신청을 하시겠습니까?";
                tv.setText(message);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        apply_study();
                        mPopupWindow.dismiss();
                        //finish();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String toast_text = "스터디 신청 취소.";
                        Toast toast = Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT);
                        LinearLayout toastLayout = (LinearLayout) toast.getView();
                        TextView toastTV = (TextView) toastLayout.getChildAt(0);
                        toastTV.setTextSize(20);
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                        toast.show();

                        mPopupWindow.dismiss();
                    }
                });
            }
        });
    }

    void get_user_info(){
        if (userAuth != null) {
            databaseReference_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (userAuth.getEmail().equals(ds.child("email").getValue())) {
                            //User(String email, String username, String L_deptname, String S_deptname)
                            auth_name = ds.child("username").getValue().toString();
                            auth_email = ds.child("email").getValue().toString();
                            auth_l_cate = ds.child("L_deptname").getValue().toString();
                            auth_s_cate = ds.child("S_deptname").getValue().toString();
                            applier = new User(auth_email, auth_name, auth_l_cate, auth_s_cate);
                            studyRef.child(key).child("applier_list").child(auth_id).setValue(applier);
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    void apply_study(){
        mAuth = FirebaseAuth.getInstance();
        userAuth = mAuth.getCurrentUser();
        String email = userAuth.getEmail();
        StringTokenizer stringTokenizer = new StringTokenizer(email, "@");
        auth_id = stringTokenizer.nextToken(); //@ 분리


        if (userAuth != null) {

            final int[] num_applier = {0};
            final int[] applier_limit = {0};

            // Get applier_limit value
            studyRef.child(study_key).child("applier_limit").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getValue() != null) {
                        Log.d("get limit value : ",dataSnapshot.getValue().toString());
                        applier_limit[0] = Integer.parseInt(dataSnapshot.getValue().toString());
                    }
                    studyRef.child(study_key).child("applier_list").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            for (DataSnapshot applier : dataSnapshot.getChildren()) {
                                num_applier[0] += 1;
                            }
                            Log.d("get applier value : ",String.valueOf(num_applier[0]));

                            // Check the applier limit
                            if (num_applier[0] >= applier_limit[0]) {
                                String toast_text = "스터디 지원 대기열이 가득 찼습니다.";
                                Toast toast = Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT);
                                LinearLayout toastLayout = (LinearLayout) toast.getView();
                                TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                toastTV.setTextSize(20);
                                toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                                toast.show();

                            } else {
                                studyRef.child(study_key).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot ds) {
                                        StringTokenizer stringTokenizer_leader = new StringTokenizer(leader_email, "@");
                                        String leader_id = stringTokenizer_leader.nextToken();

                                        final Boolean[] declined = {false};

                                        studyRef.child(study_key).child("block_member_list").addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                                    Log.d("block test ",ds.getKey()+auth_id);

                                                    if (ds.getKey().equals(auth_id)) {
                                                        declined[0] = true;
                                                    }

                                                }

                                                if (ds.child("reApply_status").getValue() != null) {
                                                    declined[0] = false;
                                                }
                                                Log.d("decline value : ",declined[0].toString());
                                                if (leader_id.equals(auth_id)){
                                                    String toast_text = "자신이 만든 스터디는 신청할 수 없습니다.";
                                                    Toast toast = Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT);
                                                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                                    toastTV.setTextSize(20);
                                                    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                                                    toast.show();

                                                } else if (ds.child("apply_status").getValue() != null) {
                                                    String toast_text = "스터디 모집이 마감되었습니다.";
                                                    Toast toast = Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT);
                                                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                                    toastTV.setTextSize(20);
                                                    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                                                    toast.show();

                                                } else if (declined[0]) {
                                                    String toast_text = "참여가 거절된 스터디입니다.";
                                                    Toast toast = Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT);
                                                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                                    toastTV.setTextSize(20);
                                                    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                                                    toast.show();
                                                } else {
                                                    key = study_key;
                                                    get_user_info();
                                                    String toast_text = "스터디 신청 완료.";
                                                    Toast toast = Toast.makeText(getApplicationContext(), toast_text, Toast.LENGTH_SHORT);
                                                    LinearLayout toastLayout = (LinearLayout) toast.getView();
                                                    TextView toastTV = (TextView) toastLayout.getChildAt(0);
                                                    toastTV.setTextSize(20);
                                                    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                                                    toast.show();                            }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });


                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });






        }
    }

    void get_study_info(){
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("Study");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (study_name.equals(ds.child("study_name").getValue().toString())) {
                        // 상세 데이터 가져오기
                        String L = ds.child("L_category").getValue().toString();
                        String S = ds.child("S_category").getValue().toString();
                        String e_day = ds.child("e_period").getValue().toString();
                        String s_day = ds.child("s_period").getValue().toString();
                        info = ds.child("info").getValue().toString();
                        leader_email = ds.child("leader_email").getValue().toString();

                        category = L + " / " + S;
                        period = s_day + " ~ " + e_day;
                        leader = ds.child("leader_email").getValue().toString();
                        org = ds.child("organization").getValue().toString();
                        String study_day = ds.child("study_day").getValue().toString();
                        String study_time = ds.child("study_time").getValue().toString();
                        time = "매주 " + study_day + " / " + study_time;
                        title = study_name;
                        tv_category.setText(category);
                        tv_title.setText(title);
                        tv_period.setText(period);
                        tv_time.setText(time);
                        tv_leader.setText(leader);
                        tv_org.setText(org);
                        tv_info.setText(info);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void simple() {
        // 여기에 그냥 친거 그대로 받아오면 될 것 같은데?
        final Markwon markwon = Markwon.builder(this)
                .usePlugin(CorePlugin.create())
                .usePlugin(StrikethroughPlugin.create())
                .build();

        final String markdown = mark_text;
        // this will parse raw markdown and set parsed content to specified TextView
        markwon.setMarkdown(textView, markdown);
    }

    void set_view(){

    }
}

