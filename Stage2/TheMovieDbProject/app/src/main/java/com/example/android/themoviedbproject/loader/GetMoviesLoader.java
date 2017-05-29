package com.example.android.themoviedbproject.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.themoviedbproject.common.MovieUtil;
import com.example.android.themoviedbproject.model.Movie;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Created by gparmar on 29/05/17.
 */

public class GetMoviesLoader extends AsyncTaskLoader<List<Movie>> {
    private static String TAG = "GetMoviesLoader";
    private URL mUrl;
    private List<Movie> mData;
    public GetMoviesLoader(Context context, URL url) {
        super(context);
        mUrl = url;
    }
    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            // We have no data, so kick off loading it
            forceLoad();
        }
    }

    @Override
    public List<Movie> loadInBackground() {

        if (mUrl != null) {
            try {
                //I am using apache commons IOUtils utility to convert
                //a url directly to text.
                String json = IOUtils.toString(mUrl);
                Log.d(TAG, "Json from TMD:" + json);
                return MovieUtil.getMovieListFromJson(json);
            } catch (IOException e) {
                Log.e(TAG, "Error while getting scanner", e);
            }
        }
        return null;
    }

    @Override
    public void deliverResult(List<Movie> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }
}
