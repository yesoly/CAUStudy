package com.example.caustudy.MemberManagement;


import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.caustudy.R;
import com.example.caustudy.ui.Setting.Setting_HashtagActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Dialog_member_evaluation {

    private RatingBar ratingBar_indicator;
    private Context context;
    DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("StudyList");
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("사용자");
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser userAuth = mAuth.getCurrentUser();
    StringTokenizer stringTokenizer = new StringTokenizer(userAuth.getEmail(), "@");
    String user_id = stringTokenizer.nextToken();
    private ArrayList<String> fill_list;

    String study_key;
    String email_mem;
    String name_mem;
    String id_mem;

    public Dialog_member_evaluation(Context context) {

        this.context = context;

    }

    public Dialog_member_evaluation(String mem_email, String study_key) {
        this.study_key = study_key;
        this.email_mem = mem_email;
        this.id_mem = id_mem;
        this.name_mem = name_mem;
    }


    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String study_key, String member_id) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.activity_dialog_member_evaluation);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        this.id_mem = member_id;

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        //final EditText message = (EditText) dlg.findViewById(R.id.mesgase);

        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        final EditText feedback = (EditText) dlg.findViewById(R.id.textView_member_feedback_edittext);
        ratingBar_indicator = (RatingBar) dlg.findViewById(R.id.ratingBarInficator);
        Log.d("Rating bar call function","start");
        ratingBar_indicator.setOnRatingBarChangeListener(new Listener());



        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
                userRef.child(id_mem).child("ratings").child(study_key).child("rate").setValue(String.valueOf(ratingBar_indicator.getRating()));
                //userRef.child(id_mem).child("ratings").child(study_key).child("study_name").setValue(String.valueOf(ratingBar_indicator.getRating()));


                if (feedback.getText() != null) {
                    userRef.child(id_mem).child("ratings").child(study_key).child("feedback").setValue(feedback.getText().toString());

                } else {
                    userRef.child(id_mem).child("ratings").child(study_key).child("feedback").setValue("-");
                }
                // 커스텀 다이얼로그를 종료한다.
                ((MemberManagementActivity)MemberManagementActivity.mContext).update_view();
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


    class Listener implements RatingBar.OnRatingBarChangeListener
    {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            ratingBar_indicator.setRating(rating);
            Log.d("rating bar rating changed",String.valueOf(rating));
        }
    }
}