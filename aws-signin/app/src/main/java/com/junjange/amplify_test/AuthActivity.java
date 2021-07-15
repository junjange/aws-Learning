package com.junjange.amplify_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.amazonaws.mobile.auth.userpools.SignUpActivity;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignInResult;

public class AuthActivity extends AppCompatActivity {

    private final String TAG = AuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Button button = findViewById(R.id.button2); // 로그인 버튼
        Button sign_button = findViewById(R.id.signup_button); // 회원가입 버튼

        AWSMobileClient.getInstance().initialize(getApplicationContext(), new Callback<UserStateDetails>() {

            @Override
            public void onResult(UserStateDetails userStateDetails) {
                Log.i(TAG, userStateDetails.getUserState().toString());
                switch (userStateDetails.getUserState()){
                    case SIGNED_IN:
                        Intent i = new Intent(AuthActivity.this, MainActivity.class);
                        startActivity(i);

                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e(TAG, e.toString());
            }
        });

        // 로그인 버튼
        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                showSignIn();
            }
        });

        // 회원가입 버튼
        sign_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent i = new Intent(AuthActivity.this, SignUpActivity.class);
                startActivity(i);
                finish();
            }

        });
    }


    // 로그인 함수
    private void showSignIn() {

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
                                Toast.makeText(getApplicationContext(), "Sign-in done.", Toast.LENGTH_SHORT).show();
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
                Log.e(TAG, "Sign-in error", e);
            }
        });
    }
}