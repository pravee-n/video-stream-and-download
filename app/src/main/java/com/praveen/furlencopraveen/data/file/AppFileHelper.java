package com.praveen.furlencopraveen.data.file;

import android.os.Environment;

import java.io.File;
import java.util.UUID;

public class AppFileHelper implements FileHelper {
    @Override
    public boolean doesFileExist(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Furlenco-Praveen/" + fileName);
        return file.exists();
    }

    @Override
    public String getUniqueFileName() {
        return UUID.randomUUID().toString() + ".mp4";
    }

    @Override
    public String getNewFilePath(String fileName) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/Furlenco-Praveen/" + fileName);
        if (file.exists()) {
            file.delete();
        }
        return file.getAbsolutePath();
    }
}
