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
package com.paulmender.udacity.popularmovies.utility;

import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.data.MovieDbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilities to manage the MovieDb JSON data.
 * References:
 *   Udacity SO3.01-Solution-RecylerView, OpenWeatherJsonUtils
 * Created by Paul on 8/28/2017.
 */
public final class MovieDbJsonUtility {
    // private static final String LOG_TAG = MovieDbJsonUtility.class.getSimpleName();

    private static final String sMdJsonCode = "cod";
    private static final String sMdResults = "results";

    /**
     * Parse the JSON response from the MovieDB API.
     *
     * @param movieDbJsonString response from server.
     * @return An array of strings describing the MovieDB data.
     * @throws JSONException If JSON data cannot be parsed.
     */
    public static List<MovieParcelable> getMovieDbStringsFromJson(String movieDbJsonString)
            throws JSONException {

        List<MovieParcelable> parsedMovies = new ArrayList<>();
        JSONObject movieJson = new JSONObject(movieDbJsonString);

        if (isValidJsonObject(movieJson)){

            JSONArray movieJsonArray = movieJson.getJSONArray(sMdResults);
            for (int i = 0; i< movieJsonArray.length(); i++){

                JSONObject movie = movieJsonArray.getJSONObject(i);

                MovieParcelable movieParcelable = new MovieParcelable();
                movieParcelable.movieId = movie.getInt(MovieDbContract.MOVIE_DB_ID);
                movieParcelable.overview = movie.getString(MovieDbContract.MOVIE_DB_OVERVIEW);
                movieParcelable.posterPath = movie.getString(MovieDbContract.MOVIE_DB_POSTER_PATH);
                movieParcelable.releaseDate = movie.getString(MovieDbContract.MOVIE_DB_RELEASE_DATE);
                movieParcelable.title = movie.getString(MovieDbContract.MOVIE_DB_TITLE);
                movieParcelable.voteAverage = movie.getString(MovieDbContract.MOVIE_DB_VOTE_AVERAGE);

                parsedMovies.add(i,movieParcelable);
            }
        }
        return parsedMovies;
    }

    private static boolean isValidJsonObject(JSONObject movieJson){
        boolean isValid = true;
        if (movieJson.has(sMdJsonCode)) {
            try {
                int errorCode = movieJson.getInt(sMdJsonCode);
                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        isValid = true;
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                    /* invalid query*/
                        isValid = false;
                        break;
                    default:
                    /* Server probably down */
                        isValid = false;
                        break;
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return isValid;
    }
}