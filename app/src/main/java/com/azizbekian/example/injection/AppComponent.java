package com.azizbekian.example.injection;

import com.azizbekian.example.rest.TraktTvApi;
import com.azizbekian.example.ui.fragment.DetailMovieFragment;
import com.azizbekian.example.ui.fragment.MainFragment;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
@Singleton
@Component(modules  = {AppModule.class})
public interface AppComponent {

    void inject(MainFragment fragment);
    void inject(DetailMovieFragment fragment);

    Picasso getPicasso();
    TraktTvApi.Search getSearchApi();
}
