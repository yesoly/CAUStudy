package com.example.caustudy;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.caustudy.ui.Setting.Setting_HashtagActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Dialog_Todo {

    private Context context;
    DatabaseReference studyRef = FirebaseDatabase.getInstance().getReference("Study");
    private DatePickerDialog.OnDateSetListener callbackMethod;
    String day;
    public Dialog_Todo(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String studyKey) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);
        final String studyNum = studyKey;
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_make_todo);

        // 커스텀 다이얼로그를 노출한다.
        dlg.show();

        final Button okButton = (Button) dlg.findViewById(R.id.okButton);
        final Button cancelButton = (Button) dlg.findViewById(R.id.cancelButton);
        final EditText todo_num = (EditText) dlg.findViewById(R.id.new_todo_num);
        final EditText todo_topic = (EditText) dlg.findViewById(R.id.new_todo_topic);
        final EditText todo_day = (EditText) dlg.findViewById(R.id.new_todo_day);

        todo_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dayOnClickHandler(v, todo_day);
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String num = todo_num.getText().toString();
                Todo todo = new Todo(num, todo_topic.getText().toString(), day, "0");
                studyRef.child(studyNum).child("todo_list").child(num).setValue(todo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "새 스터디 일정 등록완료.", Toast.LENGTH_SHORT).show();
                        //((MakeTodoActivity)MakeTodoActivity.mContext).refresh_nextSchedule_view();
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

    public void dayOnClickHandler(View view, EditText todo_day){
        callbackMethod = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                day = String.format("%d-%d-%d", year, monthOfYear+1, dayOfMonth);
                todo_day.setText(day);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this.context, callbackMethod, 2020, 5, 17);
        dialog.show();
    }
}