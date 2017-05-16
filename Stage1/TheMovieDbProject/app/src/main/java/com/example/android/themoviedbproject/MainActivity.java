package com.example.android.themoviedbproject;

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
import com.example.android.themoviedbproject.model.Movie;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView mMoviePostersRV;
    public static boolean mSortedByPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortedByPopular = Boolean.parseBoolean(CommonUtils.getSharedPref(this, Constants.PROPERTY_SORTED_ON, "true"));

        mMoviePostersRV = (RecyclerView)findViewById(R.id.rv_movie_posters);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mMoviePostersRV.setLayoutManager(layoutManager);
        mMoviePostersRV.setAdapter(new PostersGridAdapter(this, null));

        if (mSortedByPopular) {
            showMoviePosters(Constants.POPULAR_MOVIES_URL);
        } else {
            showMoviePosters(Constants.TOPRATED_MOVIES_URL);
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
                .buildUpon().appendQueryParameter("api_key",BuildConfig.THEMOVIEDB_API_KEY)
                .build();
        Log.d(TAG,"Will make http request "+uri.toString());
        try {
            new GetMovies().execute(new URL(uri.toString()));
        } catch (MalformedURLException e) {
            Log.e(TAG, "Error while getting popular movies", e);
        }
        CommonUtils.putSharedPref(this, Constants.PROPERTY_SORTED_ON, mSortedByPopular);
    }

    public class GetMovies extends AsyncTask<URL, Void, List<Movie>> {

        @Override
        protected List<Movie> doInBackground(URL... urls) {
            if (urls != null && urls.length > 0) {
                try {
                    //I am using apache commons IOUtils utility to convert
                    //a url directly to text.
                    String json = IOUtils.toString(urls[0]);
                    Log.d(TAG,"Json from TMD:"+json);
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
                Log.d(TAG, "Got movie list:"+movies);
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
            mSortedByPopular = true;
            showMoviePosters(Constants.POPULAR_MOVIES_URL);
        } else if (item.getItemId() == R.id.sort_by_toprated) {
            mSortedByPopular = false;
            showMoviePosters(Constants.TOPRATED_MOVIES_URL);
        }

        return false;
    }

    private void toggleMenuItems(Menu menu){
        MenuItem popularMenuItem = menu.findItem(R.id.sort_by_popular);
        MenuItem topratedMenuItem = menu.findItem(R.id.sort_by_toprated);
        //If the movies are sorted by popular movies, which is the default,
        //then hide the "sort by popular" menu item and vice versa for toprated
        if (mSortedByPopular) {
            popularMenuItem.setVisible(false);
            topratedMenuItem.setVisible(true);
        } else {
            topratedMenuItem.setVisible(false);
            popularMenuItem.setVisible(true);
        }
    }
}
