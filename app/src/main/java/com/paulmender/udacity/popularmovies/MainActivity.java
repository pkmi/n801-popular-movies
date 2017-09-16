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

/*
Popular Movies is an application project for the Udacity Android Developer Nanodegree
Author: Paul Mender
Stage 1 Requirements:
    1. Completed App is written solely in the Java Programming Language.
    2. Completed Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails.
    3. Completed UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated.
    4. Completed: UI contains a screen for displaying the details for a selected movie.
    5. Completed: Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
    6. Completed When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
    7. Completed When a movie poster thumbnail is selected, the movie details screen is launched.
    8. Completed In a background thread, app queries the /movie/popular or /movie/top_rated API for the sort criteria specified in the settings menu.

General Requirements:
    TODO: App conforms to common standards found in the Android Nanodegree General Project Guidelines
    - Java      Completed:
    - Git       Completed: Note: For Stage 1, did not include the type.
    - Core      Completed: Note: The app may or may not adhere to Google Play Store App policies.
    - Tablet

Other Implementation Guide Requirements
    1. Oompleted: (Using gradle.properties/BuildConfig) IMPORTANT: PLEASE REMOVE YOUR API KEY WHEN SHARING CODE PUBLICALLY
    2. Completed: You must make sure your app does not crash when there is no network connection!

*/
package com.paulmender.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
//import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
// Was used for setSortSpinner:
// import android.widget.Spinner;
// import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.paulmender.common.ui.GridLayoutManagerAutofit;
import com.paulmender.udacity.popularmovies.adapter.MovieDbRecyclerAdapter;
import com.paulmender.udacity.popularmovies.utility.MovieDbJsonUtility;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility.MovieSortOrder;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * The Popular Movies application's main activity.
 */
