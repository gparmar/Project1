package com.example.android.themoviedbproject.common;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by gparmar on 08/05/17.
 */

public class NetworkUtil {
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            return IOUtils.toString(in);
        } finally {
            urlConnection.disconnect();
        }
    }
}
