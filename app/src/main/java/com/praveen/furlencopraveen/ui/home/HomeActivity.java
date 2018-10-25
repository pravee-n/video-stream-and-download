package com.praveen.furlencopraveen.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.praveen.furlencopraveen.server.VideoDownloadAndPlayService;

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

    private static final String TAG = "HomeActivity";

    private VideoDownloadAndPlayService videoService;

    private HomeMvpPresenter<HomeMvpView> mHomePresenter;

    public String mVideoUrl = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        mContext = this;

        DataManager dataManager = ((FurlencoTask) getApplication()).getDataManager();
        mHomePresenter = new HomePresenter(dataManager);
        mHomePresenter.onAttach(this);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHomePresenter.onStartClicked();
            }
        });
    }


    private void startServer() {
        File videoFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Furlenco-Praveen/furlenco-video-alt.mp4");
        if (videoFile.exists()) {
            MediaController mediaController = new MediaController(this);
            mediaController.setMediaPlayer(mVideoView);
            mVideoView.setMediaController(mediaController);
            mVideoView.setVideoPath(videoFile.getAbsolutePath());
            mVideoView.requestFocus();
            mVideoView.start();
        } else {
            String videoPath = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
            videoService = VideoDownloadAndPlayService.startServer(this, videoPath, videoFile.getAbsolutePath(), "127.0.0.1", new VideoDownloadAndPlayService.VideoStreamInterface()
            {
                @Override
                public void onServerStart(String videoStreamUrl)
                {
                    // use videoStreamUrl to play video through media player
                    mVideoView.setVideoURI(Uri.parse(videoStreamUrl));
                    /*MediaController mediaController = new MediaController(HomeActivity.this);
                    mediaController.setMediaPlayer(mVideoView);
                    mVideoView.setMediaController(mediaController);*/
                    mVideoView.start();
                }
            });
        }
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
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this,
                    android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }

        builder.setTitle("")
                .setMessage(getString(R.string.storage_permission_message))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.cancelPermissionRequest();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        token.continuePermissionRequest();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override public void onDismiss(DialogInterface dialog) {
                        token.cancelPermissionRequest();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.show();
    }


    @Override
    public void startVideoFromLocalFile(Video video) {
        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.setVideoPath(video.getPath());
        mVideoView.requestFocus();
        mVideoView.start();
        Toast.makeText(mContext, "Permission granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startDownloadAndStreamVideo(Video video) {
        videoService = VideoDownloadAndPlayService.startServer(this, video.getUrl(),
                video.getPath(), "127.0.0.1", new VideoDownloadAndPlayService.VideoStreamInterface()
                {
                    @Override
                    public void onServerStart(String videoStreamUrl)
                    {
                        mVideoView.setVideoURI(Uri.parse(videoStreamUrl));
                        mVideoView.start();
                    }
                });
    }

    @Override
    public String getVideoUrl() {
        return mVideoUrl;
    }
}
