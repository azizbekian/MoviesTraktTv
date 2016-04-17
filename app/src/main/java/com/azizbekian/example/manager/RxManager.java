package com.azizbekian.example.manager;

import com.azizbekian.example.entity.SearchItem;
import com.azizbekian.example.rest.TraktTvApi;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.azizbekian.example.misc.Constants.DEFAULT_MOVIE_LIMIT;
import static com.azizbekian.example.misc.Constants.EXTEND_TYPE_FULL_IMAGES;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class RxManager {

    public static Observable<List<SearchItem.Movie>> getPopularMovies(
            TraktTvApi.Default traktTvDefaultApi, int pageCounter) {
        return traktTvDefaultApi.getPopularMovies(pageCounter, DEFAULT_MOVIE_LIMIT,
                EXTEND_TYPE_FULL_IMAGES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
