package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static android.net.sip.SipErrorCode.TIME_OUT;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();
    private DatabaseReference myRef=database.getReference("사용자");


    private static final Pattern PWD_RULE=Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{6,16}$");
    private static final Pattern EMAIL_RULE=Pattern.compile("^[a-zA-Z0-9]+@cau.ac.kr+$");
    private EditText email_e,pwd_e,check_pwd,name_e, hashtag_e;
    private TextView check_show;
    private Spinner L_cate, S_cate;
    ArrayAdapter<CharSequence> adapter_large, adapter_small; //어댑터를 선언
    private Button signup_btn;
    long count=0;
    private String email="";
    private String pwd="";
    private String name="";
    private String L_deptname="";
    private String S_deptname="";
    private String hashtag="";
    public static int TM_OUT = 1001;

    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email_e=(EditText)findViewById(R.id.email);
        pwd_e=(EditText)findViewById(R.id.password);
        check_pwd=(EditText)findViewById(R.id.checkpwd);
        name_e=(EditText)findViewById(R.id.name);
        check_show=(TextView)findViewById(R.id.checkText);
        signup_btn=(Button)findViewById(R.id.sign_up);
        hashtag_e = (EditText)findViewById(R.id.hashtag);
        L_cate=(Spinner)findViewById(R.id.large_dept);
        S_cate=(Spinner)findViewById(R.id.small_dept);
        adapter_large = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.spinner_dept, android.R.layout.simple_spinner_dropdown_item);
        L_cate.setAdapter(adapter_large);

        L_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapter_large.getItem(i).equals("공과대학")) {
                    L_deptname = "공과대학";
                    adapter_small = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.spinner_dept_engineering, android.R.layout.simple_spinner_dropdown_item);
                    adapter_small.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    S_cate.setAdapter(adapter_small);
                    S_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            S_deptname = adapter_small.getItem(i).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adapter_large.getItem(i).equals("창의ICT공과대학")) {
                    L_deptname = "창의ICT공과대학";
                    adapter_small = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.spinner_dept_ict_engineering, android.R.layout.simple_spinner_dropdown_item);
                    adapter_small.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    S_cate.setAdapter(adapter_small);
                    S_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            S_deptname = adapter_small.getItem(i).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else if (adapter_large.getItem(i).equals("경영경제대학")) {
                    L_deptname = "경영경제대학";
                    adapter_small = ArrayAdapter.createFromResource(SignUpActivity.this, R.array.spinner_dept_management, android.R.layout.simple_spinner_dropdown_item);
                    adapter_small.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    S_cate.setAdapter(adapter_small);
                    S_cate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            S_deptname = adapter_small.getItem(i).toString();
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

        //비밀번호 일치하는지 확인, 일치 시에 회원가입 가능
        check_pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String temp1=pwd_e.getText().toString();
                String temp2=check_pwd.getText().toString();

                if(temp1.equals(temp2)){
                    check_show.setText("일치");
                    signup_btn.setEnabled(true);
                }else{
                    check_show.setText("불일치");
                }
            }
        });

        //회원가입을 누르면 유저를 등록
        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
                email_e.setText(null);
                pwd_e.setText(null);
                check_pwd.setText(null);
                name_e.setText(null);
            }
        });


    }

    //이메일 인증 메일 요청 기다리기 위함
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == TM_OUT) {
                dialog.dismiss();
            }
        }
    };

    //이메일 유효성 검사 @cau.ac.kr 형식만 허용
    private boolean isValidEmail() {
        if(email.isEmpty()){
            return false;
        }else {
            return EMAIL_RULE.matcher(email).matches();
        }
    }

    //비밀번호 유효성 검사 (특수문자, 숫자)
    private boolean isValidPwd(){
        if(pwd.isEmpty()){
            return false;
        }else return PWD_RULE.matcher(pwd).matches();
    }

    public void registerUser(){
        email=email_e.getText().toString();
        pwd=pwd_e.getText().toString();
        name=name_e.getText().toString();
        hashtag=hashtag_e.getText().toString();
        if(isValidEmail()==false){
            Toast.makeText(SignUpActivity.this,"중앙대학교 이메일을 입력해주세요",Toast.LENGTH_SHORT).show();
        }
        if(isValidEmail() && isValidPwd()){
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.useAppLanguage();
            signupUser(email, pwd);
        }
    }

    //회원 가입, firebase 이메일 인증 방식
    private void signupUser(final String email,String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    mFirebaseUser = firebaseAuth.getCurrentUser();
                    if (mFirebaseUser != null){
                        mFirebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "인증 메일 전송:" + mFirebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                                    dialog = ProgressDialog.show(SignUpActivity.this, "회원가입이 완료되었습니다.", mFirebaseUser.getEmail() + "으로 인증메일이 전송되었습니다.", true);
                                    mHandler.sendEmptyMessageDelayed(TM_OUT, 2000);

                                    //데이터베이스에 유저를 등록
                                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                count++;
                                            }

                                            User user = new User(email, name, L_deptname, S_deptname);
                                            StringTokenizer stringTokenizer = new StringTokenizer(email, "@");
                                            StringTokenizer hashTokenizer = new StringTokenizer(hashtag, "#");
                                            myRef.child(stringTokenizer.nextToken()).setValue(user);
                                            while(hashTokenizer.hasMoreTokens()) {
                                                String hashtag = hashTokenizer.nextToken();
                                                hashtag = hashtag.trim();
                                                myRef.child(stringTokenizer.nextToken()).child("hashtag").child(hashtag).setValue(1);;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                } else {
                                    Toast.makeText(SignUpActivity.this, "인증 메일 전송을 실패했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
                else {
                    Toast.makeText(SignUpActivity.this, "회원가입에 실패하셨습니다.", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}