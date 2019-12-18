package com.example.conquermobile.manage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import com.example.conquermobile.AppManagement;
import com.example.conquermobile.R;
import com.yhao.floatwindow.FloatWindow;
import com.yhao.floatwindow.MoveType;
import com.yhao.floatwindow.Screen;


public class QuickTomato extends Service {
    private long endTime;
    private boolean running=true;
    View view;
    private boolean hasView=false;
    @Override
    public void onCreate() {
        //设置锁屏所展示的view
        view = LayoutInflater.from(this).inflate(R.layout.activity_quick_tomato, null);
        SharedPreferences shp = getSharedPreferences(AppManagement.saveName, Context.MODE_PRIVATE);
        endTime=shp.getLong("howLong", 0);
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
                    FloatWindow.get("tomato").hide();
                    break;
            }
        };
    };

    private void floatWindowOpen(){
        if(FloatWindow.get("tomato")!=null){
            changeView();
            FloatWindow.get("tomato").show();
        }else{
            changeView();
            FloatWindow
                    .with(getApplicationContext())
                    .setView(view)
                    .setWidth(Screen.width, 1f)                               //设置控件宽高
                    .setHeight(Screen.height,1.2f)
                    .setX(Screen.width, 0f)                                   //设置控件初始位置
                    .setY(Screen.height,0f)
                    .setTag("tomato")
                    .setDesktopShow(true)
                    .setMoveType(MoveType.inactive)
                    .build();
            hasView = true;
        }
    }

    private void startCheck(){
        new Thread(){
            @Override
            public void run() {
                super.run();
                while(running) {
                    handler.sendEmptyMessage(FLOAT_WINDOW_OPEN);
                }
            }
        }.start();
    }

    public void changeView(){
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION| View.SYSTEM_UI_FLAG_IMMERSIVE |View.SYSTEM_UI_FLAG_FULLSCREEN);
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
        info = info+"放下手机吧!";
        TextView textView = view.findViewById(R.id.textView);
        textView.setText(info);
        view.refreshDrawableState();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        FloatWindow.destroy();
    }
}
