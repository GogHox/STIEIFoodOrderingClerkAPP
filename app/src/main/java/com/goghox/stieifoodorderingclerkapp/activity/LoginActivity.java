package com.goghox.stieifoodorderingclerkapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.goghox.stieifoodorderingclerkapp.AppConstant;
import com.goghox.stieifoodorderingclerkapp.R;
import com.goghox.stieifoodorderingclerkapp.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import okhttp3.OkHttpClient;


/**
 * Created by GogHox on 2017/11/26.
 */

public class LoginActivity extends AppCompatActivity {
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private String TAG = "LoginActivity";

    private final int MSG_NETWORK_ERROR = 1;
    private final int MSG_SUCCESS = 2;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MSG_NETWORK_ERROR:
                    break;
                case MSG_SUCCESS:
                    finish();
                    startActivity(new Intent(getBaseContext(), MainActivity.class));
                    break;
            }
            return true;
        }
    });
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        final String username = etUsername.getText().toString().trim();
        final String password = etPassword.getText().toString().trim();
        if(username.isEmpty() || password.isEmpty()){
            Toast.makeText(this, "Please input username or password!", Toast.LENGTH_SHORT).show();
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        if(okHttpClient == null){
            okHttpClient = new OkHttpClient();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String json = NetworkUtils.login(username, password);

                    if(json == null){
                        mHandler.sendEmptyMessage(MSG_NETWORK_ERROR);
                        return;
                    }
                    JSONObject jo = new JSONObject(json);
                    AppConstant.name = jo.getString("name");
                    AppConstant.token = jo.getString("token");

                    if(AppConstant.token != null || !AppConstant.token.isEmpty()) {
                        mHandler.sendEmptyMessage(MSG_SUCCESS);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_NETWORK_ERROR);
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_NETWORK_ERROR);
                }
            }
        }).start();

    }
}
