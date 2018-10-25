package com.praveen.furlencopraveen.data.file;

import android.os.Environment;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    public static String MD5FromUrl(String url) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        byte[] hashInBytes = new byte[0];
        try {
            hashInBytes = md.digest(url.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String getVideoDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/Furlenco-Praveen/";
    }
}
