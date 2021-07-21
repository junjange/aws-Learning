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
import com.amazonaws.mobile.client.results.ForgotPasswordResult;
import com.amazonaws.mobile.client.results.ForgotPasswordState;

public class ForgotActivity extends AppCompatActivity {

    private final String TAG = AuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        Button code_button = findViewById(R.id.code_button); // 인증 코드 버튼
        Button new_paw_button = findViewById(R.id.new_paw_button); // 비밀번호 재설정 버튼


        // 인증 버튼
        code_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                // 인증코드 확인
                EditText paw_signUpUsername = (EditText) findViewById(R.id.paw_signUpUsername);
                String username = paw_signUpUsername.getText().toString();

                AWSMobileClient.getInstance().forgotPassword(username, new Callback<ForgotPasswordResult>() {
                    @Override
                    public void onResult(final ForgotPasswordResult result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "forgot password state: " + result.getState());
                                if (result.getState() == ForgotPasswordState.CONFIRMATION_CODE) {
                                    Toast.makeText(getApplicationContext(), "이메일 주소로 인증 코드가 전송되었습니다.", Toast.LENGTH_SHORT).show();


                                } else {
                                    Log.e(TAG, "un-supported forgot password state");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "forgot password error", e);


                        if (e.getMessage().contains("Value at 'username' failed to satisfy constraint")){

                            errorMessage("이메일을 입력해주세요.");

                        }
                        else if (e.getMessage().contains("Username/client id combination not found.")){
                            errorMessage("이메일 주소와 일치하는 회원이 없습니다.");

                        }
                    }
                });
            }
        });



        // 비밀번호 재설정 버튼
        new_paw_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // 인증코드, 비밀번호 재설정
                EditText paw_code_name = (EditText) findViewById(R.id.paw_code_name);
                EditText new_paw_name = (EditText) findViewById(R.id.new_paw_name);


                String CONFIRMATION_CODE = paw_code_name.getText().toString();
                String NEW_PASSWORD_HERE = new_paw_name.getText().toString();


                AWSMobileClient.getInstance().confirmForgotPassword(NEW_PASSWORD_HERE, CONFIRMATION_CODE, new Callback<ForgotPasswordResult>() {
                    @Override
                    public void onResult(final ForgotPasswordResult result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "forgot password state: " + result.getState());
                                if (result.getState() == ForgotPasswordState.DONE) {

                                    // 비밀번호가 재설정 되었스면 로그인 창으로 이동
                                    Toast.makeText(getApplicationContext(), "성공적으로 비밀번호가 재설정 되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(ForgotActivity.this, AuthActivity.class);
                                    startActivity(i);
                                    finish();

                                } else {
                                    Log.e(TAG, "un-supported forgot password state");
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "forgot password error", e);

                        if (e.getMessage().contains(" Value '' at 'confirmationCode' failed to satisfy constraint")){
                            errorMessage("인증 코드를 입력해주세요.");

                        }

                        else if (e.getMessage().contains("Invalid verification code provided, please try again.")){

                            errorMessage("인증 코드를 다시 확인해주세요.");

                        }

                        else if (e.getMessage().contains("Value at 'password' failed to satisfy constraint")){
                            errorMessage("비밀번호는 8자 이상이어야 하며 특수 문자를 반드시 포함해야 합니다.");

                        }
                        else if (e.getMessage().contains("Password did not conform with policy")){
                            errorMessage("비밀번호는 8자 이상이어야 하며 특수 문자를 반드시 포함해야 합니다.");

                        }
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {

            Intent i = new Intent(ForgotActivity.this, AuthActivity.class);
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