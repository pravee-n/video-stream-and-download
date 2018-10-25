package com.praveen.furlencopraveen.server;

import android.app.Activity;

import com.praveen.furlencopraveen.data.model.Video;

import java.io.File;


public class VideoDownloadAndPlayService
{
    private static LocalProxyStreamingServer server;

    private VideoDownloadAndPlayService(LocalProxyStreamingServer server)
    {
        this.server = server;
    }

    public static VideoDownloadAndPlayService startServer(final Activity activity,
                                                          Video video, final String ipOfServer,
                                                          final DownloaderCallback callback)
    {
        //new Downloader().execute(videoUrl, pathToSaveVideo);
        Downloader downloader = new Downloader(video, callback);
        downloader.startDownload();

        server = new LocalProxyStreamingServer(new File(video.getPath()));
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                server.init(ipOfServer);

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        server.start();
                        //callback.onServerStart(server.getFileUrl());
                    }
                });
            }
        }).start();

        return new VideoDownloadAndPlayService(server);
    }

    public void start(){
        server.start();
    }

    public void stop() {
        server.stop();
    }
}
