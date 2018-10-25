package com.praveen.furlencopraveen.ui.home;

import com.karumi.dexter.PermissionToken;
import com.praveen.furlencopraveen.data.model.Video;
import com.praveen.furlencopraveen.ui.base.MvpView;

import java.io.File;

public interface HomeMvpView extends MvpView {
    void checkStoragePermission();

    void showPermissionSnackbar();

    void showPermissionRationale(PermissionToken token);

    void startVideoFromLocalFile(Video video, boolean switchingToLocal);

    void startDownloadAndStreamVideo(Video video);

    String getVideoUrl();

    void stopDownloadAndCleanUp();

    void stopServer();
}
