package com.praveen.furlencopraveen.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.praveen.furlencopraveen.FurlencoTask;
import com.praveen.furlencopraveen.R;
import com.praveen.furlencopraveen.data.DataManager;
import com.praveen.furlencopraveen.data.model.Video;
import com.praveen.furlencopraveen.server.Downloader;
import com.praveen.furlencopraveen.server.DownloaderCallback;
import com.praveen.furlencopraveen.server.LocalProxyStreamingServer;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements HomeMvpView {

    @BindView(R.id.a_home_video_view)
    VideoView mVideoView;

    @BindView(R.id.a_home_start_btn)
    Button mStartBtn;

    @BindView(R.id.a_home_root)
    ConstraintLayout mRootView;

    private Context mContext;

    private MediaController mMediaController;

    private static final String TAG = "HomeActivity";

    private static LocalProxyStreamingServer mServer;

    private HomeMvpPresenter<HomeMvpView> mHomePresenter;

    public String mVideoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
    //public String mVideoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4";
//    public String mVideoUrl = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4";


    private Downloader mDownloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mContext = this;

        DataManager dataManager = ((FurlencoTask) getApplication()).getDataManager();
        mHomePresenter = new HomePresenter(dataManager);
        mHomePresenter.onAttach(this);

        mMediaController = new MediaController(this, false);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomePresenter.onStartClicked();
                mStartBtn.setEnabled(false);
            }
        });
    }



    /**
     *  Runtime storage permission check
     */
    @Override
    public void checkStoragePermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        mHomePresenter.onStoragePermissionGranted();
                    }

                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        mHomePresenter.onStoragePermissionDenied();
                    }

                    @Override public void onPermissionRationaleShouldBeShown(
                            PermissionRequest permission, PermissionToken token) {
                        mHomePresenter.onShouldShowPermissionRationale(token);
                    }
                }).check();
    }


    /**
     * Take user to settings screen of the app if the user has
     * denied storage permission permanently
     */
    @Override
    public void showPermissionSnackbar() {
        final Snackbar snackbar = Snackbar.make(mRootView,
                getString(R.string.please_allow_access_to_storage), Snackbar.LENGTH_LONG);
        snackbar.setAction(getString(R.string.settings), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        snackbar.show();
    }


    /**
     *  Show user a basic dialog explaining why we need storage permission
     *  if the user has denied to give storage permission at least once.
     */
    @Override
    public void showPermissionRationale(final PermissionToken token) {
        PermissionRationaleDialogFragment rationaleDialog = PermissionRationaleDialogFragment.newInstance();
        rationaleDialog.setPermissionToken(token);
        rationaleDialog.show(getSupportFragmentManager(), null);
    }


    @Override
    public void startVideoFromLocalFile(Video video, boolean switchingToLocal) {
        mStartBtn.setEnabled(false);

        mMediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mMediaController);
        mVideoView.setVideoPath(video.getPath());
        mVideoView.requestFocus();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mMediaController.show();
            }
        });
        if (switchingToLocal) {
            mVideoView.setOnCompletionListener(null);
            mServer.stop();
            Toast.makeText(mContext, "Download complete, would use local file now",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Playing from local file",
                    Toast.LENGTH_SHORT).show();
            mVideoView.start();
        }

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaController.show(2000);
            }
        });

    }

    /**
     *  Start download thread. Start proxy server and begin
     *  streaming to videoView
     */
    @Override
    public void startDownloadAndStreamVideo(Video video) {
        mStartBtn.setEnabled(false);
        Toast.makeText(mContext, "Downloading and Streaming simultaneously",
                Toast.LENGTH_SHORT).show();

        final DownloaderCallback downloaderCallback = new DownloaderCallback() {
            @Override
            public void onDownloadComplete(Video video) {
                mHomePresenter.onDownloadComplete(video);
            }
        };

        if (mDownloader == null) {
            mDownloader = new Downloader(video, downloaderCallback);
            mDownloader.startDownload();
        }

        mServer = new LocalProxyStreamingServer(new File(video.getPath()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                mServer.init("127.0.0.1");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mServer.start();
                        mVideoView.setVideoURI(Uri.parse(mServer.getFileUrl()));
                        mVideoView.requestFocus();
                        mVideoView.start();
                        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mHomePresenter.onFirstPlaybackComplete();
                            }
                        });
                    }
                });
            }
        }).start();
    }

    @Override
    public String getVideoUrl() {
        return mVideoUrl;
    }

    /**
     *  Activity destroyed. Stop the server, cancel pending download,
     *  delete partially downloaded file.
     */
    @Override
    public void stopDownloadAndCleanUp() {
        if (mDownloader != null && mDownloader.isRunning()) {
            mDownloader.stop();
        }

        if (mServer != null) {
            mServer.stop();
        }
    }

    @Override
    public void stopServer() {
        if (mServer != null) {
            mServer.stop();
        }
        mStartBtn.setEnabled(true);
    }


    /**
     * Activity about to be paused. Pass this data to Presenter
     */
    @Override
    protected void onPause() {
        mHomePresenter.onActivityPaused();
        super.onPause();
    }

    /**
     * Activity about to be destroyed. Pass this data to Presenter
     */
    @Override
    protected void onDestroy() {
        mHomePresenter.onActivityDestroy();
        super.onDestroy();
    }
}
