package com.example.android.themoviedbproject.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.Table;

/**
 * Created by gparmar on 15/05/17.
 */
@Database(version = MovieDatabase.VERSION)
public final class MovieDatabase {
    public static final int VERSION = 2;

    public MovieDatabase() {
    }

    @Table(FavoriteMovie.class)
    public static final String FAVORITE_MOVIES = "favorite_movies";
}
