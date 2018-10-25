package com.praveen.furlencopraveen.server;

import android.app.Activity;

import java.io.File;


public class VideoDownloadAndPlayService
{
    private static LocalProxyStreamingServer server;

    private VideoDownloadAndPlayService(LocalProxyStreamingServer server)
    {
        this.server = server;
    }

    public static VideoDownloadAndPlayService startServer(final Activity activity, String videoUrl, String pathToSaveVideo, final String ipOfServer, final VideoStreamInterface callback)
    {
        //new VideoDownloader().execute(videoUrl, pathToSaveVideo);
        VideoDownloader downloader = new VideoDownloader(videoUrl, pathToSaveVideo);
        downloader.startDownload();

        server = new LocalProxyStreamingServer(new File(pathToSaveVideo));
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
                        callback.onServerStart(server.getFileUrl());
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

    public static interface VideoStreamInterface{
        public void onServerStart(String videoStreamUrl);
    }
}
