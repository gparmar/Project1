package com.example.android.themoviedbproject;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.example.android.themoviedbproject.model.Movie;
import com.example.android.themoviedbproject.model.Review;
import com.example.android.themoviedbproject.model.Video;
import com.squareup.picasso.Picasso;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity {
    private static String TAG = "MovieDetailsActivity";
    private TextView mTitleTV;
    private TextView mReleaseDateTV;
    private TextView mUserRatingTV;
    private TextView mPlotTV;
    private ImageView mMoviePosterIV;
    private LinearLayout mTrailersContainer;
    private LinearLayout mReviewsContainer;
    private Button mFavoriteButton;

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

        mTitleTV.setText(movie.getTitle());
        mReleaseDateTV.setText(movie.getReleaseDate());
        mPlotTV.setText(movie.getPlotSynopsis());
        mUserRatingTV.setText(movie.getUserRating());
        CommonUtils.loadImageIntoImageView(this, movie.getImageUrl(), mMoviePosterIV);

        //Get the movie trailers and display them
        new GetVideoDetailsTask().execute(movie.getId());
        //Get the movie reviews and display them
        new GetReviewDetailsTask().execute(movie.getId());

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
                }
            }
        });
    }

    public class GetVideoDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String videosUrlString = Constants.MOVIE_VIDEOS_URL;
            videosUrlString = videosUrlString.replace("###", strings[0]) +
                    "?api_key="+ BuildConfig.THEMOVIEDB_API_KEY;
            try {
                InputStream in = new URL(videosUrlString).openStream();
                String json = IOUtils.toString(in);
                return json;
            } catch (IOException e) {
                Log.e(TAG, "Error while getting the videos json.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            List<Video> videos = MovieUtil.getMovieVideoListFromJson(json);
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
    }
    public class GetReviewDetailsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String reviewsUrlString = Constants.MOVIE_REVIEWS_URL;
            reviewsUrlString = reviewsUrlString.replace("###", strings[0]) +
                    "?api_key="+ BuildConfig.THEMOVIEDB_API_KEY;
            try {
                String json = IOUtils.toString(new URL(reviewsUrlString));
                return json;
            } catch (IOException e) {
                Log.e(TAG, "Error while getting the reviews json.", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String json) {
            List<Review> reviews = MovieUtil.getMovieReviewListFromJson(json);
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
    }
}
