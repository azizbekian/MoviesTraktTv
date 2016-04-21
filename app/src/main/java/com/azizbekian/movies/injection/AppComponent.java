package com.azizbekian.movies.injection;

import com.azizbekian.movies.rest.TraktTvApi;
import com.azizbekian.movies.fragment.DetailMovieFragment;
import com.azizbekian.movies.fragment.movies.MoviesFragment;
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

    void inject(MoviesFragment fragment);
    void inject(DetailMovieFragment fragment);

    Picasso getPicasso();
    TraktTvApi.Search getSearchApi();
}
