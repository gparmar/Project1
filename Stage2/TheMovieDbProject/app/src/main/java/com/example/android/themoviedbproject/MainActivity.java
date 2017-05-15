package com.example.android.themoviedbproject;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.themoviedbproject.adapter.PostersGridAdapter;
import com.example.android.themoviedbproject.common.CommonUtils;
import com.example.android.themoviedbproject.common.Constants;
import com.example.android.themoviedbproject.common.MovieUtil;
import com.example.android.themoviedbproject.common.NetworkUtil;
import com.example.android.themoviedbproject.data.FavoriteMovie;
import com.example.android.themoviedbproject.data.MovieProvider;
import com.example.android.themoviedbproject.model.Movie;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int SORTED_BY_POPULAR_MOVIES = 0;
    private static final int SORTED_BY_TOP_RATED_MOVIES = 1;
    private static final int SORTED_BY_FAV_MOVIES = 2;
    private RecyclerView mMoviePostersRV;
    public static int mSortedBy = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortedBy = Integer.parseInt(CommonUtils.getSharedPref(this, Constants.PROPERTY_SORTED_ON, "0"));

        mMoviePostersRV = (RecyclerView) findViewById(R.id.rv_movie_posters);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mMoviePostersRV.setLayoutManager(layoutManager);
        mMoviePostersRV.setAdapter(new PostersGridAdapter(this, null));

        if (SORTED_BY_POPULAR_MOVIES == mSortedBy) {
            setTitle("Popular Movies");
            showMoviePosters(Constants.POPULAR_MOVIES_URL);
        } else if (SORTED_BY_TOP_RATED_MOVIES == mSortedBy) {
            setTitle("Top Rated Movies");
            showMoviePosters(Constants.TOPRATED_MOVIES_URL);
        } else if (SORTED_BY_FAV_MOVIES == mSortedBy) {
            setTitle("Favorite Movies");
            showFavoriteMovies();
        }
    }

    /**
     * A method that initiates the http query of movies from the given url
     * and the results in displaying the movie posters in the app.
     *
     * @param moviesUrl
     */
    private void showMoviePosters(String moviesUrl) {
        //Get the popular movies and display them as a grid
        Uri uri = Uri.parse(moviesUrl)
                .buildUpon().appendQueryParameter("api_key", Constants.THEMOVIEDB_API_KEY)
                .build();
        Log.d(TAG, "Will make http request " + uri.toString());
        try {
            new GetMovies().execute(new URL(uri.toString()));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while getting popular movies", e);
        }

        if (SORTED_BY_POPULAR_MOVIES == mSortedBy) {
            setTitle("Popular Movies");
        } else if (SORTED_BY_TOP_RATED_MOVIES == mSortedBy) {
            setTitle("Top Rated Movies");
        }
    }

    private void showFavoriteMovies() {
        Cursor c = getContentResolver().query(MovieProvider.FavoriteMovies.CONTENT_URI,
                null, null, null, null);
        List<Movie> movies = new ArrayList<>();
        if (c != null && c.getCount() > 0) {

            c.moveToFirst();
            do {
                Movie m = new Movie();
                try {
                    m.setTitle(c.getString(c.getColumnIndex(FavoriteMovie.TITLE)));
                    m.setUserRating(c.getString(c.getColumnIndex(FavoriteMovie.RATING)));
                    m.setReleaseDate(c.getString(c.getColumnIndex(FavoriteMovie.RELEASE_DATE)));
                    m.setPlotSynopsis(c.getString(c.getColumnIndex(FavoriteMovie.PLOT)));
                    m.setImageUrl(c.getString(c.getColumnIndex(FavoriteMovie.IMAGE_URL)));
                    m.setId(c.getString(c.getColumnIndex(FavoriteMovie._ID)));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                movies.add(m);
                c.moveToNext();
            } while (!c.isAfterLast());

        }

        Log.d(TAG, "Got movie list:" + movies);
        PostersGridAdapter adapter = (PostersGridAdapter) mMoviePostersRV.getAdapter();
        adapter.setMovies(movies);
        setTitle("Favorite Movies");
    }

    public class GetMovies extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(URL... urls) {
            if (urls != null && urls.length > 0) {
                try {
                    //I am using apache commons IOUtils utility to convert
                    //a url directly to text.
                    String json = IOUtils.toString(urls[0]);
                    Log.d(TAG, "Json from TMD:" + json);
                    return MovieUtil.getMovieListFromJson(json);
                } catch (IOException e) {
                    Log.e(TAG, "Error while getting scanner", e);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            if (movies != null) {
                Log.d(TAG, "Got movie list:" + movies);
                PostersGridAdapter adapter = (PostersGridAdapter) mMoviePostersRV.getAdapter();
                adapter.setMovies(movies);
            } else {
                Toast.makeText(MainActivity.this,
                        "No movies found. Please check your internet",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        //Set the right menu items to show or hide
        toggleMenuItems(menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Set the right menu items to show or hide
        toggleMenuItems(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.sort_by_popular) {
            mSortedBy = SORTED_BY_POPULAR_MOVIES;
            showMoviePosters(Constants.POPULAR_MOVIES_URL);
        } else if (item.getItemId() == R.id.sort_by_toprated) {
            mSortedBy = SORTED_BY_TOP_RATED_MOVIES;
            showMoviePosters(Constants.TOPRATED_MOVIES_URL);
        } else if (item.getItemId() == R.id.sort_by_favorites) {
            mSortedBy = SORTED_BY_FAV_MOVIES;
            showFavoriteMovies();
        }
        CommonUtils.putSharedPref(this, Constants.PROPERTY_SORTED_ON, mSortedBy);
        return false;
    }

    private void toggleMenuItems(Menu menu) {
        MenuItem popularMenuItem = menu.findItem(R.id.sort_by_popular);
        MenuItem topratedMenuItem = menu.findItem(R.id.sort_by_toprated);
        MenuItem favoritesMenuItem = menu.findItem(R.id.sort_by_favorites);
        //If the movies are sorted by popular movies, which is the default,
        //then hide the "sort by popular" menu item and vice versa for toprated
        if (SORTED_BY_POPULAR_MOVIES == mSortedBy) {
            popularMenuItem.setVisible(false);
            topratedMenuItem.setVisible(true);
            favoritesMenuItem.setVisible(true);
        } else if (SORTED_BY_TOP_RATED_MOVIES == mSortedBy) {
            topratedMenuItem.setVisible(false);
            popularMenuItem.setVisible(true);
            favoritesMenuItem.setVisible(true);
        } else if (SORTED_BY_FAV_MOVIES == mSortedBy) {
            topratedMenuItem.setVisible(true);
            popularMenuItem.setVisible(true);
            favoritesMenuItem.setVisible(false);
        }
    }
}