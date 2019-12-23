package com.app.timemanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import com.cuiweiyou.numberpickerdialog.NumberPickerDialog;
import com.app.timemanager.manage.AppManageList;
import com.app.timemanager.manage.QuickTomato;
import com.app.timemanager.R;


public class AppManagement extends AppCompatActivity implements NumberPicker.OnValueChangeListener{
    NumberPickerDialog numberPickerDialog;
    public final static String saveName = "AppManagement";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_management);
        findViewById(R.id.btn_contraint_usage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppManagement.this, AppManageList.class);
                startActivity(intent);
            }
        });

        numberPickerDialog = new NumberPickerDialog(this,this,90, 20, 20);
        numberPickerDialog.setTitle(R.string.quick_tomato_title);
        numberPickerDialog.setInverseBackgroundForced(true);
        findViewById(R.id.btn_quick_tomato).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberPickerDialog.show();
            }
        });
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        Log.d("HQS", "newVal oldVal "+oldVal+" "+newVal);
        if(oldVal==newVal&&newVal==0){
            newVal = 20;
        }
        SharedPreferences shp = getSharedPreferences(saveName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shp.edit();
        long now = System.currentTimeMillis();
        now += newVal*60*1000;
        editor.putLong("howLong", now);
        editor.apply();
        Intent intent = new Intent(this, QuickTomato.class);
        startService(intent);
    }
}
