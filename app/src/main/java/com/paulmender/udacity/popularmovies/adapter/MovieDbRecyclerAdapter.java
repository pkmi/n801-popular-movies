/*
 * Copyright (C) 2016 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paulmender.udacity.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
//import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.R;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Recycler Adapter for the MovieDb.
 * References:
 *  https://guides.codepath.com/android/using-the-recyclerview#creating-the-recyclerview-adapter.
 * Created by Paul on 8/28/2017.
 */
public class MovieDbRecyclerAdapter extends
        RecyclerView.Adapter<MovieDbRecyclerAdapter.MovieDbViewHolder>{

    //private static final String LOG_TAG = MovieDbRecyclerAdapter.class.getSimpleName();

    // Stores a list of parcelable movies from the MovieDB API.
    private List<MovieParcelable> mMovieList;

    /**
     * Define an on-click handler to allow an Activity to interface with the RecyclerView.
     */
    private final MovieDbAdapterOnClickHandler mClickHandler;

    /**
     * The interface for receiving click messages.
     */
    public interface MovieDbAdapterOnClickHandler {
        void onClick(MovieParcelable movieDetail);
    }

    /** Constructor
     * clickHandler
     */
    public MovieDbRecyclerAdapter(MovieDbAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * The view cache of the MovieDb items.
     * Notes:
     *   PKMI Removed static from the class definition, perhaps this will fix not displaying
     *   data in the main activity.
     */
    public class MovieDbViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        private final Context context;

        private final ImageView moviePosterImageView;

        /**
         * Called when the child views are clicked.
         * @param v The View that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            MovieParcelable movie = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movie);
        }

        // Constructor
        private MovieDbViewHolder(View movieView) {
            super(movieView);

            context = movieView.getContext();

            movieView.setOnClickListener(this);

            moviePosterImageView = (ImageView) movieView.findViewById(R.id.iv_movie_poster);
        }
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  Note: If the RecyclerView has more than one type of item,
     *                  use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieDbViewHolder that holds the View for each movie poster item.
     */
    @Override
    public MovieDbViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        Context context = viewGroup.getContext();
        int layoutId = R.layout.movie_poster;

        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean attachToViewGroupImmediately = false;

        View movieView = inflater.inflate(layoutId, viewGroup, attachToViewGroupImmediately);
        return new MovieDbViewHolder(movieView);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. Update the contents of the ViewHolder to display the movie
     * for this particular position.
     *
     * @param holder The ViewHolder which should be updated to represent the
     *               contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieDbViewHolder holder, int position) {

        try {
        String loadImage =  MovieDbUtility.getImageUrlString(mMovieList.get(position).posterPath);
            Picasso.with(holder.context).
                    load(loadImage).
                    into(holder.moviePosterImageView);
            //Log.d(LOG_TAG,getResources().getString(R.string.pkmi_debug)+"Picasso: "+loadImage);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (mMovieList == null){
            return 0;
        }
        else {
            return mMovieList.size();
        }
    }

    /**
     * This method is used to set the movie data if the RecyclerAdapter already exists.
     * This allows new data query without the need to create a new RecyclerAdapter to display it.
     * References:
     *  Udacity S03.01-Solution-RecyclerView.
     * @param movieList The new movies to be displayed.
     */
    public void setMovieData(List<MovieParcelable> movieList) {
        mMovieList = movieList;
        notifyDataSetChanged();
    }
}
