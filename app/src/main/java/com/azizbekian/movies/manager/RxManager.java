package com.azizbekian.movies.manager;

import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.rest.TraktTvApi;
import com.azizbekian.movies.utils.RxUtils;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.azizbekian.movies.misc.Constants.DEFAULT_MOVIE_LIMIT;
import static com.azizbekian.movies.misc.Constants.EXTEND_TYPE_FULL_IMAGES;

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
                .compose(RxUtils.applyIOtoMainThreadSchedulers());
    }
}
