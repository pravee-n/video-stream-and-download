package com.praveen.furlencopraveen.server;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class VideoDownloader implements Runnable {

    private boolean isRunning = false;

    private String videoUrl;

    private String videoFilePath;

    private static final String TAG = "VideoDownloader";

    /**
     * Data status
     */
    public static final int DATA_READY = 1;
    public static final int DATA_NOT_READY = 2;
    public static final int DATA_CONSUMED = 3;
    public static final int DATA_NOT_AVAILABLE = 4;

    public static int dataStatus = -1;

    public VideoDownloader(String videoUrl, String videoFilePath) {
        this.videoUrl = videoUrl;
        this.videoFilePath = videoFilePath;
    }

    public void startDownload() {
        Thread thread = new Thread(this);
        thread.start();
        isRunning = true;
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
        while (isRunning) {
            BufferedInputStream input = null;
            try {
                final FileOutputStream out = new FileOutputStream(videoFilePath);
                try {
                    URL url = new URL(videoUrl);

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                        throw new RuntimeException("response is not http_ok");
                    }
                    fileLength = connection.getContentLength();

                    input = new BufferedInputStream(connection.getInputStream());
                    byte data[] = new byte[1024 * 50];
                    int len;

                    while ((len = input.read(data)) != -1) {
                        out.write(data, 0, len);
                        out.flush();
                        downloadedBytes += len;
                        if ((downloadedBytes / 1024) % 100 == 0) {
                            Log.w(TAG, (downloadedBytes / 1024) + "kb of " + (fileLength / 1024) + "kb");
                        }
                    }

                    isRunning = false;
                    Log.w(TAG, "Download done");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (out != null) {
                        out.flush();
                        out.close();
                    }
                    if (input != null) {
                        input.close();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
