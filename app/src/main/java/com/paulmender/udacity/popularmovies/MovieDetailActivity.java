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
package com.paulmender.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.squareup.picasso.Picasso;

/**
 * Display the details of the movie selected from the main activity:
 * title, release date, movie poster, user rating (vote average), and plot synopsis (overview).
 * Reference:
 *  Popular Movies App Implementation Guide, UX Mockups.
 */
public class MovieDetailActivity extends AppCompatActivity {

    private final String LOG_TAG = "pkmi debug: "+
            MovieDetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        setMovieDetailContent();
    }

    //region Private Methods

    /**
     * Populate the movie detail view.
     */
    private void setMovieDetailContent() {

        TextView Title = (TextView) findViewById(R.id.tv_movie_detail_title);
        ImageView Poster = (ImageView) findViewById(R.id.iv_movie_detail_poster);
        TextView Year = (TextView) findViewById(R.id.tv_movie_detail_year);
        TextView VoteAverage = (TextView) findViewById(R.id.tv_movie_detail_vote_average);
        TextView Overview = (TextView) findViewById(R.id.tv_movie_detail_overview);

        Intent callerIntent = getIntent();

        if (callerIntent != null) {

            if (callerIntent.hasExtra(MainActivity.KEY_MOVIE_DETAIL)) {
                MovieParcelable mMovie = getIntent().getParcelableExtra(MainActivity.KEY_MOVIE_DETAIL);
                Title.setText(mMovie.title);

                String loadImage = MovieDbUtility.getImageUrlString(mMovie.posterPath);
                Picasso.with(this).
                        load(loadImage).
                        into(Poster);

                String year = mMovie.releaseDate.substring(0,4);
                Year.setText(year);

                String voteAverageText= mMovie.voteAverage +
                        getResources().getString(R.string.vote_scale_suffix);
                VoteAverage.setText(voteAverageText);

                Overview.setText(mMovie.overview);
            }
        }
        //endregion Private Methods
    }
}
