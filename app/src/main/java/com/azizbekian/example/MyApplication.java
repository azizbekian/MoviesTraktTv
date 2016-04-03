package com.azizbekian.example;

import android.app.Application;

import com.azizbekian.example.injection.AppComponent;
import com.azizbekian.example.injection.AppModule;

import com.azizbekian.example.injection.DaggerAppComponent;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MyApplication extends Application {

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
