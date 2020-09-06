package com.example.caustudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class SignInActivity extends AppCompatActivity {

    private static final Pattern PWD_RULE = Pattern.compile("^[a-zA-Z0-9!@.#$%^&*?_~]{4,16}$");
    private static final Pattern EMAIL_RULE=Pattern.compile("^[a-zA-Z0-9]+@cau.ac.kr+$");
    ProgressDialog dialog;
    private EditText email_login;
    private EditText pwd_login;
    private Button login;
    private Button find_password;
    private String email="";
    private String pwd="";
    private CheckBox autoLogin;

    FirebaseAuth firebaseAuth;
    FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        boolean autologin=PreferenceUtil.getInstance(this).getBooleanExtra("AutoLogin");
        pwd_login=(EditText)findViewById(R.id.pwd_in);
        email_login=(EditText) findViewById(R.id.email_in);
        autoLogin=(CheckBox)findViewById(R.id.autoLogin);
        login =(Button)findViewById(R.id.signIn);
        find_password =(Button)findViewById(R.id.findPW);
        firebaseAuth=FirebaseAuth.getInstance();
        autoLogin.setChecked(autologin);

        if(autologin){
            load();
        }

        find_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, FindPW_Activity.class);
                startActivity(intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }



    public void signIn(){
        if(mFirebaseUser!=null)
            FirebaseAuth.getInstance().signOut();

        email=email_login.getText().toString();
        pwd=pwd_login.getText().toString();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"이메일을 입력해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwd)){
            Toast.makeText(this,"비밀번호를 입력해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }

        //이메일 인증을 했을 경우에만, 로그인이 가능
        if(isValidEmail()&&isValidPwd()){
            firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        mFirebaseUser=firebaseAuth.getCurrentUser();
                        if(mFirebaseUser!=null){
                            if(!(mFirebaseUser.isEmailVerified())){
                                Toast.makeText(SignInActivity.this,"이메일 인증이 필요합니다.",Toast.LENGTH_LONG).show();
                                return;
                            }else{
                                email_login.setText(null);
                                pwd_login.setText(null);

                                dialog=ProgressDialog.show(SignInActivity.this,"로그인 중 입니다.","잠시만 기다려주세요.");
                                if(autoLogin.isChecked()){
                                    PreferenceUtil.getInstance(getApplicationContext()).putBooleanExtra("AutoLogin", true);
                                    try {
                                        PreferenceUtil.getInstance(getApplicationContext()).putStringExtra("LoginID", AES.AES_Encode(email));
                                        PreferenceUtil.getInstance(getApplicationContext()).putStringExtra("LoginPW", AES.AES_Encode(pwd));
                                    } catch (UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchPaddingException e) {
                                        e.printStackTrace();
                                    } catch (InvalidKeyException e) {
                                        e.printStackTrace();
                                    } catch (InvalidAlgorithmParameterException e) {
                                        e.printStackTrace();
                                    } catch (IllegalBlockSizeException e) {
                                        e.printStackTrace();
                                    } catch (BadPaddingException e) {
                                        e.printStackTrace();
                                    }

                                }else{
                                    PreferenceUtil.getInstance(getApplicationContext()).putBooleanExtra("AutoLogin", false);
                                    PreferenceUtil.getInstance(getApplicationContext()).removePreference("LoginID");
                                    PreferenceUtil.getInstance(getApplicationContext()).removePreference("LoginPW");
                                }
                                //이메일은 사실상 고유한 아이디임 @빼고 넣어야 구독가능 특수문자x
                                final StringTokenizer st = new StringTokenizer(mFirebaseUser.getEmail(), "@");
                                //잘안보이면 .마다 엔터해주기

                                FirebaseMessaging.getInstance().subscribeToTopic(st.nextToken() + st.nextToken())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                // jesnk : For testing
                                                //startActivity(new Intent(getApplicationContext(), JMainActivity.class));

                                                startActivity(new Intent(getApplicationContext(),MainActivity.class));

                                                dialog.dismiss();
                                                finish();
                                                //구독이 잘되었으면 로그인
                                                //기기 구독 방식임 앱 고유의 토큰을 사용
                                            }
                                        });

                            }
                        }else {
                            Toast.makeText(SignInActivity.this,"아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        } else{
            Toast.makeText(SignInActivity.this,"아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private boolean isValidEmail() {
        if (email.isEmpty()) {
            return false;
        }else if(!EMAIL_RULE.matcher(email).matches()){
            return false;
        }else{
            return true;
        }
    }

    private boolean isValidPwd(){
        if(pwd.isEmpty()){
            return false;
        }else if(!PWD_RULE.matcher(pwd).matches()){
            return false;
        }else{
            return true;
        }
    }

    private void load() {
        try {
            email_login.setText(AES.AES_Decode(PreferenceUtil.getInstance(this).getStringExtra("LoginID")));
            pwd_login.setText(AES.AES_Decode(PreferenceUtil.getInstance(this).getStringExtra("LoginPW")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }
}