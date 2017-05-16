package com.example.android.themoviedbproject.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.themoviedbproject.MovieDetailsActivity;
import com.example.android.themoviedbproject.R;
import com.example.android.themoviedbproject.common.CommonUtils;
import com.example.android.themoviedbproject.model.Movie;

import java.util.List;

/**
 * Created by gparmar on 08/05/17.
 */

public class PostersGridAdapter extends RecyclerView.Adapter<PostersGridAdapter.PosterItemViewHolder> {

    private List<Movie> movies;
    private Context context;

    public PostersGridAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    @Override
    public PosterItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.poster_item, parent, false);
        Log.d("PostersGridAdapter","Returning a new ViewHolder");

        return new PosterItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PosterItemViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bindImage(movie.getImageUrl(), movie);
    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        }
        return 0;
    }

    public void setMovies(List<Movie> movies) {
        Log.d("PostersGridAdapter","Setting a new movies list.");
        this.movies = movies;
        notifyDataSetChanged();
    }

    public class PosterItemViewHolder extends RecyclerView.ViewHolder{
        Movie movie;
        ImageView posterIV;


        public PosterItemViewHolder(View itemView) {
            super(itemView);
            posterIV = (ImageView) itemView.findViewById(R.id.iv_poster);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("movie", movie);
                    context.startActivity(intent);
                }
            });
        }

        public void bindImage(String imageUrl, Movie movie) {
            this.movie = movie;
            CommonUtils.loadImageIntoImageView(context, imageUrl, posterIV);
        }
    }
}
