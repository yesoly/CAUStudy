package com.example.caustudy.ui.searchstudy;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.caustudy.MainActivity;
import com.example.caustudy.MakeStudyActivity;
import com.example.caustudy.R;
import com.example.caustudy.StudyDetailActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

public class SearchStudyFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private RecyclerAdapter adapter;
    Spinner small_category, large_category;
    String l_cate, s_cate;
    Button btn;
    Button create_btn;
    Button serach_btn2;
    Button recommend_btn;
    AutoCompleteTextView autoCompleteTextView;
    private ArrayList<String> fill_list;
    private String tag_search;
    ArrayAdapter<CharSequence> adapter_large, adapter_small; //어댑터를 선언
    DatabaseReference tagRef = FirebaseDatabase.getInstance().getReference("Hashtags");
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    Map<String, Integer> interest = new HashMap<>();
    private TextView upper_title;

    List<String> listKey = new ArrayList<>();
    List<String> listTitle = new ArrayList<>();
    List<String> listPeriod = new ArrayList<>();
    List<String> listTime = new ArrayList<>();
    List<String> listLeader = new ArrayList<>();
    List<String> listOrg = new ArrayList<>();
    List<String> listInfo = new ArrayList<>();

    List<String> list_match_tag = new ArrayList<>();

    RecyclerView recyclerView;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel =
                ViewModelProviders.of(this).get(SearchViewModel.class);
        View root = inflater.inflate(R.layout.fragment_searchstudy, container, false);

        upper_title = (TextView)root.findViewById(R.id.textView1);
        //자동완성
        autoCompleteTextView = (AutoCompleteTextView) root.findViewById(R.id.autoCompleteTextView);
        fill_list = new ArrayList<String>();
        setting_fill_list();
        autoCompleteTextView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, fill_list));

        serach_btn2 = (Button)root.findViewById(R.id.search_btn2);
        recommend_btn = (Button)root.findViewById(R.id.recommend_list_btn);

        create_btn = (Button)root.findViewById(R.id.create_btn);
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MakeStudyActivity.class);
                startActivity(intent);
            }
        });

        large_category=(Spinner)root.findViewById(R.id.large_category);
        small_category=(Spinner)root.findViewById(R.id.small_category);
        recyclerView = root.findViewById(R.id.mt_list);
        adapter_large = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_do, android.R.layout.simple_spinner_dropdown_item);
        large_category.setAdapter(adapter_large);


        large_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter_large.getItem(i).equals("전공")) {
                    l_cate = "전공";
                    adapter_small = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_do_major, android.R.layout.simple_spinner_dropdown_item);
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
                    adapter_small = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_do_language, android.R.layout.simple_spinner_dropdown_item);
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
                    adapter_small = ArrayAdapter.createFromResource(getActivity(), R.array.spinner_do_exam, android.R.layout.simple_spinner_dropdown_item);
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

        serach_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(autoCompleteTextView.getText().toString().length() <= 0){
                    getData(recyclerView);
                    upper_title.setText("스터디 검색");
                }
                else{
                    getData_2(recyclerView);
                    upper_title.setText("스터디 검색");

                }
            }
        });
        recommend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 추천 리스트 불러오기
                get_recommend_Data(recyclerView);
                upper_title.setText("추천 스터디 목록");
            }
        });

        return root;
    }

    // 자동완성 관련, 추천단어 추
    private void setting_fill_list() {
        DatabaseReference hashTagRef = FirebaseDatabase.getInstance().getReference("Hashtags");
        hashTagRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null){
                    fill_list.clear();
                }
                for (DataSnapshot tag : dataSnapshot.getChildren()) {
                    String tag_name = tag.getKey();
                    fill_list.add(tag_name);
                    Log.d("setting_fill_list added",tag_name);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }


    public void getData(RecyclerView rv) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter();
        tag_search = autoCompleteTextView.getText().toString();

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position){
                Intent intent = new Intent(getActivity(), StudyDetailActivity.class);
                intent.putExtra("study_name",listTitle.get(position) );
                intent.putExtra("study_key",listKey.get(position) );
                startActivity(intent);
            }
        });
        rv.setAdapter(adapter);

        studyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    listKey.clear();
                    listTitle.clear();
                    listPeriod.clear();
                    listTime.clear();
                    listLeader.clear();
                    listOrg.clear();
                    listInfo.clear();
                }
                for (DataSnapshot study : dataSnapshot.getChildren()) {
                    if (study.child("L_category").getValue().toString().equals(l_cate) && study.child("S_category").getValue().toString().equals(s_cate)) {
                        if (study.child("apply_status").getValue() != null) {
                            //스터디 신청 마감
                            String key = study.getKey();
                            String title = study.child("study_name").getValue().toString();
                            String s_time = study.child("s_period").getValue().toString();
                            String e_time = study.child("e_period").getValue().toString();
                            String period = s_time + " ~ " + e_time;
                            String day = study.child("study_day").getValue().toString();
                            String time = study.child("study_time").getValue().toString();
                            day = day + " " + time;
                            String leader = study.child("leader_email").getValue().toString();
                            String org = study.child("organization").getValue().toString();
                            String info = study.child("info").getValue().toString();
                            Log.v("리스트", "title" + title);
                            listKey.add(key);
                            listTitle.add(title);
                            listPeriod.add(period);
                            listTime.add(day);
                            listLeader.add(leader);
                            listOrg.add(org);
                            listInfo.add(info);
                        }
                        else {
                            String key = study.getKey();
                            String title = study.child("study_name").getValue().toString();
                            String s_time = study.child("s_period").getValue().toString();
                            String e_time = study.child("e_period").getValue().toString();
                            String period = s_time + " ~ " + e_time;
                            String day = study.child("study_day").getValue().toString();
                            String time = study.child("study_time").getValue().toString();
                            day = day + " " + time;
                            String leader = study.child("leader_email").getValue().toString();
                            String org = study.child("organization").getValue().toString();
                            String info = study.child("info").getValue().toString();
                            Log.v("리스트", "title" + title);
                            listKey.add(key);
                            listTitle.add(title);
                            listPeriod.add(period);
                            listTime.add(day);
                            listLeader.add(leader);
                            listOrg.add(org);
                            listInfo.add(info);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        Log.v("리스트", "title"+listTitle.toString());
        Log.v("리스트", "period"+listPeriod.toString());
        Log.v("리스트", "title"+listTitle.toString());

        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setPeriod(listPeriod.get(i));
            data.setTime(listTime.get(i));
            data.setLeader(listLeader.get(i));
            data.setOrg(listOrg.get(i));
            data.setInfo(listInfo.get(i));

            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }


    public void getData_2(RecyclerView rv) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter();
        tag_search = autoCompleteTextView.getText().toString();

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position){
                Intent intent = new Intent(getActivity(), StudyDetailActivity.class);
                intent.putExtra("study_name",listTitle.get(position) );
                intent.putExtra("study_key",listKey.get(position));
                startActivity(intent);
            }
        });

        rv.setAdapter(adapter);

        // 태그와 일치하는 스터디 넘버를 리스트에 넣음
        tagRef.child(tag_search).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    list_match_tag.clear();
                }
                for (DataSnapshot studyNum : dataSnapshot.getChildren()) {
                    list_match_tag.add(studyNum.getKey());

                    Log.d("list_match_tag added",studyNum.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        });

        studyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    listTitle.clear();
                    listPeriod.clear();
                    listTime.clear();
                    listLeader.clear();
                    listOrg.clear();
                    listInfo.clear();
                    listKey.clear();
                }
                for (DataSnapshot study : dataSnapshot.getChildren()) {
                    if (list_match_tag.contains(study.getKey())) {
                        if (study.child("apply_status").getValue() != null) {
                            //스터디 신청 마감
                        }
                        else {
                            String title = study.child("study_name").getValue().toString();
                            String s_time = study.child("s_period").getValue().toString();
                            String e_time = study.child("e_period").getValue().toString();
                            String period = s_time + " ~ " + e_time;
                            String day = study.child("study_day").getValue().toString();
                            String time = study.child("study_time").getValue().toString();
                            day = day + " " + time;
                            String leader = study.child("leader_email").getValue().toString();
                            String org = study.child("organization").getValue().toString();
                            String info = study.child("info").getValue().toString();
                            String key = study.getKey();
                            Log.v("리스트", "title" + title);
                            listTitle.add(title);
                            listPeriod.add(period);
                            listTime.add(day);
                            listLeader.add(leader);
                            listOrg.add(org);
                            listInfo.add(info);
                            listKey.add(key);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.v("리스트", "title"+listTitle.toString());
        Log.v("리스트", "period"+listPeriod.toString());
        Log.v("리스트", "title"+listTitle.toString());

        for (int i = 0; i < listTitle.size(); i++) {
            // 각 List의 값들을 data 객체에 set 해줍니다.
            Data data = new Data();
            data.setTitle(listTitle.get(i));
            data.setPeriod(listPeriod.get(i));
            data.setTime(listTime.get(i));
            data.setLeader(listLeader.get(i));
            data.setOrg(listOrg.get(i));
            data.setInfo(listInfo.get(i));

            adapter.addItem(data);
        }

        // adapter의 값이 변경되었다는 것을 알려줍니다.
        adapter.notifyDataSetChanged();
    }

    public void get_recommend_Data(RecyclerView rv) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(linearLayoutManager);
        adapter = new RecyclerAdapter();
        tag_search = autoCompleteTextView.getText().toString();

        adapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position){
                Intent intent = new Intent(getActivity(), StudyDetailActivity.class);
                intent.putExtra("study_name",listTitle.get(position) );
                startActivity(intent);
            }
        });

        rv.setAdapter(adapter);


        userRef.child(user_id).child("hashtag").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (interest.get(dataSnapshot.getKey()) != null) {
                        Log.d("getKeyTest",ds.getKey() + " " +interest.get(ds.getKey()).toString());
                        interest.put(ds.getKey(),Integer.parseInt(interest.get(ds.getKey()).toString()) + 10);
                    } else {
                        interest.put(ds.getKey(), 10);
                    }
                }
                userRef.child(user_id).child("hashtag_history").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        System.out.println("hashtag_history");
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (interest.get(dataSnapshot.getKey()) != null) {
                                interest.put(ds.getKey(), Integer.parseInt(interest.get(ds.getKey()).toString()) + 1);
                                System.out.println(interest.get(ds.getKey()));
                            } else {
                                interest.put(ds.getKey(),1);
                            }
                        }

                        List<Entry<String, Integer>> keySetList = new ArrayList<Entry<String, Integer>>(interest.entrySet());

                        Collections.sort(keySetList, new Comparator<Entry<String, Integer>>() {
                            // compare로 값을 비교
                            public int compare(Entry<String, Integer> obj1, Entry<String, Integer> obj2) {
                                // 오름 차순 정렬
                                return obj2.getValue().compareTo(obj1.getValue());
                            }
                        });
                        //정렬 끝.

                        int idx = 0;

                        for(Entry<String, Integer> entry : keySetList) {
                            idx += 1;
                            System.out.println("추천 검색 태그 : "+ entry.getKey() + " : " + entry.getValue());
                            // 상위태그에서,
                            // 태그 별 스터디 검색
                            // entry.getKey() : 태그

                            list_match_tag.clear();
                            tagRef.child(entry.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    for (DataSnapshot studyNum : dataSnapshot.getChildren()) {
                                        if (list_match_tag.contains(studyNum.getKey())) {
                                            continue;
                                        } else {
                                            list_match_tag.add(studyNum.getKey());
                                            Log.d("Recommend Studynum added",studyNum.getKey());
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {


                                }
                            });
                            // 상위 태그 3개까지만.
                            if (idx > 3) {
                                break;
                            }
                        }
                        // 어댑터에 추가
                        studyRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    listTitle.clear();
                                    listPeriod.clear();
                                    listTime.clear();
                                    listLeader.clear();
                                    listOrg.clear();
                                    listInfo.clear();
                                    Log.v("리스트 ","초기화됨");
                                }
                                for (DataSnapshot study : dataSnapshot.getChildren()) {
                                    if (list_match_tag.contains(study.getKey())) {
                                        String title = study.child("study_name").getValue().toString();
                                        String s_time = study.child("s_period").getValue().toString();
                                        String e_time = study.child("e_period").getValue().toString();
                                        String period = s_time + " ~ " + e_time;
                                        String day = study.child("study_day").getValue().toString();
                                        String time = study.child("study_time").getValue().toString();
                                        day = day + " " + time;
                                        String leader = study.child("leader_email").getValue().toString();
                                        String org = study.child("organization").getValue().toString();
                                        String info = study.child("info").getValue().toString();
                                        Log.v("리스트 추가됨", "title : "+ title);
                                        listTitle.add(title);
                                        listPeriod.add(period);
                                        listTime.add(day);
                                        listLeader.add(leader);
                                        listOrg.add(org);
                                        listInfo.add(info);
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        Log.v("리스트", "title"+listTitle.toString());
                        Log.v("리스트", "period"+listPeriod.toString());
                        Log.v("리스트", "title"+listTitle.toString());

                        for (int i = 0; i < listTitle.size(); i++) {
                            // 각 List의 값들을 data 객체에 set 해줍니다.
                            Data data = new Data();
                            data.setTitle(listTitle.get(i));
                            data.setPeriod(listPeriod.get(i));
                            data.setTime(listTime.get(i));
                            data.setLeader(listLeader.get(i));
                            data.setOrg(listOrg.get(i));
                            data.setInfo(listInfo.get(i));

                            adapter.addItem(data);
                        }

                        // adapter의 값이 변경되었다는 것을 알려줍니다.
                        adapter.notifyDataSetChanged();

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

