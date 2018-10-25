package com.praveen.furlencopraveen.data.sharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.praveen.furlencopraveen.data.model.Video;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.HashMap;

public class AppSharedPrefsHelper implements SharedPrefsHelper {
    private SharedPreferences mPrefs;

    private Gson mGson;

    private String prefFileName = "com.praveen.furlencopraveen.preferencefile";

    private final String PREF_KEY_VIDEO_DATA = "PREF_KEY_VIDEO_DATA";

    public AppSharedPrefsHelper(Context context) {
        this.mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
        this.mGson = new Gson();
    }

    @Override
    public void saveToPrefs(Video video) {
        String videosMapString = mPrefs.getString(PREF_KEY_VIDEO_DATA, null);
        HashMap<String, Video> videoMap;
        if (videosMapString == null) {
            videoMap = new HashMap<>();
        } else {
            Type type = new TypeToken<HashMap<String, Video>>(){}.getType();
            videoMap = mGson.fromJson(videosMapString, type);
        }

        videoMap.put(video.getUrl(), video);
        mPrefs.edit()
                .putString(PREF_KEY_VIDEO_DATA, mGson.toJson(videoMap))
                .apply();
    }

    @Override
    public Video getVideoFromPrefs(String url) {
        String videosMapString = mPrefs.getString(PREF_KEY_VIDEO_DATA, null);
        HashMap<String, Video> videoMap;
        if (videosMapString == null) {
            return null;
        } else {
            Type type = new TypeToken<HashMap<String, Video>>(){}.getType();
            videoMap = mGson.fromJson(videosMapString, type);
            try {
                return videoMap.get(URLDecoder.decode(url, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
