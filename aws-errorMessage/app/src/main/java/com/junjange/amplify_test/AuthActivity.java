package com.junjange.amplify_test;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserState;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;

public class AuthActivity extends AppCompatActivity {

    private final String TAG = AuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Button signIn_button = findViewById(R.id.signIn_button); // 로그인 버튼
        Button signUp_button = findViewById(R.id.signUp_button); // 회원가입 버튼
        Button forgot_Password_button = findViewById(R.id.forgot_Password_button); // 비밀번호를 잊어버리셨나요?

        // 로그인이 되어있는지 확인
        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

            @Override
            public void onResult(UserStateDetails userStateDetails) {
                Log.i(TAG, userStateDetails.getUserState().toString());

                // 로그인이 되어있으면 MainActivity 로 이동
                if (userStateDetails.getUserState() == UserState.SIGNED_IN) {
                    Intent i = new Intent(AuthActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.toString());
            }
        });

        // 로그인 버튼
        signIn_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showSignIn();
            }
        });

        // 회원가입 버튼
        signUp_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(AuthActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }

        });

        // 비밀번호를 잊어버리셨나요?
        forgot_Password_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(AuthActivity.this, ForgotActivity.class);
                startActivity(i);
                finish();
            }

        });
    }


    // 로그인 함수
    private void showSignIn() {

        // 아이디 비밀번호 순
        EditText login_id = findViewById(R.id.login_id);
        EditText login_paw = findViewById(R.id.login_paw);

        String username = login_id.getText().toString();
        String password = login_paw.getText().toString();


        AWSMobileClient.getInstance().signIn(username, password, null, new Callback<SignInResult>() {
            @Override
            public void onResult(final SignInResult signInResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "Sign-in callback state: " + signInResult.getSignInState());
                        switch (signInResult.getSignInState()) {
                            case DONE:
                                Toast.makeText(getApplicationContext(), "환영합니다!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(AuthActivity.this, MainActivity.class);
                                startActivity(i);
                                finish();
                                break;
                            case SMS_MFA:
                                Toast.makeText(getApplicationContext(), "Please confirm sign-in with SMS.", Toast.LENGTH_SHORT).show();
                                break;
                            case NEW_PASSWORD_REQUIRED:
                                Toast.makeText(getApplicationContext(), "Please confirm sign-in with new password.", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(getApplicationContext(), "Unsupported sign-in confirmation: " + signInResult.getSignInState(), Toast.LENGTH_SHORT).show();
                                break;
                        }

                    }
                });
            }


            @Override
            public void onError(Exception e) {

                Log.e(TAG, "Sign-in error : " + e.getMessage());

                if (e.getMessage().contains("Missing required parameter USERNAME")) {


                    errorMessage("아이디와 비밀번호를 입력해주세요.");

                } else if (e.getMessage().contains("Incorrect username or password.")) {
                    errorMessage("아이디와 비밀번호가 일치하지 않습니다.");

                } else if (e.getMessage().contains("User does not exist.")) {
                    errorMessage("존재하지 않는 아이디입니다.");

                } else if (e.getMessage().contains("Unable to execute HTTP request")) {

                    errorMessage("네트워크가 원활하지 않습니다.\n네트워크 연결 상태를 확인하세요.");

                } else if (e.getMessage().contains("User is not confirmed.")) {


                    // 다이어로그 생성
                    Handler mHandler = new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder ad = new AlertDialog.Builder(AuthActivity.this);
                            ad.setIcon(R.mipmap.ic_launcher);
                            ad.setTitle("인증 코드 미승인");
                            ad.setMessage("인증 코드를 승인하지 않았습니다.\n인증 코드를 승인하러 가시겠습니까?");

                            // 확인버튼
                            ad.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    // 이메일에 문제가 없으면 인증 코드 창으로 이동
                                    Intent i = new Intent(AuthActivity.this, OkActivity.class);
                                    i.putExtra("email",username); // username을 인증 코드 창에서 사용하기 위해
                                    startActivity(i);
                                    finish();
                                    dialog.dismiss();
                                }
                            });

                            // 취소버튼
                            ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            ad.show();
                        }
                    });

                }
            }
        });
    }

    // 뒤로가기 2번 눌러야 종료
    private final long FINISH_INTERVAL_TIME = 2500;
    private long backPressedTime = 0;
    private  Toast toast;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;


        // 뒤로 가기 할 경우 홈 화면으로 이동
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
            // 뒤로가기 토스트 종료
            toast.cancel();
            finish();

        } else {
            backPressedTime = tempTime;
            toast=Toast.makeText(getApplicationContext(), "'뒤로'버튼 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
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