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

import android.net.Uri;

import com.paulmender.udacity.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Query the MovieDB API on the web.
 * Created by Paul on 8/22/2017.
 */

public class MovieDbUtility {

    /**
     * MovieDbUtility defines the URLs, URI components and column names used from the MovieDB.
     * Note: This approach is based on the Sunshine application.
     */
    //private static final String LOG_TAG = "pkmi debug: "+MovieDbUtility.class.getSimpleName();

    //region Enumerators
    public enum MovieSortOrder {
        MOVIE_SORT_POPULAR,
        MOVIE_SORT_TOP_RATED,
        MOVIE_SORT_DEFAULT
    }

    //endregion

    //region Private static fields
    private static final String sApiKey = BuildConfig.MOVIE_DB_API_KEY;
    private static final String sEndPointDiscover = "/discover";
    private static final String sEndPointDiscoverMovie = "/discover/movie";
    private static final String sEndPointFind = "/find";
    private static final String sEndPointImage = "image";
    private static final String sEndPointSearch = "/search";
    private static final String sEndPointMoviePopular = "/movie/popular";
    private static final String sEndPointMovieTopRated = "/movie/top_rated";

    private static final String sSortByPopularDesc = "popularity.desc";
    private static final String sSortByRatingDesc = "vote_average.desc";
    private static final String sSortByNone = "";

    private static final String sUriScheme = "http";

    private static final String sWebProtocol = sUriScheme +"://";
    private static final String sWebProtocolSecure = sUriScheme +"s://";

    /*
    * The base URI for connecting to the Movie DB.
    */
    private static final String sUriBase = sWebProtocolSecure+"api.themoviedb.org/3";

    /**
     * Parameter constants
     */
    private static final String sParamApiKey = "api_key";
    private static final String sParamSortByKey = "sort_by";

    /**
     * The base image URI used in conjunction with the poster path to complete the
     * URL to fetch images.
     */
    private static final String sUriImageAuthority = "image.tmdb.org/t/p/";

    /** Screen size options. */
    private static final String sUriImageSize92 = "w92";
    private static final String sUriImageSize154 = "w154";
    private static final String sUriImageSize185 = "w185";
    private static final String sUriImageSize342 = "w342";
    private static final String sUriImageSize500 = "w500";
    private static final String sUriImageSize780 = "w780";
    private static final String sUriImageSizeOriginal = "original";
    // endregion

    //region Private static methods

    /**
     * Builds the URL used to query the MovieDB.
     * @param endPoint The end point (defined by the END_POINT... constants).
     * @param sortBy The sort order (defined by the SORT_BY... constants).
     * @return The URL.
     * Example: https://api.themoviedb.org/3/discover/movie?api_key=<<api_key>>&sort_by=popularity.desc
     */
    private static URL buildMovieUrl(String api_key, String endPoint, String sortBy) {

        String baseUrl = sUriBase + endPoint;

        Uri builtUri = android.net.Uri.parse(baseUrl);
        builtUri = builtUri.buildUpon().appendQueryParameter(sParamApiKey, api_key).build();
        if (sortBy != null && !sortBy.equals(sSortByNone)) {
            builtUri = builtUri.buildUpon()
                    .appendQueryParameter(sParamSortByKey, sortBy)
                    .build();
        }
        return getUrl(builtUri);
    }

    private static URL getUrl(android.net.Uri uri){
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    //endregion

    /**
     * Encapsulates the translation of the MovieSortOrder enumerator.
     */
    public static class MovieSortOrderWrapper {

        // Constructor
        public MovieSortOrderWrapper(){}

        public static MovieSortOrder getEnumerator(String sortOrder){
            MovieSortOrder movieSortOrder;
            if (sortOrder.equals(String.valueOf(MovieSortOrder.MOVIE_SORT_POPULAR))){
                movieSortOrder =  MovieSortOrder.MOVIE_SORT_POPULAR;
            } else if (sortOrder.equals(String.valueOf(MovieSortOrder.MOVIE_SORT_TOP_RATED))) {
                movieSortOrder =  MovieSortOrder.MOVIE_SORT_TOP_RATED;
            } else if (sortOrder.equals(String.valueOf(MovieSortOrder.MOVIE_SORT_DEFAULT))){
                movieSortOrder =  MovieSortOrder.MOVIE_SORT_DEFAULT;
            } else {
                movieSortOrder =  MovieSortOrder.MOVIE_SORT_DEFAULT;
            }

            return movieSortOrder;
        }

        public static String getString(MovieSortOrder sortOrder){
            String movieSortOrder;
            if (sortOrder == MovieSortOrder.MOVIE_SORT_POPULAR){
                movieSortOrder =  String.valueOf(MovieSortOrder.MOVIE_SORT_POPULAR);
            } else if (sortOrder == MovieSortOrder.MOVIE_SORT_TOP_RATED) {
                movieSortOrder =  String.valueOf(MovieSortOrder.MOVIE_SORT_TOP_RATED);
            } else if (sortOrder == MovieSortOrder.MOVIE_SORT_DEFAULT) {
                movieSortOrder =  String.valueOf(MovieSortOrder.MOVIE_SORT_DEFAULT);
            } else {
                // MOVIE_SORT_DEFAULT
                movieSortOrder =  String.valueOf(MovieSortOrder.MOVIE_SORT_POPULAR);
            }

            return movieSortOrder;
        }

        private static String getSortByParameter(MovieSortOrder sortOrder){
            String sortByParameter;
            if (sortOrder == MovieSortOrder.MOVIE_SORT_POPULAR){
                sortByParameter =  sSortByPopularDesc;
            } else if (sortOrder == MovieSortOrder.MOVIE_SORT_TOP_RATED) {
                sortByParameter =  sSortByRatingDesc;
            } else {
                // MOVIE_SORT_DEFAULT
                sortByParameter =  sSortByPopularDesc;
            }

            return sortByParameter;
        }

        public static MovieSortOrder getDefault(){
            return MovieSortOrder.MOVIE_SORT_DEFAULT;
        }

        private static String getEndPoint(MovieSortOrder sortOrder){
            String endPoint;

            if (sortOrder == MovieSortOrder.MOVIE_SORT_POPULAR){
                endPoint = sEndPointMoviePopular;
            } else if (sortOrder == MovieSortOrder.MOVIE_SORT_TOP_RATED) {
                endPoint =  sEndPointMovieTopRated;
            } else {
                // MOVIE_SORT_DEFAULT
                endPoint = sEndPointMoviePopular;
            }

            return endPoint;
        }

    }
    
    //region Public static methods

    public static URL getURL(MovieSortOrder sortOrder){
        return buildMovieUrl(sApiKey,MovieSortOrderWrapper.getEndPoint(sortOrder),
                        MovieSortOrderWrapper.getSortByParameter(sortOrder));
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    /**
     * Builds the URI used to retrieve an set the ImageView URI of poster from the MovieDB.
     * @param imagePath The Movie's image path.
     */
    public static String getImageUrlString(String imagePath) {

        // 185 image size was recommended in Udacity's Popular Movies Implementation Guide & Spec.

        return sWebProtocol + sUriImageAuthority + sUriImageSize185 + imagePath;
    }

    // /endregion Public static methods
}


