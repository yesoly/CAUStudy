package com.example.caustudy.ui.Setting;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.caustudy.MdInstructionActivity;
import com.example.caustudy.MemberManagement.Dialog_member_evaluation;
import com.example.caustudy.MemberManagement.Dialog_rating_inquiry;
import com.example.caustudy.MemberManagement.MemberManagementActivity;
import com.example.caustudy.QuestionActivity;
import com.example.caustudy.R;
import com.example.caustudy.StudyMenuActivity;
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

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    String number;
    SettingRecyclerAdapter singerAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference_user = firebaseDatabase.getReference("사용자");
    DatabaseReference datebaseReference_study = firebaseDatabase.getReference("StudyList");
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    List<String> study_list = new ArrayList<>();
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                ViewModelProviders.of(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);


        recyclerView = root.findViewById(R.id.mt_list);

        singerAdapter = new SettingRecyclerAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);


        recyclerView.setAdapter(singerAdapter);


        singerAdapter.addItem(new SettingData("스터디 참여도 평가 내역"));
        singerAdapter.addItem(new SettingData("관심 태그 관리"));
        singerAdapter.addItem(new SettingData("마크다운에디터 설명서"));
        singerAdapter.addItem(new SettingData("관리자 문의"));
        singerAdapter.setOnItemClickListener(new com.example.caustudy.ui.Setting.SettingRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position){
                if (position == 0 ) {
                    String id_mem = user_id;
                    Dialog_rating_inquiry dialog = new Dialog_rating_inquiry(SettingFragment.this.getContext());
                    dialog.callFunction(id_mem);
                } else if (position == 1) {
                    Intent intent = new Intent(getActivity(), Setting_HashtagActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(getActivity(), MdInstructionActivity.class);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(SettingFragment.this.getContext(), QuestionActivity.class);
                    intent.putExtra("address","ys_7197@naver.com");
                    startActivity(intent);
                }
            }
        });



        // 스터디 리스트 목록부터 받아와야함(순서 중요)
        //check_mystudy_list();

        return root;
    }



    void check_mystudy_list(){
        if (userAuth != null) {
            // 내가 가입한 스터디 목록 탐색
            databaseReference_user.child(user_id).child("taken_study").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String study_list_raw = ds.getKey();
                        // 리스트에 값이 저장이 안되는거 같아서 일단 때려박았어..
                        study_list.add(study_list_raw); // 키 값 저장 (format = L_cate:S_cate:001)

                        StringTokenizer tokens = new StringTokenizer(study_list_raw, ":");
                        String L_cate, S_cate;
                        L_cate = tokens.nextToken();
                        S_cate = tokens.nextToken();
                        number = tokens.nextToken();

                        datebaseReference_study.child(L_cate).child(S_cate).addListenerForSingleValueEvent(new ValueEventListener() {
                            String num = number;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.getKey().equals(num)) {
                                        String title = ds.child("study_name").getValue().toString();

                                        singerAdapter.addItem(new SettingData(title));
                                        singerAdapter.notifyDataSetChanged();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError databaseError){
                            }
                        });

                        //get_study_info(L_cate, S_cate);
                        Log.d("MyStudyFragment:check_mystudy_list: ",String.valueOf(number));
                    }
                }
                @Override
                public void onCancelled (@NonNull DatabaseError databaseError) {

                }
            });
            //get_study_info();
        }
    }

}
