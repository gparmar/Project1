package com.example.android.themoviedbproject.common;

import com.example.android.themoviedbproject.model.Movie;
import com.example.android.themoviedbproject.model.Review;
import com.example.android.themoviedbproject.model.Video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gparmar on 08/05/17.
 */

public class MovieUtil {
    public static List<Movie> getMovieListFromJson(String json){
        if (CommonUtils.isNotEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray moviesArray = (JSONArray) jsonObject.get("results");
                List<Movie> results = new ArrayList<>();
                for (int i=0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = (JSONObject) moviesArray.get(i);
                    Movie movie = new Movie();
                    movie.setImageUrl(movieObj.getString("poster_path"));
                    movie.setUserRating(movieObj.getString("vote_average"));
                    movie.setTitle(movieObj.getString("title"));
                    movie.setPlotSynopsis(movieObj.getString("overview"));
                    movie.setReleaseDate(movieObj.getString("release_date"));
                    movie.setId(movieObj.getString("id"));
                    results.add(movie);
                }
                return results;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static List<Video> getMovieVideoListFromJson(String json){
        if (CommonUtils.isNotEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray videosArray = (JSONArray) jsonObject.get("results");
                List<Video> results = new ArrayList<>();
                for (int i=0; i < videosArray.length(); i++) {
                    JSONObject videoObj = (JSONObject) videosArray.get(i);
                    Video video = new Video();
                    video.setId(videoObj.getString("id"));
                    video.setKey(videoObj.getString("key"));
                    video.setName(videoObj.getString("name"));
                    results.add(video);
                }
                return results;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static List<Review> getMovieReviewListFromJson(String json){
        if (CommonUtils.isNotEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray reviewsArray = (JSONArray) jsonObject.get("results");
                List<Review> results = new ArrayList<>();
                for (int i=0; i < reviewsArray.length(); i++) {
                    JSONObject reviewObj = (JSONObject) reviewsArray.get(i);
                    Review review = new Review();
                    review.setId(reviewObj.getString("id"));
                    review.setAuthor(reviewObj.getString("author"));
                    review.setContent(reviewObj.getString("content"));
                    review.setUrl(reviewObj.getString("url"));
                    results.add(review);
                }
                return results;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
