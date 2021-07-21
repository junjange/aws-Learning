package com.junjange.amplify_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {
    String TAG = AuthActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        Button signUp_button2 = findViewById(R.id.signUp_button2); // 회원 가입 버튼

        // 회원 가입 버튼
        signUp_button2.setOnClickListener(new View.OnClickListener() {


            // 회원가입
            @Override
            public void onClick(View v) {

                // 이름, 아이디(이메일), 비밀번호 순
                EditText signUpName =  (EditText)findViewById(R.id.signUpName);
                EditText signUpUsername =  (EditText)findViewById(R.id.signUpUsername);
                EditText signUpPassword =  (EditText)findViewById(R.id.signUpPassword);

                String name = signUpName.getText().toString();
                String username = signUpUsername.getText().toString();
                String password = signUpPassword.getText().toString();


                final Map<String, String> attributes = new HashMap<>();
                attributes.put("name", name);
                attributes.put("email", username);
                Log.d("TTT",attributes.toString());


                AWSMobileClient.getInstance().signUp(username, password, attributes, null, new Callback<SignUpResult>() {
                    @Override
                    public void onResult(final SignUpResult signUpResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                                if (!signUpResult.getConfirmationState()) {


                                    final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();
                                    Toast.makeText(getApplicationContext(), "인증 메일을 보냈습니다.: " + details.getDestination(), Toast.LENGTH_SHORT).show();

                                    // 이메일에 문제가 없으면 인증 코드 창으로 이동
                                    Intent i = new Intent(SignUpActivity.this, OkActivity.class);
                                    i.putExtra("email",username); // username을 인증 코드 창에서 사용하기 위해
                                    startActivity(i);
                                    finish();

                                } else {
                                    // 인증 코드 창으로 이동
                                    Toast.makeText(getApplicationContext(), "Sign-up done.", Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, "Sddd");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Sign-up error", e);


                        if (name.length() <= 1) {
                            errorMessage("이름을 정확히 입력하세요.");

                        }
                        else if (e.getMessage().contains("An account with the given email already exists.")){

                            errorMessage("주어진 이메일을 가진 계정이 이미 존재합니다.");

                        }
                        else if (e.getMessage().contains("Value at 'username' failed to satisfy constraint")){
                            errorMessage("이메일을 입력해주세요.");

                        }
                        else if (e.getMessage().contains("Invalid email address format.")){

                            errorMessage("잘못된 이메일 주소 형식입니다.");
                        }
                        else if (e.getMessage().contains("Value at 'password' failed to satisfy constraint")){

                            errorMessage("비밀번호를 입력해주세요");
                        }
                        else if (e.getMessage().contains("Password did not conform with policy: Password not long enough")){

                            errorMessage("비밀번호는 8자 이상이어야 하며 특수 문자를 반드시 포함해야 합니다.");
                        }
                    }
                });
            }

        });

    }

    @Override
    public void onBackPressed() {

            Intent i = new Intent(SignUpActivity.this, AuthActivity.class);
            startActivity(i);
            finish();
    }


    // 에러 메시지
    public void errorMessage(String message){
        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}