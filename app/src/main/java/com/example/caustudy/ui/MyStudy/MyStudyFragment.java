package com.example.caustudy.ui.MyStudy;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.Models.TextParsing;
import com.example.caustudy.R;
import com.example.caustudy.StudyDetailActivity;
import com.example.caustudy.StudyMenuActivity;
import com.example.caustudy.ui.searchstudy.RecyclerAdapter;
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

public class MyStudyFragment extends Fragment {

    private MyStudyViewModel myStudyViewModel;

    GridView gridView;
    EditText editText;
    EditText editText2;
    Button button;
    String study_list_raw;
    MyStudy_Adapter singerAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference_user = firebaseDatabase.getReference("사용자");
    DatabaseReference datebaseReference_study = firebaseDatabase.getReference("Study");
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    List<String> study_list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Log.d("study",user_id);

        myStudyViewModel = ViewModelProviders.of(this).get(MyStudyViewModel.class);
        View root = inflater.inflate(R.layout.fragment_mystudy, container, false);

        gridView = (GridView)root.findViewById(R.id.gridView);

        singerAdapter = new MyStudy_Adapter();

        // 스터디 리스트 목록부터 받아와야함(순서 중요)
        check_mystudy_list();

        Log.d("study_list check :",study_list.toString());
        gridView.setAdapter(singerAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), StudyMenuActivity.class);
                intent.putExtra("study_key", study_list.get(i) );
                MyStudy_SingerItem item = singerAdapter.getItem(i);
                String title = item.getTitle();
                intent.putExtra("study_name", title);
                startActivity(intent);
            }
        });
        return root;
    }

    void check_mystudy_list(){
        if (userAuth != null) {
            // 내가 가입한 스터디 목록 탐색
            databaseReference_user.child(user_id).child("taken_study").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    singerAdapter.resetItem(); // 중복 방지
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        study_list_raw = ds.getKey();
                        // 리스트에 값이 저장이 안되는거 같아서 일단 때려박았어..
                        study_list.add(study_list_raw); // 키 값 저장 (format = 001)

                        //get_study_info
                        datebaseReference_study.addListenerForSingleValueEvent(new ValueEventListener() {
                            String num = study_list_raw;
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    if (ds.getKey().equals(num)) {
                                        String title = ds.child("study_name").getValue().toString();
                                        String s_period = ds.child("s_period").getValue().toString();   // 시작일
                                        String e_period = ds.child("e_period").getValue().toString();   // 종료일
                                        String day = ds.child("study_day").getValue().toString();  // 요일
                                        String time = ds.child("study_time").getValue().toString();  // 시간
                                        String org = ds.child("organization").getValue().toString();  // 소속
                                        singerAdapter.addItem(new MyStudy_SingerItem(title, s_period + " ~ " + e_period, day + " / " + time, org));
                                        singerAdapter.notifyDataSetChanged();
                                    }
                                    //singerAdapter.addItem(new MyStudy_SingerItem("test","period","time","zp"));
                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError databaseError){
                            }
                        });
                    }
                }
                @Override
                public void onCancelled (@NonNull DatabaseError databaseError) {

                }
            });
            //get_study_info();
        }
    }

    void get_study_info(String L, String S){
            datebaseReference_study.child(L).child(S).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        if (ds.getKey().equals(study_list_raw)) {
                            String title = ds.child("study_name").getValue().toString();
                            String s_period = ds.child("s_period").getValue().toString();   // 시작일
                            String e_period = ds.child("e_period").getValue().toString();   // 종료일
                            String day = ds.child("study_day").getValue().toString();  // 요일
                            String time = ds.child("study_time").getValue().toString();  // 시간
                            String org = ds.child("organization").getValue().toString();  // 소속
                            singerAdapter.addItem(new MyStudy_SingerItem(title, s_period + " ~ " + e_period, day + " / " + time, org));
                            singerAdapter.notifyDataSetChanged();
                        }
                        //singerAdapter.addItem(new MyStudy_SingerItem("test","period","time","zp"));
                    }
                }

                @Override
                public void onCancelled (@NonNull DatabaseError databaseError){
                }
            });
        }
}