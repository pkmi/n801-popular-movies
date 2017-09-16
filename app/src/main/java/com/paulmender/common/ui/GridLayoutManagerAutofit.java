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

package com.paulmender.common.ui;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;

/**
 * Custom grid layout manager that calculates the span value based on the columnWidth, screen size
 * and thus fills the entire screen with the grid.
 * Created by Paul on 9/11/2017.
 * References:
 *  Diep Nguyen, https://codentrick.com/part-4-android-recyclerview-grid/
 */
public class GridLayoutManagerAutofit extends GridLayoutManager {
    private int mColumnWidth;
    private boolean mColumnWidthChanged = true;

    private final String LOG_TAG = "pkmi debug: "+ GridLayoutManager.class.getSimpleName();

    // Constructor
    public GridLayoutManagerAutofit(Context context, int columnWidth) {
        /* Initially set spanCount to 1, will be changed automatically later. */
        super(context, 1);

        setColumnWidth(getColumnWidth(context, columnWidth));
    }

    // Constructor
    public GridLayoutManagerAutofit(
            Context context, int columnWidth, int orientation, boolean reverseLayout){
        /* Initially set spanCount to 1, will be changed automatically later. */
        super(context, columnWidth, orientation, reverseLayout);
        setColumnWidth(getColumnWidth(context, columnWidth));
    }
    private int getColumnWidth(Context context, int columnWidth) {
        if (columnWidth <= 0) {
            /* Set default columnWidth value (48dp here). */
            int defaultColumnWidth = 48;
            columnWidth = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, defaultColumnWidth,
                    context.getResources().getDisplayMetrics());
        }
        return columnWidth;
    }

    private void setColumnWidth(int newColumnWidth) {
        if (newColumnWidth > 0 && newColumnWidth != mColumnWidth) {
            mColumnWidth = newColumnWidth;
            mColumnWidthChanged = true;
        }
    }

    /**
     * Set the span value according to the calculated column width and screen orientation.
     * @param recycler The RecycleView Recycler
     * @param state The RecyclerView State
     */
    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {

        if (mColumnWidthChanged && mColumnWidth > 0) {
            int totalSpace;
            if (getOrientation() == VERTICAL) {
                totalSpace = getWidth() - getPaddingRight() - getPaddingLeft();
            } else {
                totalSpace = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            int spanCount = Math.max(1, totalSpace / mColumnWidth);
            setSpanCount(spanCount);

            mColumnWidthChanged = false;

            // TODO Remove span count debug during stage 2 completion
            //String s = String.format("%1$d divided by %2$d",totalSpace,mColumnWidth);
            //Log.d(LOG_TAG, s);
            //Log.d(LOG_TAG,"spanCount = "+spanCount);
        }
        super.onLayoutChildren(recycler, state);
    }
}
