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

package com.paulmender.udacity.popularmovies.data;

/**
 * Defines the column names used from theS MovieDB.
 * References:
 * This approach is based on the Udacity Sunshine application.
 */

public final class MovieDbContract {

    //region Stage 1 project
    public static final String MOVIE_DB_ID = "id";
    public static final String MOVIE_DB_TITLE = "title";
    public static final String MOVIE_DB_RELEASE_DATE = "release_date";
    public static final String MOVIE_DB_POSTER_PATH = "poster_path";
    public static final String MOVIE_DB_VOTE_AVERAGE = "vote_average";
    public static final String MOVIE_DB_OVERVIEW = "overview";
    public static final String MOVIE_DB_TOTAL_RESULTS = "total_results";
    //endregion

    // The constructor.
    public MovieDbContract(){

    }

}