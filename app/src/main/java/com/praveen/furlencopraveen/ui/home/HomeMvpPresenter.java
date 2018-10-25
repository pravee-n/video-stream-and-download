package com.praveen.furlencopraveen.ui.home;

import com.karumi.dexter.PermissionToken;
import com.praveen.furlencopraveen.data.model.Video;
import com.praveen.furlencopraveen.ui.base.MvpPresenter;

public interface HomeMvpPresenter<V extends HomeMvpView> extends MvpPresenter<V> {
    void onStartClicked();

    void onStoragePermissionGranted();

    void onStoragePermissionDenied();

    void onShouldShowPermissionRationale(PermissionToken token);

    void onDownloadComplete(Video video);

    void onFirstPlaybackComplete();

    void onActivityPaused();

    void onActivityDestroy();
}
