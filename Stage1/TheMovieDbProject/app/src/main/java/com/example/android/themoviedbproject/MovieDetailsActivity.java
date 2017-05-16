package com.example.android.themoviedbproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.themoviedbproject.common.Constants;
import com.example.android.themoviedbproject.model.Movie;
import com.squareup.picasso.Picasso;

public class MovieDetailsActivity extends AppCompatActivity {
    private TextView mTitleTV;
    private TextView mReleaseDateTV;
    private TextView mUserRatingTV;
    private TextView mPlotTV;
    private ImageView mMoviePosterIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        Movie movie = getIntent().getParcelableExtra("movie");

        mTitleTV = (TextView) findViewById(R.id.title);
        mReleaseDateTV = (TextView) findViewById(R.id.release_date);
        mPlotTV = (TextView) findViewById(R.id.plot_synopsis);
        mUserRatingTV = (TextView) findViewById(R.id.user_rating);
        mMoviePosterIV = (ImageView) findViewById(R.id.movie_poster);


        mTitleTV.setText(movie.getTitle());
        mReleaseDateTV.setText(movie.getReleaseDate());
        mPlotTV.setText(movie.getPlotSynopsis());
        mUserRatingTV.setText(movie.getUserRating());
        Picasso.with(this).load(Constants.IMAGE_BASE_URL+
                Constants.IMAGE_SIZE+movie.getImageUrl()).into(mMoviePosterIV);
    }
}
