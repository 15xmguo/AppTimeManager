package com.example.conquermobile;

/**
 * Created by mlyang on 2016/11/20.
 */

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class Login extends AppCompatActivity implements View.OnClickListener {

    private Button login;
    private TextView user, psw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //AVAnalytics.trackAppOpened(getIntent());
        initView();
        initEvent();
    }

    private void initEvent() {
        login.setOnClickListener(this);
    }

    private void initView() {
        login = (Button) findViewById(R.id.sign_in_button);
        user = (TextView) findViewById(R.id.user);
        psw = (TextView) findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                Toast.makeText(getApplicationContext(), "登陆成功", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                i.putExtra("name",user.getText().toString());
                setResult(1,i);
                finish();
                break;
        }
    }

    private void show_main_screen() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
    }
}
