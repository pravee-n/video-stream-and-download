package com.praveen.furlencopraveen.ui.home;

import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import com.praveen.furlencopraveen.R;
import com.praveen.furlencopraveen.server.VideoDownloadAndPlayService;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.a_home_video_view)
    VideoView mVideoView;

    @BindView(R.id.a_home_start_btn)
    Button mStartBtn;

    private static final String TAG = "HomeActivity";

    private VideoDownloadAndPlayService videoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startServer();
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
}
