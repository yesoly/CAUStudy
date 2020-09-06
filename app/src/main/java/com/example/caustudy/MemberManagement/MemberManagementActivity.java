package com.example.caustudy.MemberManagement;

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
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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

public class MemberManagementActivity extends AppCompatActivity {


    List<String> listName = new ArrayList<>();
    List<String> listEmail = new ArrayList<>();
    List<String> list_L = new ArrayList<>();
    List<String> list_S = new ArrayList<>();
    List<String> listId = new ArrayList<>();

    List<String> member_email = new ArrayList<>();
    List<String> member_name = new ArrayList<>();
    List<String> member_list_L = new ArrayList<>();
    List<String> member_list_S = new ArrayList<>();

    List<String> block_member_email = new ArrayList<>();
    List<String> block_member_name = new ArrayList<>();
    List<String> block_member_list_L = new ArrayList<>();
    List<String> block_member_list_S = new ArrayList<>();




    Switch fin;
    Switch fin2;
    RecyclerView apply_recyclerView;
    RecyclerView member_recyclerView;
    RecyclerView block_member_recyclerView;

    private MemberViewAdapter member_view_adapter;
    private ApplyViewAdapter apply_view_adapter;
    private BlockMemberViewAdapter block_member_view_adapter;

    private String study_key, study_name, email, name, l_dept, s_dept;

    private Button set_apply_limit;
    private TextView applier_text_view;
    final String[] applier_limit = new String[1];

    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String cur_user_id = stringTokenizer.nextToken();

    public static Context mContext;

