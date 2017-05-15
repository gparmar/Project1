package com.example.android.themoviedbproject.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;

/**
 * Created by gparmar on 15/05/17.
 */

public interface FavoriteMovie {
    @DataType(DataType.Type.INTEGER) @PrimaryKey
    @AutoIncrement
    public static final String _ID =
            "_id";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String TITLE = "title";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RATING = "user_rating";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String RELEASE_DATE = "release_date";
    @DataType(DataType.Type.TEXT) @NotNull
    public static final String PLOT = "plot";

}
