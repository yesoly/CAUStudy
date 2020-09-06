package com.example.caustudy.MemberManagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caustudy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.StringTokenizer;


public class Dialog_rating_inquiry {



    private Context context;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("StudyList");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    private ArrayList<String> fill_list;
    private RatingHistoryViewAdapter rating_view_adapter;
    RecyclerView recyclerView;


    public Dialog_rating_inquiry(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String id_mem) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.activity_dialog_rating_inquiry);
        recyclerView = dlg.findViewById(R.id.rating_history_view);
        rating_view_adapter = new RatingHistoryViewAdapter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this.context);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(rating_view_adapter);
        rating_view_adapter.update_rates_view(id_mem);

        Log.d("Why adapter not update?",String.valueOf(rating_view_adapter.getItemCount()));




        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        //final EditText message = (EditText) dlg.findViewById(R.id.mesgase);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
    // 자동완성 리스트에 단어 추가

}