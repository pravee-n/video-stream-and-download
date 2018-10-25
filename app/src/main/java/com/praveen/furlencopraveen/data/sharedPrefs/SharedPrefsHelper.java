package com.praveen.furlencopraveen.data.sharedPrefs;

import com.praveen.furlencopraveen.data.model.Video;

public interface SharedPrefsHelper {
    void saveToPrefs(Video video);

    Video getVideoFromPrefs(String url);
}
