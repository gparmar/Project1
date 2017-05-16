package com.example.android.themoviedbproject.common;

/**
 * Created by gparmar on 08/05/17.
 */

public interface Constants {
    String POPULAR_MOVIES_URL = "http://api.themoviedb.org/3/movie/popular";
    String TOPRATED_MOVIES_URL = "http://api.themoviedb.org/3/movie/top_rated";
    String MOVIE_VIDEOS_URL = "http://api.themoviedb.org/3/movie/###/videos";
    String MOVIE_REVIEWS_URL = "http://api.themoviedb.org/3/movie/###/reviews";
    String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
    String IMAGE_SIZE = "w342";
    String YOUTUBE_THUMBNAIL_URL = "https://i.ytimg.com/vi/###/hqdefault.jpg";
    String LOCAL_IMAGES_FOLDER = "themoviedbpics";

    //Properties
    String PROPERTY_SORTED_ON = "PROPERTY_SORTED_ON";
}
