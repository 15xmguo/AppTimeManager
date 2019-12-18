package com.example.conquermobile.manage;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;


import com.example.conquermobile.R;
import com.example.conquermobile.abs.AppDatabase;
import com.example.conquermobile.dao.RestrictedAppDao;
import com.example.conquermobile.entities.RestrictedApp;
import com.example.conquermobile.lib.BackgroundUtil;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;

import java.util.List;


public class WatchDogService extends Service {
    public static final String TAG = "WatchDogService";
    RestrictedAppDao restrictedAppDao;
    List<RestrictedApp> restrictedApps;
    static int count = 0;
    private long endTime;
    View view;
    @Override
    public void onCreate() {
        restrictedAppDao = AppDatabase.getInstance(getApplicationContext()).getRestrictedAppDao();
        getRestrictedApps();
        //设置锁屏所展示的view
        view = LayoutInflater.from(this).inflate(R.layout.activity_pop_out, null);
        startCheck();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private static final int FLOAT_WINDOW_OPEN=0;
    private static final int FLOAT_WINDOW_CLOSE=1;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what){
                case FLOAT_WINDOW_OPEN:
                    floatWindowOpen();
                    break;
                case FLOAT_WINDOW_CLOSE:
                    FloatWindow.get("new").hide();
                    break;
            }
        };
    };

    private void floatWindowOpen(){
        if(FloatWindow.get("new")!=null){
            changeView();
            FloatWindow.get("new").show();
        }else{
            changeView();
            FloatWindow
                    .with(getApplicationContext())
                    .setView(view)
                    .setWidth(Screen.width, 1f)                               //设置控件宽高
                    .setHeight(Screen.height,1.2f)
                    .setX(Screen.width, 0f)                                   //设置控件初始位置
                    .setY(Screen.height,0f)
                    .setTag("new")
                    .setMoveType(MoveType.inactive)
                    .build();
        }
    }

    private void getRestrictedApps() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                restrictedApps = restrictedAppDao.getAllRestrictedApps();
            }
        }).start();
    }

    private boolean isOkForPrevent(String packageName){
        RestrictedApp restrictedApp = restrictedAppDao.getAppByName(packageName);
        if((restrictedApp!=null)&&(restrictedApp.getEndTime() <= System.currentTimeMillis())) {
            restrictedAppDao.delete(restrictedApp);
        }
        if((restrictedApp!=null)&&(restrictedApp.getEndTime() >= System.currentTimeMillis())){
            endTime = restrictedApp.getEndTime();
            return true;
        }else{
            return false;
        }
    }

    private void startCheck(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (true){
                    String packageName = BackgroundUtil.queryUsageStats(getApplicationContext());
                    Log.d(TAG, packageName);
                    if(count==1 && packageName.equals("android")){
                        Log.d(TAG, "阻止"+packageName+"打开");
                        handler.sendEmptyMessage(FLOAT_WINDOW_OPEN);
                    }
                    if(isOkForPrevent(packageName)){
                        count += 1;
                        Log.d(TAG, "阻止"+packageName+"打开");
                        handler.sendEmptyMessage(FLOAT_WINDOW_OPEN);
                    } else if(packageName.startsWith("com.")&&FloatWindow.get("new")!=null){
                        count = 0;
                        handler.sendEmptyMessage(FLOAT_WINDOW_CLOSE);
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public void changeView(){
        long duration  = endTime - System.currentTimeMillis();
        duration /= 1000;
        duration /= 60;
        long hour = duration/60;
        long min = duration%60;
        String info="加油，在";
        if(hour>0){
            info += ""+hour+"小时";
        }
        if(min>0){
            info += ""+min+"分钟";
        }
        if(hour+min==0){
            info+="剩下的1分钟内";
        }
        info = info+"里继续努力吧!";
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(info);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatWindow.destroy();
    }
}
