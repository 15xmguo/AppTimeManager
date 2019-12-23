package com.app.timemanager.lib;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.app.timemanager.entities.RestrictedApp;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class BackgroundUtil {

    private static final String TAG = "BackgroundUtil";
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static String queryUsageStats(Context context) {
        class RecentUseComparator implements Comparator<UsageStats> {
            @Override
            public int compare(UsageStats lhs, UsageStats rhs) {
                return (lhs.getLastTimeUsed() > rhs.getLastTimeUsed()) ? -1 : (lhs.getLastTimeUsed() == rhs.getLastTimeUsed()) ? 0 : 1;
            }
        }
        RecentUseComparator mRecentComp = new RecentUseComparator();
        long ts = System.currentTimeMillis();
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        List<UsageStats> usageStats = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 1000 * 10, ts);
        if (usageStats == null || usageStats.size() == 0) {
            return "";
        }
        Collections.sort(usageStats, mRecentComp);
        String currentTopPackage = usageStats.get(0).getPackageName();
        return currentTopPackage;
    }

    /**
     * 判断是否有用权限
     *
     * @param context 上下文参数
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean HavaPermissionForTest(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static boolean checkAppUsagePermission(Context context) {
        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        if(usageStatsManager == null) {
            return false;
        }
        long currentTime = System.currentTimeMillis();
        // try to get app usage state in last 1 min
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 60 * 1000, currentTime);
        if (stats.size() == 0) {
            return false;
        }

        return true;
    }

    public static void requestAppUsagePermission(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.i(TAG,"Start usage access settings activity fail!");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    public static List<RestrictedApp> getAllAppsNonSystem(Context context){
        List<RestrictedApp> restrictedApps = new ArrayList<>();
        List<UsageStats> usageStats;
        PackageManager pm = context.getPackageManager();
        UsageStatsManager m = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
        if(m != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -5);
            long begintime = calendar.getTimeInMillis();
            long now = System.currentTimeMillis();
            usageStats = m.queryUsageStats(UsageStatsManager.INTERVAL_YEARLY, begintime, now);
            for(UsageStats usageStat : usageStats){
                try {
                    ApplicationInfo applicationInfo = pm.getApplicationInfo(usageStat.getPackageName(),0);
                    if((usageStat.getTotalTimeInForeground()>0)&&(applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1){
                        RestrictedApp restrictedApp = new RestrictedApp();
                        restrictedApp.appName = (String)pm.getApplicationLabel(applicationInfo);
                        restrictedApp.usedTimes = usageStat.getTotalTimeInForeground();
                        restrictedApp.setEndTime(0);
                        //加载图像
                        restrictedApp.setIcon(applicationInfo.loadIcon(pm));
                        restrictedApp.setRestrictedAppName(usageStat.getPackageName());
                        restrictedApps.add(restrictedApp);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
        }
        return restrictedApps;
    }
}