package com.example.android.themoviedbproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.themoviedbproject.adapter.PostersGridAdapter;
import com.example.android.themoviedbproject.common.CommonUtils;
import com.example.android.themoviedbproject.common.Constants;
import com.example.android.themoviedbproject.data.FavoriteMovie;
import com.example.android.themoviedbproject.data.MovieProvider;
import com.example.android.themoviedbproject.loader.GetMoviesLoader;
import com.example.android.themoviedbproject.model.Movie;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {
    private static final String TAG = "MainActivity";
    private static final int SORTED_BY_POPULAR_MOVIES = 0;
    private static final int SORTED_BY_TOP_RATED_MOVIES = 1;
    private static final int SORTED_BY_FAV_MOVIES = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQ_CODE = 10;

    public static final int GET_MOVIES_REQUEST_ID = 10;

    private RecyclerView mMoviePostersRV;
    public static int mSortedBy = 0;
    private PostersGridAdapter mAdapter;
    private GridLayoutManager mLayoutManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSortedBy = Integer.parseInt(CommonUtils.getSharedPref(this, Constants.PROPERTY_SORTED_ON, "0"));

        mMoviePostersRV = (RecyclerView) findViewById(R.id.rv_movie_posters);

        //LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);

        int orientation = getResources().getConfiguration().orientation;
        if (Configuration.ORIENTATION_PORTRAIT == orientation) {
            mLayoutManager = new GridLayoutManager(this, 3);
        } else {
            mLayoutManager = new GridLayoutManager(this, 6);
        }
        mMoviePostersRV.setLayoutManager(mLayoutManager);
        mAdapter = new PostersGridAdapter(this, null);
        mMoviePostersRV.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            List<Movie> movies = savedInstanceState.getParcelableArrayList("movies");
            mAdapter.setMovies(movies);
            int scrollPosition = savedInstanceState.getInt("scrollPosition");
            mLayoutManager.scrollToPosition(scrollPosition);
            if (SORTED_BY_POPULAR_MOVIES == mSortedBy) {
                setTitle("Popular Movies");
            } else if (SORTED_BY_TOP_RATED_MOVIES == mSortedBy) {
                setTitle("Top Rated Movies");
            } else if (SORTED_BY_FAV_MOVIES == mSortedBy) {
                setTitle("Favorite Movies");
            }
        } else {
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Ask for WRITE_EXTERNAL_STORAGE permission from the user, if not present
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {

                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    new AlertDialog.Builder(this).setMessage("To support offline mode" +
                            " we need your permission to write to your local folder.")
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    WRITE_EXTERNAL_STORAGE_REQ_CODE);
                            dialogInterface.dismiss();
                        }
                    }).show();

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            WRITE_EXTERNAL_STORAGE_REQ_CODE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "Granted permission to write to local folders");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        int scrollPosition = mLayoutManager.findFirstVisibleItemPosition();
        outState.putInt("scrollPosition", scrollPosition);
        outState.putParcelableArrayList("movies",
                (ArrayList<? extends Parcelable>) mAdapter.getMovies());
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
                .buildUpon().appendQueryParameter("api_key", BuildConfig.THEMOVIEDB_API_KEY)
                .build();
        Log.d(TAG, "Will make http request " + uri.toString());

        Bundle queryBundle = new Bundle();
        queryBundle.putString("uri", uri.toString());
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> loader = loaderManager.getLoader(GET_MOVIES_REQUEST_ID);
        if (loader == null) {
            loaderManager.initLoader(GET_MOVIES_REQUEST_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(GET_MOVIES_REQUEST_ID, queryBundle, this);
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

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
        if (args == null) {
            return null;
        }
        String uri = args.getString("uri", "");
        try {
            URL url = new URL(uri);
            return new GetMoviesLoader(this, url);
        } catch (MalformedURLException e) {
            Log.e(TAG, "error while creating url from uri:" + uri);
        }
        return null;
    }


    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        if (movies != null) {
            Log.d(TAG, "Got movie list:" + movies);
            mAdapter.setMovies(movies);
        } else {
            movies = new ArrayList<>();
            mAdapter.setMovies(movies);
            Toast.makeText(MainActivity.this,
                    R.string.no_movies_msg,
                    Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
        mAdapter.setMovies(null);
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
