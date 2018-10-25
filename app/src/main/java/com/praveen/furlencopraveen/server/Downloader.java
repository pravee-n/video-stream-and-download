package com.praveen.furlencopraveen.server;

import android.util.Log;

import com.praveen.furlencopraveen.data.model.Video;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Downloader implements Runnable {

    private boolean running = false;

    private boolean downloadInterrupted = false;

    private Video mVideo;

    private DownloaderCallback mDownloaderCallback;

    private static final String TAG = "Downloader";

    private BufferedInputStream mInputStream;

    private FileOutputStream mFileOutputStream;

    /**
     * Data status
     */
    public static final int DATA_READY = 1;
    public static final int DATA_NOT_READY = 2;
    public static final int DATA_CONSUMED = 3;
    public static final int DATA_NOT_AVAILABLE = 4;

    public static int dataStatus = -1;

    public Downloader(Video video, DownloaderCallback downloaderCallback) {
        this.mVideo = video;
        this.mDownloaderCallback = downloaderCallback;
    }

    public void startDownload() {
        Thread thread = new Thread(this);
        thread.start();
        running = true;
    }

    public static boolean isDataReady() {
        dataStatus = -1;
        boolean res = false;
        if (fileLength == downloadedBytes) {
            dataStatus = DATA_CONSUMED;
            res = false;
        } else if (downloadedBytes > consumedBytes) {
            dataStatus = DATA_READY;
            res = true;
        } else if (downloadedBytes <= consumedBytes) {
            dataStatus = DATA_NOT_READY;
            res = false;
        } else if (fileLength == -1) {
            dataStatus = DATA_NOT_AVAILABLE;
            res = false;
        }
        return res;
    }

    /**
     * Keeps track of bytes consumed by player
     */
    public static int consumedBytes = 0;

    /**
     * Keeps track of all bytes downloaded
     */
    private static int downloadedBytes = 0;

    /**
     * Length of file being downloaded.
     */
    static int fileLength = -1;


    @Override
    public void run() {
        while (isRunning()) {
            mInputStream = null;
            try {
                mFileOutputStream = new FileOutputStream(mVideo.getPath());
                try {
                    URL url = new URL(mVideo.getUrl());

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("response is not http_ok");
                    }
                    fileLength = connection.getContentLength();

                    mInputStream = new BufferedInputStream(connection.getInputStream());
                    byte data[] = new byte[1024 * 50];
                    int len;

                    while (((len = mInputStream.read(data)) != -1) && !downloadInterrupted) {
                        mFileOutputStream.write(data, 0, len);
                        mFileOutputStream.flush();
                        downloadedBytes += len;
                        if ((downloadedBytes / 1024) % 10 == 0) {
                            Log.w(TAG, (downloadedBytes / 1024) + "kb of " + (fileLength / 1024) + "kb");
                        }
                    }

                    running = false;
                    if (!downloadInterrupted) {
                        Log.w(TAG, "Download done");
                        mDownloaderCallback.onDownloadComplete(mVideo);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (mFileOutputStream != null) {
                        mFileOutputStream.flush();
                        mFileOutputStream.close();
                    }
                    if (mInputStream != null) {
                        mInputStream.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void stop() {
        Log.d("hola", "stopping download");
        downloadInterrupted = true;

        File file = new File(mVideo.getPath());
        if (file.exists()) {
            Log.d("hola", "file deleted");
            file.delete();
        }
    }
}
