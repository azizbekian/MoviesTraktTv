package com.azizbekian.movies;

import android.app.Application;
import android.content.Context;

import com.azizbekian.movies.injection.AppComponent;
import com.azizbekian.movies.injection.AppModule;

import com.azizbekian.movies.injection.DaggerAppComponent;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MoviesApplication extends Application {

    private static AppComponent sAppComponent;
    private static Context sAppContext;

    @Override
    public void onCreate() {
        super.onCreate();

//        LeakCanary.install(this);
        sAppContext = getApplicationContext();
        sAppComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }

    public static Context getAppContext() {
        return sAppContext;
    }

}
