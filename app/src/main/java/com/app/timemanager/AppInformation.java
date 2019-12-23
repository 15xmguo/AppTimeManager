package com.app.timemanager;

import android.app.usage.UsageStats;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;



public class AppInformation {
    private UsageStats usageStats;
    private String packageName;
    private String label;
    private Drawable Icon;
    private long UsedTimebyDay;  //milliseconds
    private Context context;
    private int times;
    private boolean isSys;
    public boolean isSys() {
        return isSys;
    }

    public void setSys(boolean sys) {
        isSys = sys;
    }

    public AppInformation(UsageStats usageStats , Context context, boolean isSys) {
        this.usageStats = usageStats;
        this.context = context;
        this.isSys = isSys;
        try {
            GenerateInfo();
        } catch (PackageManager.NameNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private void GenerateInfo() throws PackageManager.NameNotFoundException, NoSuchFieldException, IllegalAccessException {
        PackageManager packageManager = context.getPackageManager();
        this.packageName = usageStats.getPackageName();

        ApplicationInfo applicationInfo = packageManager.getApplicationInfo(this.packageName,0);
        this.label = (String) packageManager.getApplicationLabel(applicationInfo);
        this.UsedTimebyDay = usageStats.getTotalTimeInForeground();
        this.times = (Integer)usageStats.getClass().getDeclaredField("mLaunchCount").get(usageStats);

        if(this.UsedTimebyDay > 0) {
            this.Icon = applicationInfo.loadIcon(packageManager);
        }
    }

    public UsageStats getUsageStats() {
        return usageStats;
    }

    public int getTimes() {
        return times;
    }

    public void setTimes(int times) {
        this.times = times;
    }

    public void setUsedTimebyDay(long usedTimebyDay) {
        this.UsedTimebyDay = usedTimebyDay;
    }

    public Drawable getIcon() {
        return Icon;
    }

    public long getUsedTimebyDay() {
        return UsedTimebyDay;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }


}
