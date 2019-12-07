package com.example.conquermobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JoinGroup extends AppCompatActivity {

    private Button join;
    private TextView group;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        join=(Button)findViewById(R.id.group_button);
        group=(TextView)findViewById(R.id.group);
        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("group",group.getText().toString());
                setResult(0,i);
                finish();
            }
        });
    }
}
