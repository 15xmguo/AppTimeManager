package com.example.conquermobile.manage;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.conquermobile.R;
import com.example.conquermobile.abs.AppDatabase;
import com.example.conquermobile.dao.RestrictedAppDao;
import com.example.conquermobile.entities.RestrictedApp;
import com.example.conquermobile.lib.BackgroundUtil;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppManageList extends AppCompatActivity implements OnDateSetListener, AdapterView.OnItemClickListener {

    SimpleAdapter adapter;
    ListView listView;
    //Data Base
    private List<RestrictedApp> restrictedAppsFromDB;
    //获取最近一段时间使用的app
    private List<RestrictedApp> allApps;
    RestrictedAppDao restrictedAppDao;
    private int p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manage_list);
        restrictedAppDao = AppDatabase.getInstance(getApplicationContext()).getRestrictedAppDao();
    }

    //每次重新进入界面的时候加载listView
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @SuppressLint("SetTextI18n")
    @Override
    protected void onResume() {
        super.onResume();
        restrictedAppsFromDB = restrictedAppDao.getAllRestrictedApps();
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private void initView(){
        restrictedAppsFromDB = restrictedAppDao.getAllRestrictedApps();
        //启动一个线程删掉不被使用的app
        new Thread(new Runnable() {
            @Override
            public void run() {
                long now = System.currentTimeMillis();
                for(RestrictedApp restrictedApp : restrictedAppsFromDB){
                    if(restrictedApp.getEndTime()<now){
                        restrictedAppDao.delete(restrictedApp);
                    }
                }
            }
        }).start();
        List<Map<String,Object>> datalist = null;
        datalist =getDataList();
        listView = findViewById(R.id.AppManageList);
        adapter = new SimpleAdapter(this,datalist,R.layout.inner_list2,
                new String[]{"label","info","icon"},
                new int[]{R.id.label,R.id.listIconInfo,R.id.icon});
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);

        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object o, String s) {
                if(view instanceof ImageView && o instanceof Drawable){
                    ImageView iv=(ImageView)view;
                    iv.setImageDrawable((Drawable)o);
                    return true;
                }
                else return false;
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    private List<Map<String,Object>> getDataList() {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String,Object> map;
        allApps = BackgroundUtil.getAllAppsNonSystem(getApplicationContext());
        long now = System.currentTimeMillis();
        for(RestrictedApp restrictedApp : restrictedAppsFromDB){
            for(int i=0; i< allApps.size(); ++i){
                if(restrictedApp.getRestrictedAppName().equals(allApps.get(i).getRestrictedAppName()) && (restrictedApp.getEndTime()>now)){
                    allApps.get(i).setEndTime(restrictedApp.getEndTime());
                }
            }
        }
        Collections.sort(allApps, new Comparator<RestrictedApp>() {
            @Override
            public int compare(RestrictedApp o1, RestrictedApp o2) {
                return (int)(o2.usedTimes - o1.usedTimes);
            }
        });
        for(RestrictedApp app : allApps) {
            map = new HashMap<>();
            String info = "";
            long duration  = app.getEndTime() - now;
            duration /= 1000;
            duration /= 60;
            long hour = duration/60;
            long min = duration%60;
            if(hour>0){
                info += ""+hour+"小时";
            }
            if(min>0){
                info += ""+min+"分钟";
            }
            if(info.equals("")){
                info = "未设定";
            }else{
                info = "锁屏:" +info;
            }
            map.put("label", app.appName);
            map.put("info", info);
            map.put("icon",app.getIcon());
            app.timePickerDialog = new TimePickerDialog.Builder()
                    .setType(Type.HOURS_MINS)
                    .setCallBack(this)
                    .setThemeColor(getResources().getColor(R.color.darkred))
                    .setCurrentMillseconds(0)
                    .setTitleStringId(getResources().getString(R.string.timePickerTitle))
                    .build();
            dataList.add(map);
        }
        return dataList;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        p = i;
        allApps.get(i).timePickerDialog.show(getSupportFragmentManager(), allApps.get(i).getRestrictedAppName());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        long now = System.currentTimeMillis();
        //对话框设置为0的起始时间
        long zero = 1420041600000L;
        millseconds -= zero;
        allApps.get(p).setEndTime(millseconds + now);

        new Thread(new Runnable() {
            @Override
            public void run() {
                RestrictedApp restrictedApp = restrictedAppDao.getAppByName(allApps.get(p).getRestrictedAppName());
                if(restrictedApp==null){
                    restrictedAppDao.insert(allApps.get(p));
                }else{
                    restrictedApp.setEndTime(allApps.get(p).getEndTime());
                    restrictedAppDao.update(restrictedApp);
                }
            }
        }).start();
        Map<String, Object> item = (HashMap<String, Object>)adapter.getItem(p);
        long duration  = allApps.get(p).getEndTime() - now;
        duration /= 1000;
        duration /= 60;
        long hour = duration/60;
        long min = duration%60;
        String info="";
        if(hour>0){
            info += ""+hour+"小时";
        }
        if(min>0){
            info += ""+min+"分钟";
        }
        if(info.equals("")){
            info = "未设定";
        }else{
            info = "锁屏:" +info;
        }
        item.put("info", info);
        adapter.notifyDataSetChanged();
    }
}
