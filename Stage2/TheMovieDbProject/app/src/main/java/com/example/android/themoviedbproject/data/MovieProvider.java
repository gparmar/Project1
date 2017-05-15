package com.example.android.themoviedbproject.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

/**
 * Created by gparmar on 15/05/17.
 */
@ContentProvider(authority = MovieProvider.AUTHORITY, database = MovieDatabase.class)
public class MovieProvider {
    public static final String AUTHORITY = "com.example.android.themoviedbproject.data.MovieProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    interface Path {
        String FAVORITE_MOVIES = "favorite_movies";
    }

    private static Uri buildUri(String ... paths){
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths){
            builder.appendPath(path);
        }
        return builder.build();
    }

    @TableEndpoint(table = MovieDatabase.FAVORITE_MOVIES)
    public static class FavoriteMovies{
        @ContentUri(
                path = Path.FAVORITE_MOVIES,
                type = "vnd.android.cursor.dir/favorite_movies",
                defaultSort = FavoriteMovie._ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.FAVORITE_MOVIES);

        @InexactContentUri(
                name = "FAVORITE_MOVIE_ID",
                path = Path.FAVORITE_MOVIES + "/#",
                type = "vnd.android.cursor.item/favorite_movies",
                whereColumn = FavoriteMovie._ID,
                pathSegment = 1)
        public static Uri withId(String id){
            return buildUri(Path.FAVORITE_MOVIES, id);
        }
    }
}
