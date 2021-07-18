package com.junjange.amplify_test;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button signOut_button = findViewById(R.id.signOut_button); // 로그아웃 버튼

        // 로그아웃 버튼
        signOut_button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {
                    @Override
                    public void onResult(UserStateDetails userStateDetails) {
                        // 로그아웃 후 로그인 창으로 이동
                        AWSMobileClient.getInstance().signOut();
                        Intent i = new Intent(MainActivity.this, AuthActivity.class);
                        startActivity(i);
                        finish();
                    }

                    @Override
                    public void onError(Exception e) {
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
}