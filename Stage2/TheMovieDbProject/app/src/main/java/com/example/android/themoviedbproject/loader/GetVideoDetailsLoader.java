package com.example.android.themoviedbproject.loader;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.example.android.themoviedbproject.BuildConfig;
import com.example.android.themoviedbproject.common.Constants;
import com.example.android.themoviedbproject.common.MovieUtil;
import com.example.android.themoviedbproject.model.Movie;
import com.example.android.themoviedbproject.model.Video;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by gparmar on 29/05/17.
 */

public class GetVideoDetailsLoader extends AsyncTaskLoader<List<Video>> {
    private String TAG = "GetVideoDetailsLoader";
    private String mMovieId;
    private List<Video> mData;

    public GetVideoDetailsLoader(Context context,String movieId) {
        super(context);
        mMovieId = movieId;
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
    public List<Video> loadInBackground() {
        String videosUrlString = Constants.MOVIE_VIDEOS_URL;
        videosUrlString = videosUrlString.replace("###", mMovieId) +
                "?api_key="+ BuildConfig.THEMOVIEDB_API_KEY;
        try {
            InputStream in = new URL(videosUrlString).openStream();
            String json = IOUtils.toString(in);
            return MovieUtil.getMovieVideoListFromJson(json);
        } catch (IOException e) {
            Log.e(TAG, "Error while getting the videos json.", e);
        }
        return null;
    }

    @Override
    public void deliverResult(List<Video> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }
}
