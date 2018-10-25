package com.praveen.furlencopraveen.data.file;

import java.io.File;

public interface FileHelper {
    boolean doesFileExist(String fileName);

    String getUniqueFileNameFromUrl(String url);

    String getNewFilePath(String fileName);
}
