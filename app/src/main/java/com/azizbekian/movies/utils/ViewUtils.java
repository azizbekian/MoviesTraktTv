package com.azizbekian.movies.utils;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class ViewUtils {

    /**
     * Sets up toolbar correspondingly.
     *
     * @param activity     - hosting activity
     * @param toolbar      - toolbar
     * @param toolbarTitle - title to be displayed
     * @param showHomeAsUp - boolean, indicating whether back arrow should be displayed
     */
    public static void setupToolbar(AppCompatActivity activity, Toolbar toolbar, String toolbarTitle,
                                    boolean showHomeAsUp) {
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            if (null != toolbarTitle) actionBar.setTitle(toolbarTitle);
            if (showHomeAsUp)
                actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
