package com.praveen.furlencopraveen.data;

import com.praveen.furlencopraveen.data.file.FileHelper;
import com.praveen.furlencopraveen.data.model.Video;
import com.praveen.furlencopraveen.data.sharedPrefs.SharedPrefsHelper;

public class AppDataManager implements DataManager {

    private SharedPrefsHelper mSharedPrefsHelper;

    private FileHelper mFileHelper;

    public AppDataManager(SharedPrefsHelper sharedPrefsHelper, FileHelper fileHelper) {
        this.mSharedPrefsHelper = sharedPrefsHelper;
        this.mFileHelper = fileHelper;
    }

    @Override
    public void saveToPrefs(Video video) {
        mSharedPrefsHelper.saveToPrefs(video);
    }

    @Override
    public Video getVideoFromPrefs(String url) {
        return mSharedPrefsHelper.getVideoFromPrefs(url);
    }

    @Override
    public boolean doesFileExist(String fileName) {
        return mFileHelper.doesFileExist(fileName);
    }

    @Override
    public String getUniqueFileName() {
        return mFileHelper.getUniqueFileName();
    }

    @Override
    public String getNewFilePath(String fileName) {
        return mFileHelper.getNewFilePath(fileName);
    }
}
