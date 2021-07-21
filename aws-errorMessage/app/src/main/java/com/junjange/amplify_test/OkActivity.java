package com.junjange.amplify_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amazonaws.mobile.client.results.UserCodeDeliveryDetails;

public class OkActivity extends AppCompatActivity {
    String TAG = AuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ok);

        // 인증 확인 버튼
        Button Ok_button = findViewById(R.id.Ok_button);
        // 인증 재전송 버튼
        Button Re_Ok_button = findViewById(R.id.Re_Ok_button);

        // SignUpActivity 에서 사용된 username 정보를 가져와 TextView에 넣는다.
        TextView TextView = findViewById(R.id.signUpUsername2);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String username = bundle.getString("email");
        TextView.setText(username);

        // 인증 버튼
        Ok_button.setOnClickListener(new View.OnClickListener() {

            // 인증 코드 확인
            @Override
            public void onClick(View v) {

                EditText code_name = findViewById(R.id.code_name);
                String code = code_name.getText().toString();

                AWSMobileClient.getInstance().confirmSignUp(username, code, new Callback<SignUpResult>() {
                    @Override
                    public void onResult(final SignUpResult signUpResult) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Log.d(TAG, "Sign-up callback state: " + signUpResult.getConfirmationState());
                                if (!signUpResult.getConfirmationState()) {
                                    final UserCodeDeliveryDetails details = signUpResult.getUserCodeDeliveryDetails();

                                    Toast.makeText(getApplicationContext(), "Confirm sign-up with: " + details.getDestination(), Toast.LENGTH_SHORT).show();


                                } else {

                                    // 회원가입이 완료되면 로그인 창으로 이동
                                    Toast.makeText(getApplicationContext(), "성공적으로 회원가입 되셨습니다.", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(OkActivity.this, AuthActivity.class);
                                    startActivity(i);
                                    finish();

                                }
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, "Confirm sign-up error", e);

                        if (e.getMessage().contains("Value '' at 'confirmationCode' failed to satisfy constraint")){

                            errorMessage("인증 코드를 입력해주세요.");

                        } else if (e.getMessage().contains("Invalid verification code provided, please try again.")){

                            errorMessage("인증 코드를 다시 확인해주세요.");
                        }
                    }
                });


            }

        });

        // 인증 코드 재전송 버튼
        Re_Ok_button.setOnClickListener(new View.OnClickListener() {

            // 인증 코드 재전송
            @Override
            public void onClick(View v) {

                AWSMobileClient.getInstance().resendSignUp(username, new Callback<SignUpResult>() {
                    @Override
                    public void onResult(SignUpResult signUpResult) {
                        Log.i(TAG, "A verification code has been sent via" +
                                signUpResult.getUserCodeDeliveryDetails().getDeliveryMedium()
                                + " at " +
                                signUpResult.getUserCodeDeliveryDetails().getDestination());
                        Toast.makeText(getApplicationContext(), "인증 메일이 재전송 되었습니다.", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e(TAG, String.valueOf(e));

                    }
                });
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


        // 뒤로 가기 할 경우 로그인 화면으로 이동
        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            Intent i = new Intent(OkActivity.this, AuthActivity.class);
            startActivity(i);

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

