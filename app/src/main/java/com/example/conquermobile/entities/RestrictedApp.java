package com.example.conquermobile.entities;


import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.conquermobile.abs.AppDatabase;
import com.example.conquermobile.dao.RestrictedAppDao;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.listener.OnDateSetListener;

@Entity
public class RestrictedApp{
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String restrictedAppName;
    //app的名字,如:微信而不是com.tencent.mm
    @Ignore
    public String appName;
    @Ignore
    public long usedTimes;
    //锁屏结束时间
    private long endTime;


    public Drawable getIcon() {
        return Icon;
    }

    public void setIcon(Drawable icon) {
        Icon = icon;
    }

    public TimePickerDialog getTimePickerDialog() {
        return timePickerDialog;
    }

    public void setTimePickerDialog(TimePickerDialog timePickerDialog) {
        this.timePickerDialog = timePickerDialog;
    }
    @Ignore
    public Drawable Icon;
    @Ignore
    public TimePickerDialog timePickerDialog;

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestrictedAppName() {
        return restrictedAppName;
    }

    public void setRestrictedAppName(String restrictedAppName) {
        this.restrictedAppName = restrictedAppName;
    }

    @Override
    public String toString() {
        return "RestrictedApp{" +
                "id=" + id +
                ", restrictedAppName='" + restrictedAppName + '\'' +
                '}';
    }
}
