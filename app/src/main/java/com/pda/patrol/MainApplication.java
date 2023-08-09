package com.pda.patrol;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import java.util.List;

public class MainApplication extends Application {
    private static MainApplication instance;
    public static MainApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    private boolean isMainProcess() {
        String mainProcessName = this.getPackageName();
        ActivityManager am = ((ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if(runningApps == null || runningApps.size() == 0) {
            return false;
        }
        for(ActivityManager.RunningAppProcessInfo info : runningApps) {
            if(mainProcessName.equals(info.processName)) {
                return true;
            }
        }

        return false;
    }
}
