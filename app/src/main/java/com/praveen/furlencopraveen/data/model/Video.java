package com.praveen.furlencopraveen.data.model;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class Video {
    private String url;

    private String id;

    private String path;

    public Video(String url) {
        this.url = url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        try {
            return URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return url;
        }
    }

    public String getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
