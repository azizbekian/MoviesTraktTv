package com.azizbekian.movies.rest;

import android.support.annotation.StringDef;

import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.misc.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import rx.Observable;

/**
 * We have to use different {@link retrofit2.Retrofit} interfaces, because they use different
 * {@link com.google.gson.ExclusionStrategy} and different {@link retrofit2.Retrofit}
 * {@link retrofit2.CallAdapter}s.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public interface TraktTvApi {

    String KEY_TRAKT_API_KEY = "trakt-api-key";

    interface Default {

        /**
         * Fetches top popular movies from trakt.tv API.
         *
         * @param page       the number of page that needs to be brought
         * @param limit      how many items does each page contain
         * @param extendType different type of responses are available, which provide from basic to
         *                   full info about each movie.
         */
        @Headers({"Content-type: application/json",
                "trakt-api-version: 2"})
        @GET("/movies/popular")
        Observable<List<SearchItem.Movie>> getPopularMovies(@Query("page") int page,
                                                            @Query("limit") int limit,
                                                            @Query("extended") @ExtendType String extendType);
    }

    interface Search {

        /**
         * Fetches search result by specified query from trakt.tv API.
         *
         * @param query the query to perform a search
         * @param type  in what kind of data to search, e.g.: movies, serials, tv shows.
         * @param page  the number of page that needs to be brought
         */
        @Headers({"Content-type: application/json",
                "trakt-api-version: 2"})
        @GET("/search?")
        Call<List<SearchItem>> searchMovies(@Query("query") String query,
                                            @Query("type") @SearchType String type,
                                            @Query("page") int page);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Constants.EXTEND_TYPE_FULL_IMAGES
    })
    @interface ExtendType {
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({
            Constants.TYPE_MOVIE
    })
    @interface SearchType {
    }

}
