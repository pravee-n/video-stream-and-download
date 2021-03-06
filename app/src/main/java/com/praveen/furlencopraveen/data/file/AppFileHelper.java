package com.praveen.furlencopraveen.data.file;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

public class AppFileHelper implements FileHelper {
    @Override
    public boolean doesFileExist(String fileName) {
        File file = new File(FileUtils.getVideoDirectory() + fileName);
        return file.exists();
    }

    @Override
    public String getUniqueFileNameFromUrl(String url) {
        return FileUtils.MD5FromUrl(url) + ".mp4";
    }

    @Override
    public String getNewFilePath(String fileName) {
        File storageDir = new File(FileUtils.getVideoDirectory());
        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                return null;
            }
        }
        File file = new File(FileUtils.getVideoDirectory() + fileName);
        /*if (file.exists()) {
            file.delete();
        }*/
        return file.getAbsolutePath();
    }
}
