package com.praveen.furlencopraveen.data.file;

import java.io.File;

public interface FileHelper {
    boolean doesFileExist(String fileName);

    String getUniqueFileName();

    String getNewFilePath(String fileName);
}
