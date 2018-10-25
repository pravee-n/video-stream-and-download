package com.praveen.furlencopraveen.server;

import com.praveen.furlencopraveen.data.model.Video;

public interface DownloaderCallback {
    void onDownloadComplete(Video video);
}
