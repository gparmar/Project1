package com.example.android.themoviedbproject.loader;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import com.example.android.themoviedbproject.data.MovieProvider;

/**
 * Created by gparmar on 29/05/17.
 */

public class CheckFavoriteLoader extends AsyncTaskLoader<Cursor> {
    private String TAG = "CheckFavoriteLoader";
    private String mMovieId;
    private Cursor mData;

    public CheckFavoriteLoader(Context context, String movieId) {
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
    public Cursor loadInBackground() {
        Cursor result = null;
        result = getContext().getContentResolver()
                    .query(MovieProvider.FavoriteMovies.withId(mMovieId),
                            null,null,null,null);

        return result;
    }

    @Override
    public void deliverResult(Cursor data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }
}
