package com.praveen.furlencopraveen;

import android.app.Application;

import com.praveen.furlencopraveen.data.AppDataManager;
import com.praveen.furlencopraveen.data.file.AppFileHelper;
import com.praveen.furlencopraveen.data.file.FileHelper;
import com.praveen.furlencopraveen.data.sharedPrefs.AppSharedPrefsHelper;
import com.praveen.furlencopraveen.data.sharedPrefs.SharedPrefsHelper;

public class FurlencoTask extends Application {
    private AppDataManager mAppDataManager;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPrefsHelper sharedPrefsHelper = new AppSharedPrefsHelper(getApplicationContext());
        FileHelper fileHelper = new AppFileHelper();
        mAppDataManager = new AppDataManager(sharedPrefsHelper, fileHelper);
    }

    public AppDataManager getDataManager() {
        return mAppDataManager;
    }
}
