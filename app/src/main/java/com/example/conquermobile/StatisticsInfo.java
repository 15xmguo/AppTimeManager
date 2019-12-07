package com.example.conquermobile;

import android.annotation.TargetApi;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StatisticsInfo {

    final public static int DAY = 0;
    final public static int WEEK = 1;
    final public static int MONTH = 2;
    final public static int YEAR = 3;
    private ArrayList<AppInformation> ShowList;
    private ArrayList<AppInformation> AppInfoList;
    private List<UsageStats> result;
    private Context context;
    private long totalTime;
    private int totalTimes;
    private int style;

    public StatisticsInfo(Context context, int style) {
        try {
            this.style = style;
            this.context = context;
            setUsageStatsList();
            setShowList();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    //将次数和时间为0的应用信息过滤掉
    private void setShowList() {
        this.ShowList = new ArrayList<>();
        totalTime = 0;
        Log.d("HQS", "style "+style);
        Log.d("HQS", "AppInfoList.size() "+AppInfoList.size());
        for(int i=0;i<AppInfoList.size();i++) {
            if(AppInfoList.get(i).getUsedTimebyDay() >= 0 && !AppInfoList.get(i).isSys()) { //&& AppInfoList.get(i).getTimes() > 0) {
                this.ShowList.add(AppInfoList.get(i));
                totalTime += AppInfoList.get(i).getUsedTimebyDay();
                totalTimes += AppInfoList.get(i).getTimes();
            }
        }

        //将显示列表中的应用按显示顺序排序
        for(int i = 0;i<this.ShowList.size() - 1;i++) {
            for(int j = 0; j< this.ShowList.size() - i - 1; j++) {
                if(this.ShowList.get(j).getUsedTimebyDay() < this.ShowList.get(j+1).getUsedTimebyDay()) {
                    AppInformation temp = this.ShowList.get(j);
                    this.ShowList.set(j,this.ShowList.get(j+1));
                    this.ShowList.set(j+1,temp);
                }
            }
        }
    }

    //统计当天的应用使用时间
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setUsageStatsList() throws NoSuchFieldException{
        Calendar calendar = Calendar.getInstance();
        PackageManager pm = context.getPackageManager();
        setResultList();
        List<UsageStats> Mergeresult = MergeList(this.result);

        for(UsageStats usageStats:Mergeresult) {
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(usageStats.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            this.AppInfoList.add(new AppInformation(usageStats , context, (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==1));
        }
    }

    private void setResultList() {
        UsageStatsManager m = (UsageStatsManager)this.context.getSystemService(Context.USAGE_STATS_SERVICE);
        this.AppInfoList = new ArrayList<>();
        if(m != null) {
            Calendar calendar = Calendar.getInstance();
            long now = calendar.getTimeInMillis();
            long begintime = getBeginTime();
            if(style == DAY)
                this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begintime, now);
            else if(style == WEEK)
                this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_WEEKLY,begintime, now);
            else if(style == MONTH)
                this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, begintime, now);
            else if(style == YEAR)
                this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, begintime, now);
            else {
                this.result = m.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, begintime, now);
            }
        }
    }

    private long getBeginTime() {
        Calendar calendar = Calendar.getInstance();
        long begintime;
        long now = System.currentTimeMillis();
        if(style == WEEK) {
            calendar.add(Calendar.DATE,-7);
            begintime = calendar.getTimeInMillis();
        }
        else if(style == MONTH) {
            calendar.add(Calendar.MONTH,-1);
            begintime = calendar.getTimeInMillis();
        }
        else if(style == YEAR) {
            calendar.add(Calendar.YEAR,-1);
            begintime = calendar.getTimeInMillis();
        }
        else{
            calendar.add(Calendar.DATE,-1);
            begintime = calendar.getTimeInMillis();
        }
        return begintime;
    }

    private List<UsageStats> MergeList( List<UsageStats> result) {
        List<UsageStats> Mergeresult = new ArrayList<>();

        for(int i=0;i<result.size();i++) {

            long begintime;
            begintime = getBeginTime();


            if(result.get(i).getFirstTimeStamp() > begintime) {
                int num = FoundUsageStats(Mergeresult, result.get(i));
                if (num >= 0){
                    UsageStats u = Mergeresult.get(num);
                    u.add(result.get(i));
                    Mergeresult.set(num, u);
                } else Mergeresult.add(result.get(i));
            }
        }
        return Mergeresult;
    }

    private int FoundUsageStats(List<UsageStats> Mergeresult, UsageStats usageStats) {
        for(int i=0;i<Mergeresult.size();i++) {
            if(Mergeresult.get(i).getPackageName().equals(usageStats.getPackageName())) {
                return i;
            }
        }
        return -1;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public int getTotalTimes() {
        return totalTimes;
    }

    public ArrayList<AppInformation> getShowList() {
        return ShowList;
    }


    public ArrayList<AppInformation> getAllAppInfos(){
        UsageStatsManager m = (UsageStatsManager)this.context.getSystemService(Context.USAGE_STATS_SERVICE);
        ArrayList<AppInformation> allAppInfos;
        allAppInfos = new ArrayList<>();
        Calendar calendar =  Calendar.getInstance();;
        calendar.add(Calendar.YEAR,-1);
        long begintime = calendar.getTimeInMillis();
        long now = System.currentTimeMillis();
        List<UsageStats> allApps = m.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, begintime, now);
        PackageManager pm = this.context.getPackageManager();

        for(UsageStats usageStats: allApps) {
            PackageInfo packageInfo;
            try {
                packageInfo = pm.getPackageInfo(usageStats.getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                continue;
            }
            allAppInfos.add(new AppInformation(usageStats , context, (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) ==1));
        }
        return allAppInfos;
    }
}

