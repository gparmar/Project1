package com.example.android.themoviedbproject;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.themoviedbproject.common.CommonUtils;
import com.example.android.themoviedbproject.common.Constants;
import com.example.android.themoviedbproject.common.MovieUtil;
import com.example.android.themoviedbproject.data.FavoriteMovie;
import com.example.android.themoviedbproject.data.MovieProvider;
import com.example.android.themoviedbproject.loader.CheckFavoriteLoader;
import com.example.android.themoviedbproject.loader.GetMoviesLoader;
import com.example.android.themoviedbproject.loader.GetReviewsLoader;
import com.example.android.themoviedbproject.loader.GetVideoDetailsLoader;
import com.example.android.themoviedbproject.model.Movie;
import com.example.android.themoviedbproject.model.Review;
import com.example.android.themoviedbproject.model.Video;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private static String TAG = "MovieDetailsActivity";

    public static final int GET_VIDEOS_REQUEST_ID = 1;
    public static final int GET_REVIEWS_REQUEST_ID = 2;
    public static final int GET_FAVORITES_REQUEST_ID = 3;

    private TextView mTitleTV;
    private TextView mReleaseDateTV;
    private TextView mUserRatingTV;
    private TextView mPlotTV;
    private ImageView mMoviePosterIV;
    private LinearLayout mTrailersContainer;
    private LinearLayout mReviewsContainer;
    private Button mFavoriteButton;
    private Button mUnfavoriteButton;

    LoaderManager.LoaderCallbacks<List<Video>> mVideosCallbacks =
            new LoaderManager.LoaderCallbacks<List<Video>>() {
                @Override
                public Loader<List<Video>> onCreateLoader(int id, Bundle args) {
                    if (args == null) {
                        return null;
                    }
                    String movieId = args.getString("movieId", "");
                    return new GetVideoDetailsLoader(MovieDetailsActivity.this, movieId);
                }

                @Override
                public void onLoadFinished(Loader<List<Video>> loader, List<Video> videos) {
                    if (videos != null && videos.size() > 0) {
                        LayoutInflater inflater = LayoutInflater.from(MovieDetailsActivity.this);
                        mTrailersContainer.removeAllViews();
                        for (final Video video: videos) {
                            View view = inflater.inflate(R.layout.trailers_item, mTrailersContainer, false);
                            ImageView iv = (ImageView) view.findViewById(R.id.trailer_thumbnail);
                            Picasso.with(MovieDetailsActivity.this).load(Constants.YOUTUBE_THUMBNAIL_URL.replace("###",video.getKey()))
                                    .into(iv);
                            TextView name = (TextView) view.findViewById(R.id.trailer_name);
                            name.setText(video.getName());

                            //Add the onClickListener on the view
                            view.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + video.getKey()));
                                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("http://www.youtube.com/watch?v=" + video.getKey()));
                                    try {
                                        startActivity(appIntent);
                                    } catch (ActivityNotFoundException ex) {
                                        startActivity(webIntent);
                                    }
                                }
                            });

                            mTrailersContainer.addView(view);
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Video>> loader) {

                }
            };
    LoaderManager.LoaderCallbacks<List<Review>> mReviewsCallbacks =
            new LoaderManager.LoaderCallbacks<List<Review>>() {
                @Override
                public Loader<List<Review>> onCreateLoader(int id, Bundle args) {
                    if (args == null) {
                        return null;
                    }
                    String movieId = args.getString("movieId", "");
                    return new GetReviewsLoader(MovieDetailsActivity.this, movieId);
                }

                @Override
                public void onLoadFinished(Loader<List<Review>> loader, List<Review> reviews) {
                    if (reviews != null && reviews.size() > 0) {
                        LayoutInflater inflater = LayoutInflater.from(MovieDetailsActivity.this);
                        mReviewsContainer.removeAllViews();
                        for (Review review: reviews) {
                            View view = inflater.inflate(R.layout.reviews_item, mReviewsContainer, false);

                            TextView reviewTV = (TextView) view.findViewById(R.id.review);
                            TextView author = (TextView) view.findViewById(R.id.author);
                            reviewTV.setText(review.getContent());
                            author.setText(" - "+review.getAuthor());
                            mReviewsContainer.addView(view);
                        }
                    }
                }

                @Override
                public void onLoaderReset(Loader<List<Review>> loader) {

                }
            };
    LoaderManager.LoaderCallbacks<Cursor> mFavoritesCallbacks =
            new LoaderManager.LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                    if (args == null) {
                        return null;
                    }
                    String movieId = args.getString("movieId", "");
                    return new CheckFavoriteLoader(MovieDetailsActivity.this, movieId);
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                    if (cursor != null && cursor.getCount() > 0) {
                        //This movie has been favorited.
                        mFavoriteButton.setVisibility(View.GONE);
                        mUnfavoriteButton.setVisibility(View.VISIBLE);
                    } else {
                        //This movie has been not been favorited.
                        mFavoriteButton.setVisibility(View.VISIBLE);
                        mUnfavoriteButton.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {

                }
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        final Movie movie = getIntent().getParcelableExtra("movie");

        mTitleTV = (TextView) findViewById(R.id.title);
        mReleaseDateTV = (TextView) findViewById(R.id.release_date);
        mPlotTV = (TextView) findViewById(R.id.plot_synopsis);
        mUserRatingTV = (TextView) findViewById(R.id.user_rating);
        mMoviePosterIV = (ImageView) findViewById(R.id.movie_poster);
        mTrailersContainer = (LinearLayout) findViewById(R.id.trailers_container);
        mReviewsContainer = (LinearLayout) findViewById(R.id.reviews_container);
        mFavoriteButton = (Button) findViewById(R.id.favorite_btn);
        mUnfavoriteButton = (Button) findViewById(R.id.unfavorite_btn);

        mReleaseDateTV.setText(movie.getReleaseDate());
        mPlotTV.setText(movie.getPlotSynopsis());
        mUserRatingTV.setText(movie.getUserRating());
        CommonUtils.loadImageIntoImageView(this, movie.getImageUrl(), mMoviePosterIV);

        setTitle(getString(R.string.movie_detail));
        //Set the title of the movie
        mTitleTV.setText(movie.getTitle());

        Bundle args = new Bundle();
        args.putString("movieId", movie.getId());
        //Get the movie trailers and display them
        getSupportLoaderManager().initLoader(GET_VIDEOS_REQUEST_ID, args, mVideosCallbacks);

        //Get the movie reviews and display them
        getSupportLoaderManager().initLoader(GET_REVIEWS_REQUEST_ID, args, mReviewsCallbacks);

        //Check if the movie is favorited. If so then hide the favorite button
        //and unhide the unfavorite button and vice versa if otherwise.
        getSupportLoaderManager().initLoader(GET_FAVORITES_REQUEST_ID, args, mFavoritesCallbacks);



        //Set on click listener to the favorite button
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = MovieDetailsActivity.this.getContentResolver().query(
                        MovieProvider.FavoriteMovies.withId(movie.getId()),
                        null,null,null,null
                );

                if (c==null || (c != null && c.getCount() == 0)) {
                    ContentValues cv = new ContentValues();
                    cv.put(FavoriteMovie._ID, movie.getId());
                    cv.put(FavoriteMovie.PLOT, movie.getPlotSynopsis());
                    cv.put(FavoriteMovie.RATING, movie.getUserRating());
                    cv.put(FavoriteMovie.RELEASE_DATE, movie.getReleaseDate());
                    cv.put(FavoriteMovie.TITLE, movie.getTitle());
                    cv.put(FavoriteMovie.IMAGE_URL, movie.getImageUrl());
                    MovieDetailsActivity.this.getContentResolver().insert(
                            MovieProvider.FavoriteMovies.withId(movie.getId()), cv
                    );
                    mFavoriteButton.setVisibility(View.GONE);
                    mUnfavoriteButton.setVisibility(View.VISIBLE);
                }
            }
        });
        //Set on click listener to the unfavorite button
        mUnfavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MovieDetailsActivity.this.getContentResolver().delete(
                        MovieProvider.FavoriteMovies.withId(movie.getId()),
                        null,null
                );
                mFavoriteButton.setVisibility(View.VISIBLE);
                mUnfavoriteButton.setVisibility(View.GONE);
            }
        });
    }



}