public class MainActivity extends AppCompatActivity
    implements MovieDbRecyclerAdapter.MovieDbAdapterOnClickHandler{

    //region Static Public fields
    public static final String KEY_MOVIE_DETAIL = "movieDetailKey";
    //endregion

    //region Static Private fields
    private static final String LOG_TAG = "pkmi Debug: "+MainActivity.class.getSimpleName();

    private static final String sKeySortOrder = "sortOrderKey";

    private static final int sSortOrderToastTopPosition = 100;

    //endregion

    //region Declare non-public, non-static fields

    private String mMenuTitlePopular;

    private String mMenuTitleTopRated;

    private MovieSortOrder mMovieSortOrder;

    private TextView mErrorMessageTextView;

    private ProgressBar mFetchDataProgressBar;

    private RecyclerView mMoviesRecyclerView;

    private MovieDbRecyclerAdapter mMovieDbRecyclerAdapter;

    private List<MovieParcelable> mMovieList;

    private Toast mSortOrderToast;
    //endregion Declare reference members

    //region Implements Methods
    /**
     * onClick implements the MovieDbAdapterOnClickHandler.
     * @param movieDetail The parcelable movie object.
     */
    @Override
    public void onClick(MovieParcelable movieDetail) {
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(context, destinationClass);

        movieDetailIntent.putExtra(KEY_MOVIE_DETAIL, movieDetail);

        startActivity(movieDetailIntent);
    }

    //endregion

    //region Private Methods
    /**
     * Set the RecyclerView Adapter.
     */
    private void setMovieDbRecycler(){

        mMoviesRecyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        mMovieDbRecyclerAdapter = new MovieDbRecyclerAdapter(this);
        mMoviesRecyclerView.setAdapter(mMovieDbRecyclerAdapter);
    }

    /**
     * Set display properties for the movie poster grid.
     */
    private void setMoviePosterGridLayout(){

        // Automatically set the span count below based on column width, screen orientation,
        // and screen size to fit the images.
        int gridColumnWidth = getResources().
                getInteger(R.integer.movie_poster_grid_column_width_vertical_mode);
        GridLayoutManagerAutofit layoutManager
                = new GridLayoutManagerAutofit(this,gridColumnWidth);
        mMoviesRecyclerView.setLayoutManager(layoutManager);

    }

    /**
     * Populate the movie list.
     * @param sortOrder The sort order of the movies.
     */
    private void setMovieList(MovieSortOrder sortOrder){

        String sortParameter = MovieDbUtility.MovieSortOrderWrapper.getString(sortOrder);

        FetchMoviesParams fetchMoviesParams = new FetchMoviesParams();
        fetchMoviesParams.setSortOrder(sortParameter);

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(this,fetchMoviesParams);

        // Fetch the movies using an asynchronous task.
        fetchMoviesTask.execute(fetchMoviesParams);
    }

    /**
     * Show movie data.
    */
    private void showMainActivityView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mMoviesRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error message.
     */
    private void showErrorMessage() {
        mMoviesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    /**
     * Toggle the movie sort order between popular and top rated.
     * @param item The sort menu item.
     */
    private void setMovieSortOrder(MenuItem item){

        MovieSortOrder newSortOrder;
        String newMenuTitle;

        if (item.getTitle() == mMenuTitlePopular) {
            newMenuTitle = mMenuTitleTopRated;
            newSortOrder = MovieSortOrder.MOVIE_SORT_TOP_RATED;
        } else {
            newMenuTitle = mMenuTitlePopular;
            newSortOrder = MovieSortOrder.MOVIE_SORT_POPULAR;
        }

        // If the sort order changed...
        if (newSortOrder != mMovieSortOrder){
            item.setTitle(newMenuTitle);

            // Update the stored movie sort order.
            mMovieSortOrder = newSortOrder;

            // Display a toast to indicate the sort order change.
            if (mSortOrderToast != null){
                mSortOrderToast.cancel();
            }
            String prefix = getResources().getString(R.string.sort_order_toast_prefix);
            String toastText = String.format(prefix,newMenuTitle);
            mSortOrderToast = Toast.makeText(getApplicationContext(),toastText,Toast.LENGTH_LONG);
            mSortOrderToast.setGravity(Gravity.TOP, 0 ,sSortOrderToastTopPosition);
            mSortOrderToast.show();

            // Update the movie list according to the new sort order.
            setMovieList(mMovieSortOrder);
        }
    }

    /*
    @Deprecated
    private void setSortSpinner(MenuItem sortMenuItem) {

        Spinner spinner = (Spinner) findViewById(R.id.sort_spinner);
        if (spinner != null) {

            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    this, R.array.sort_spinner_options, android.R.layout.simple_spinner_item);

            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            spinner.setAdapter(adapter);
        }
    }
    */

    //endregion Private methods

    //region Override Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region Set members

        mMenuTitlePopular = this.getResources().getString(R.string.sort_array_item_popular);
        mMenuTitleTopRated = this.getResources().getString(R.string.sort_array_item_top_rated);

        String movieSortOrder = getSharedPreference(sKeySortOrder);
        if (movieSortOrder != null && !movieSortOrder.isEmpty()){
            mMovieSortOrder = MovieDbUtility.MovieSortOrderWrapper.getEnumerator(movieSortOrder);
        } else{
            mMovieSortOrder = MovieDbUtility.MovieSortOrderWrapper.getDefault();
        }

        /* Note: savedInstanceState was always null so replaced with SharedPreferences above.
        if (savedInstanceState != null
            && savedInstanceState.containsKey(sKeySortOrder)) {
            // Restore the saved sort order.
            String savedSortOrder = savedInstanceState.getString(sKeySortOrder);
            mMovieSortOrder = MovieDbUtility.MovieSortOrderWrapper.getEnumerator(savedSortOrder);

            Log.d(LOG_TAG,String.format("onCreate savedInstanceSortOrder = %1s", savedSortOrder));
        }
        else {
            // Set to the default sort order.
            mMovieSortOrder = MovieSortOrder.MOVIE_SORT_DEFAULT;
        }*/

        mErrorMessageTextView = (TextView) findViewById(R.id.tv_error_message_display);

        mFetchDataProgressBar = (ProgressBar) findViewById(R.id.pb_fetch_data);

        // Populate the movie list.
        setMovieList(mMovieSortOrder);

        setMovieDbRecycler();

        setMoviePosterGridLayout();

        //endregion Set members
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        //MenuItem sortMenu = menu.findItem(R.string.action_sort);

        // Set the sort spinner options.
        // deprecated setSortSpinner(sortMenu);

        /* May do something with the action bar here.
        ActionBar actionBar = this.getActionBar();
        */

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                return true;

            case R.id.action_sort:
                setMovieSortOrder(item);
                return true;

            default:
                // If none of the above, call the superclass.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem sortItem = menu.findItem(R.id.action_sort);

        String menuTitle;
        switch (mMovieSortOrder){
            case MOVIE_SORT_POPULAR: menuTitle = mMenuTitlePopular;
                break;
            case MOVIE_SORT_TOP_RATED: menuTitle = mMenuTitleTopRated;
                break;
            default: menuTitle = mMenuTitlePopular;
        }
        sortItem.setTitle(menuTitle);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String saveSortOrder = MovieDbUtility.MovieSortOrderWrapper.getString(mMovieSortOrder);

        // Note: savedInstanceState was always null in OnCreate() so using SharedPreferences.
        setSharedPreference(sKeySortOrder,saveSortOrder);
        // Obsolete
        //outState.putString(sKeySortOrder,saveSortOrder);
    }

    private void setSharedPreference(String preferenceKey, String preferenceValue){
        SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceKey, preferenceValue);
        editor.apply(); // Save the preference in the background.
    }

    private String getSharedPreference(String preferenceKey){
        SharedPreferences preference = this.getPreferences(Context.MODE_PRIVATE);
        return preference.getString(preferenceKey,null);
    }

    // endregion

    //region Asynchronous Task
    /**
     * <p>The three types used by an asynchronous task are the following:</p>
     * <ol>
     *     <li><code>Params</code>, the type of the parameters sent to the task upon
     *     execution.</li>
     *     <li><code>Progress</code>, the type of the progress units published during
     *     the background computation.</li>
     *     <li><code>Result</code>, the type of the result of the background
     *     computation.</li>
     * References:
     *  AsyncTask documentation
     */
    private class FetchMoviesTask extends AsyncTask<FetchMoviesParams, Void, List<MovieParcelable>> {

        final Context mContext;
        final FetchMoviesParams mParams;

        // Constructor Overload
        private FetchMoviesTask(Context context,FetchMoviesParams fetchMoviesParams){
            mContext = context;
            mParams = fetchMoviesParams;
        }

        //region Override methods
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mFetchDataProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<MovieParcelable> doInBackground(FetchMoviesParams... params) {

            // Set the query URL according to the requested sort order.
            URL queryUrl;

            queryUrl = MovieDbUtility.getURL(
                    MovieDbUtility.MovieSortOrderWrapper.getEnumerator(mParams.getSortOrder()));

            String queryResult = null;
            try {
                queryResult = MovieDbUtility.getResponseFromHttpUrl(queryUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                mMovieList = MovieDbJsonUtility.getMovieDbStringsFromJson(queryResult);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mMovieList;
        }

        @Override
        protected void onPostExecute(List<MovieParcelable> movieParcelableList) {

            mFetchDataProgressBar.setVisibility(View.INVISIBLE);

            if (movieParcelableList != null && ! movieParcelableList.isEmpty()) {
                showMainActivityView();
                mMovieDbRecyclerAdapter.setMovieData(movieParcelableList);
            }
            else {
                mErrorMessageTextView.
                        setText(getResources().getText(R.string.error_message_no_query_results));
                showErrorMessage();
            }
        }
        //endregion Override methods

     }

    /**
     * Parses the params type passed to the asynctask.
     * Each parameter is accessed through a public get method.
     * Note:; Only the sort order parameter is currently supported.
     */
    private class FetchMoviesParams {

        private String sortOrder;

        private String getSortOrder() {
            return sortOrder;
        }

        private void setSortOrder(String sortOrderValue) {
            sortOrder = sortOrderValue;
        }

        // Constructor
        private FetchMoviesParams(){
        }
    }
     //endregion Asynchronous Task
}
