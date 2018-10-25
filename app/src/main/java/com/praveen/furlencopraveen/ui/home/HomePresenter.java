package com.praveen.furlencopraveen.ui.home;

import com.karumi.dexter.PermissionToken;
import com.praveen.furlencopraveen.data.DataManager;
import com.praveen.furlencopraveen.data.model.Video;
import com.praveen.furlencopraveen.ui.base.BasePresenter;

public class HomePresenter<V extends HomeMvpView> extends BasePresenter<V>
        implements HomeMvpPresenter<V> {

    private Video mVideo;

    public HomePresenter(DataManager dataManager) {
        super(dataManager);
    }

    @Override
    public void onStartClicked() {
        getMvpView().checkStoragePermission();
    }

    @Override
    public void onStoragePermissionGranted() {
        //getMvpView().startVideo();
        mVideo = getDataManager().getVideoFromPrefs(getMvpView().getVideoUrl());

        // video has been downloaded already, use local file
        if (mVideo != null && getDataManager().doesFileExist(mVideo.getId())) {
            getMvpView().startVideoFromLocalFile(mVideo, false);
        } else {
            // video has not been downloaded yet, download and stream now
            Video video = new Video(getMvpView().getVideoUrl());
            String fileName = getDataManager().getUniqueFileNameFromUrl(video.getUrl());
            video.setId(fileName);
            video.setPath(getDataManager().getNewFilePath(fileName));
            getMvpView().startDownloadAndStreamVideo(video);
        }
    }

    @Override
    public void onStoragePermissionDenied() {
        getMvpView().showPermissionSnackbar();
    }

    @Override
    public void onShouldShowPermissionRationale(PermissionToken token) {
        getMvpView().showPermissionRationale(token);
    }

    @Override
    public void onDownloadComplete(Video video) {
        getDataManager().saveToPrefs(video);
    }


    @Override
    public void onFirstPlaybackComplete() {
        mVideo = getDataManager().getVideoFromPrefs(getMvpView().getVideoUrl());

        // video has been downloaded, use local file for next playbacks
        if (mVideo != null && getDataManager().doesFileExist(mVideo.getId())) {
            getMvpView().startVideoFromLocalFile(mVideo, true);
        }
    }

    @Override
    public void onActivityPaused() {
        getMvpView().stopServer();

    }

    @Override
    public void onActivityDestroy() {
        getMvpView().stopDownloadAndCleanUp();
    }
}
