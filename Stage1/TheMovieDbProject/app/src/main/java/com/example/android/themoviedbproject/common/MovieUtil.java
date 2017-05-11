package com.example.android.themoviedbproject.common;

import com.example.android.themoviedbproject.model.Movie;

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
                    results.add(movie);
                }
                return results;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
