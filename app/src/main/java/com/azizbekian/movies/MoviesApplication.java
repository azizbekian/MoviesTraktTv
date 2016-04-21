package com.azizbekian.movies;

import android.app.Application;

import com.azizbekian.movies.injection.AppComponent;
import com.azizbekian.movies.injection.AppModule;

import com.azizbekian.movies.injection.DaggerAppComponent;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MoviesApplication extends Application {

    private static AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

//        LeakCanary.install(this);
        mAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent getAppComponent() {
        return mAppComponent;
    }

}
