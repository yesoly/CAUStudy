package com.example.caustudy.ui.Setting;



import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.caustudy.MainActivity;
import com.example.caustudy.R;
import com.example.caustudy.ui.searchstudy.SearchViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.StringTokenizer;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Dialog_Hashtag {

    private Context context;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("StudyList");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    private ArrayList<String> fill_list;

    public Dialog_Hashtag(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.hashtag_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();


        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        //final EditText message = (EditText) dlg.findViewById(R.id.mesgase);

        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);


        // 자동완성
        final AutoCompleteTextView message = (AutoCompleteTextView)dlg.findViewById(R.id.autoCompleteTextView);
        fill_list = new ArrayList<String>();
        // 자동완성 -> 추천 단어들 불러오기
        setting_fill_list();
        // 자동완성 -> Array 어댑터 연결. 여기서, dlg.getContext() 자리에 보통 getActivity()들어가는듯. ?
        message.setAdapter(new ArrayAdapter<String>( dlg.getContext(), R.layout.support_simple_spinner_dropdown_item, fill_list));


        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                userRef.child(user_id).child("hashtag").child(message.getText().toString()).setValue(1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "\"" +  message.getText().toString() + "\" 을 관심 태그에 추가하였습니다.", Toast.LENGTH_SHORT).show();
                        ((Setting_HashtagActivity)Setting_HashtagActivity.mContext).show_data();
                    }
                });
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
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

    public void callFunction(final TextView main_label) {
        // 이 코드가 돌아가나?? 아직 이 함수 수정안함


        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.hashtag_dialog);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        final EditText message = (EditText) dlg.findViewById(R.id.autoCompleteTextView);
        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                main_label.setText(message.getText().toString());
                Toast.makeText(context, "\"" +  message.getText().toString() + "\" 을 입력하였습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}