    class ApplyableSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                studyRef.child(study_key).child("apply_status").setValue(1);
            else
                studyRef.child(study_key).child("apply_status").removeValue();
        }
    }
    class ReApplyableSwitchListener implements CompoundButton.OnCheckedChangeListener{
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked)
                studyRef.child(study_key).child("reApply_status").setValue(1);
            else
                studyRef.child(study_key).child("reApply_status").removeValue();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_management);
        Intent intent = getIntent();
        study_key = intent.getStringExtra("study_key");
        study_name = intent.getStringExtra("study_name");
        applier_text_view = findViewById(R.id.textView_applier_limit_value);


        set_apply_limit = findViewById(R.id.btn_set_applyNum);
        set_apply_limit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog_NumApply dialog_numApply = new Dialog_NumApply(MemberManagementActivity.this);
                dialog_numApply.callFunction(study_key);
                update_view();
            }
        });

        studyRef.child(study_key).child("applier_limit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.getValue() != null) {
                    applier_limit[0] = ds.getValue().toString();
                    applier_text_view.setText(applier_limit[0] + " 명");
                }
                else {
                    applier_limit[0] = "-";
                    applier_text_view.setText(applier_limit[0] + " 명");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


        mContext = this;
        get_switch_status();
        //스터디 모집 상태 가져오기
        fin = findViewById(R.id.switch_fin_apply);
        fin.setOnCheckedChangeListener(new ApplyableSwitchListener());
        fin2 = findViewById(R.id.switch_fin_reapply);
        fin2.setOnCheckedChangeListener(new ReApplyableSwitchListener());

        apply_recyclerView = findViewById(R.id.apply_view);
        member_recyclerView = findViewById(R.id.member_view);
        block_member_recyclerView = findViewById(R.id.block_user_view);

        apply_view_adapter = new ApplyViewAdapter();
        apply_view_adapter.setStudyKey(study_key);

        member_view_adapter = new MemberViewAdapter();

        block_member_view_adapter = new BlockMemberViewAdapter();


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MemberManagementActivity.this);
        apply_recyclerView.setLayoutManager(linearLayoutManager);

        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(MemberManagementActivity.this);
        member_recyclerView.setLayoutManager(linearLayoutManager2);

        LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(MemberManagementActivity.this);
        block_member_recyclerView.setLayoutManager(linearLayoutManager3);


        member_view_adapter.setOnItemClickListener(new MemberViewAdapter.OnItemClickListener() {
            @Override
            public void onEvalClick(View v, int position) {
                String email_mem = member_email.get(position);
                StringTokenizer id_token = new StringTokenizer(email_mem, "@");
                String id_mem = id_token.nextToken();
                String name_mem = member_name.get(position);
                Dialog_member_evaluation dialog_eval = new Dialog_member_evaluation(MemberManagementActivity.this);
                dialog_eval.callFunction(study_key,id_mem);
                update_view();
            }
            @Override
            public void onKickClick(View v, int position) {
                String email_mem = member_email.get(position);
                StringTokenizer id_token = new StringTokenizer(email_mem, "@");
                String id_mem = id_token.nextToken();
                String name_mem = member_name.get(position);

                studyRef.child(study_key).child("member_list").child(id_mem).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MemberManagementActivity.this, "해제 성공", Toast.LENGTH_LONG).show();
                        update_member_view();
                    }
                });
                userRef.child(id_mem).child("taken_study").child(study_key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                });


                update_view();


            }
        });

        block_member_view_adapter.setOnItemClickListener(new BlockMemberViewAdapter.OnItemClickListener() {
            @Override
            public void onUnblockClick(View v, int position) {
                String email_mem = block_member_email.get(position);
                StringTokenizer id_token = new StringTokenizer(email_mem, "@");
                String id_mem = id_token.nextToken();
                studyRef.child(study_key).child("block_member_list").child(id_mem).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(MemberManagementActivity.this, "해제 성공", Toast.LENGTH_LONG).show();

                        update_block_member_view();
                    }
                });
            }

            @Override
            public void onKickClick(View v, int position) {

            }
        });

        apply_view_adapter.setOnItemClickListener(new ApplyViewAdapter.OnItemClickListener() {
            @Override
            public void onAcceptClick(View v, int position) {
                String get_email = listEmail.get(position);
                StringTokenizer id_token = new StringTokenizer(get_email, "@");

                // Final로 했는데, 괜찮으려
                final String id = id_token.nextToken();
                userRef.child(id).child("taken_study").child(study_key).setValue(study_name);
                studyRef.child(study_key).child("applier_list").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(SettingStudyActivity.this, "삭제 성공", Toast.LENGTH_LONG).show();
                        update_applier_view();

                    }
                });
                // Test
                studyRef.child(study_key).child("member_list").child(id).setValue(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        update_member_view();
                        //Toast.makeText(SettingStudyActivity.this, "삭제 성공", Toast.LENGTH_LONG).show();
                    }
                });
                // Add Hashtag History
                Log.d("study_history ","check");
                userRef.child(id).child("study_history").child(study_key).child("score").setValue("-");
                studyRef.child(study_key).child("hashtag").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            final String[] tag_value = new String[1];
                            final int[] tag_value_int = new int[1];
                            final String tag_key = ds.getKey();
                            if (ds.getKey() != null) {
                                userRef.child(id).child("hashtag_history").child(tag_key).addListenerForSingleValueEvent(new ValueEventListener() {
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
                                        userRef.child(id).child("hashtag_history").child(tag_key).setValue(Integer.toString(tag_value_int[0]));

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
                });


            }

            @Override
            public void onRatingInquiry(View v, int position) {
                String email_mem = listEmail.get(position);
                String name_mem = listName.get(position);
                String id_mem = listId.get(position);
                Dialog_rating_inquiry dialog = new Dialog_rating_inquiry(MemberManagementActivity.this);
                dialog.callFunction(id_mem);
                update_view();
            }

            @Override
            public void onKickClick(View v, int position) {
                String get_email = listEmail.get(position);
                StringTokenizer id_token = new StringTokenizer(get_email, "@");
                String id = id_token.nextToken();
                studyRef.child(study_key).child("applier_list").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(SettingStudyActivity.this, "삭제 성공", Toast.LENGTH_LONG).show();
                    }
                });
                String declined_username = id;
                String declined_email = get_email;
                studyRef.child(study_key).child("block_member_list").child(declined_username).setValue(declined_email);
                update_block_member_view();

            }

            @Override
            public void onDeleteClick(View v, int position) {
                String get_email = listEmail.get(position);
                StringTokenizer id_token = new StringTokenizer(get_email, "@");
                String id = id_token.nextToken();
                studyRef.child(study_key).child("applier_list").child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Toast.makeText(SettingStudyActivity.this, "삭제 성공", Toast.LENGTH_LONG).show();
                    }
                });
                String declined_username = id;
                String declined_email = get_email;
                studyRef.child(study_key).child("declined_list").child(declined_username).setValue(declined_email);

            }
        });


        apply_recyclerView.setAdapter(apply_view_adapter);
        member_recyclerView.setAdapter(member_view_adapter);
        block_member_recyclerView.setAdapter(block_member_view_adapter);
        update_applier_view();
        update_member_view();
        update_block_member_view();
    }
    public void update_member_view() {
        member_view_adapter.clearItem();
        studyRef.child(study_key).child("member_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    member_email.clear();
                    member_name.clear();
                    member_list_L.clear();
                    member_list_S.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //member_email.add(ds.getValue().toString());


                    String user_email = ds.getValue().toString();
                    StringTokenizer id_token = new StringTokenizer(user_email, "@");
                    String user_id = id_token.nextToken();
                    Log.d("check",user_id);
                    Log.d("check-2",cur_user_id);
                    if (user_id.equals(cur_user_id)) {
                        continue;
                    }
                    final String[] name = new String[1];
                    userRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds) {
                            if (ds.getValue() != null) {
                                Log.d("member item added",ds.getKey());
                                name[0] = ds.child("username").getValue().toString();
                                String member_l_dept = ds.child("L_deptname").getValue().toString();
                                String member_s_dept = ds.child("S_deptname").getValue().toString();

                                member_email.add(user_email);
                                member_name.add(name[0]);
                                member_list_L.add(member_l_dept);
                                member_list_S.add(member_s_dept);
                                MemberViewItem memberViewItem = new MemberViewItem();
                                memberViewItem.setName(name[0]);
                                memberViewItem.setEmail(user_email);
                                memberViewItem.setL_dept(member_l_dept);
                                memberViewItem.setS_dept(member_s_dept);

                                member_view_adapter.addItem(memberViewItem);
                                // 아래 코드를 밖으로 뺴면 안되더
                                member_view_adapter.notifyDataSetChanged();
                            }
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
    public void update_block_member_view() {
        Log.d("update_block_member_view","called");
        block_member_view_adapter.clearItem();
        Log.d("update_block_member_view","adapter cleared");

        studyRef.child(study_key).child("block_member_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    block_member_email.clear();
                    block_member_name.clear();
                    block_member_list_L.clear();
                    block_member_list_S.clear();
                    Log.d("update_block_member_view","list cleared");
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //member_email.add(ds.getValue().toString());
                    String user_email = ds.getValue().toString();
                    StringTokenizer id_token = new StringTokenizer(user_email, "@");
                    String user_id = id_token.nextToken();

                    if (user_id.equals(cur_user_id)) {
                        continue;
                    }
                    final String[] name = new String[1];
                    userRef.child(user_id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot ds) {
                            if (ds.getValue() != null) {
                                name[0] = ds.child("username").getValue().toString();
                                String member_l_dept = ds.child("L_deptname").getValue().toString();
                                String member_s_dept = ds.child("S_deptname").getValue().toString();

                                block_member_email.add(user_email);
                                block_member_name.add(name[0]);
                                block_member_list_L.add(member_l_dept);
                                block_member_list_S.add(member_s_dept);
                                BlockMemberViewItem memberViewItem = new BlockMemberViewItem();
                                memberViewItem.setName(name[0]);
                                memberViewItem.setEmail(user_email);
                                memberViewItem.setL_dept(member_l_dept);
                                memberViewItem.setS_dept(member_s_dept);

                                block_member_view_adapter.addItem(memberViewItem);
                                Log.d("update_block_member_view","add item");

                                // 아래 코드를 밖으로 뺴면 안되더
                                block_member_view_adapter.notifyDataSetChanged();

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                }
                //update_block_member_view();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void get_switch_status() {
        studyRef.child(study_key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("apply_status").getValue() != null) {
                    fin.setChecked(true);
                }
                else{
                    fin.setChecked(false);
                }
                if (dataSnapshot.child("reApply_status").getValue() != null ) {
                    fin2.setChecked(true);
                }
                else {
                    fin2.setChecked(false);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }
    public void update_applier_view() {
        apply_view_adapter.clearItem();
        studyRef.child(study_key).child("applier_list").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    listId.clear();
                    listEmail.clear();
                    listName.clear();
                    list_L.clear();
                    list_S.clear();
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Log.d("applyer item added",ds.getKey());
                    email = ds.child("email").getValue().toString();
                    StringTokenizer stringTokenizer = new StringTokenizer(email, "@");
                    String id_mem = stringTokenizer.nextToken();

                    name = ds.child("username").getValue().toString();
                    l_dept = ds.child("L_deptname").getValue().toString();
                    s_dept = ds.child("S_deptname").getValue().toString();
                    listEmail.add(email);
                    Log.d("???",email);
                    listId.add(id_mem);
                    listName.add(name);
                    list_L.add(l_dept);
                    list_S.add(s_dept);

                    ApplyViewItem applyViewItem = new ApplyViewItem();
                    applyViewItem.setName(name);
                    applyViewItem.setEmail(email);
                    applyViewItem.setL_dept(l_dept);
                    applyViewItem.setS_dept(s_dept);
                    apply_view_adapter.addItem(applyViewItem);
                    apply_view_adapter.notifyDataSetChanged();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void update_view() {
        studyRef.child(study_key).child("applier_limit").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot ds) {
                if (ds.getValue() != null) {
                    applier_limit[0] = ds.getValue().toString();
                    applier_text_view.setText(applier_limit[0] + " 명");
                }
                else {
                    applier_limit[0] = "-";
                    applier_text_view.setText(applier_limit[0] + " 명");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}
