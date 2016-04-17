package com.azizbekian.example.injection;

import android.content.Context;

import com.azizbekian.example.BuildConfig;
import com.azizbekian.example.entity.SearchItem;
import com.azizbekian.example.misc.Constants;
import com.azizbekian.example.misc.DefaultExclusionStrategy;
import com.azizbekian.example.misc.SearchExclusionStrategy;
import com.azizbekian.example.rest.TraktTvApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
@Module
public class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    @Singleton
    public Picasso providesPicasso() {
        return Picasso.with(context);
    }

    @Provides
    @Singleton
    public TraktTvApi.Default providesDefaultTraktTvApi() {

        List<Class> classesToRemain = new ArrayList<Class>() {{
            add(List.class);
            add(SearchItem.Movie.Ids.class);
            add(String.class);
            add(SearchItem.Movie.class);
            add(SearchItem.Movie.Images.class);
            add(SearchItem.Movie.Images.Poster.class);
        }};

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setExclusionStrategies(new DefaultExclusionStrategy(classesToRemain))
                .create();

        OkHttpClient client = createOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        return retrofit.create(TraktTvApi.Default.class);
    }

    @Provides
    @Singleton
    public TraktTvApi.Search providesSearchTraktTvApi() {

        List<Class> classesToRemain = new ArrayList<Class>() {{
            add(List.class);
            add(SearchItem.class);
            add(String.class);
            add(SearchItem.Movie.class);
            add(SearchItem.Movie.Images.class);
            add(SearchItem.Movie.Images.Poster.class);
            add(SearchItem.Movie.Ids.class);
        }};

        Gson gson = new GsonBuilder()
                .serializeNulls()
                .setExclusionStrategies(new SearchExclusionStrategy(classesToRemain))
                .create();

        OkHttpClient client = createOkHttpClient();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.ENDPOINT_API)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(TraktTvApi.Search.class);
    }


    private OkHttpClient createOkHttpClient() {
        OkHttpClient client;

        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(chain -> {
            Request request = chain.request();
            Request newRequest;
            newRequest = request.newBuilder().addHeader(TraktTvApi.KEY_TRAKT_API_KEY,
                    Constants.TRAKT_API_KEY).build();

            return chain.proceed(newRequest);
        });

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            client = builder.addInterceptor(interceptor).build();
        } else client = builder.build();

        return client;
    }
}
