package com.azizbekian.movies.fragment.movies;

import android.support.v7.widget.SearchView;

import com.azizbekian.movies.MoviesApplication;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.rest.TraktTvApi;
import com.azizbekian.movies.utils.RxUtils;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.List;

import retrofit2.Call;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

import static com.azizbekian.movies.fragment.movies.MoviesContract.Presenter.SEARCH_IDLE;
import static com.azizbekian.movies.misc.Constants.DEFAULT_MOVIE_LIMIT;
import static com.azizbekian.movies.misc.Constants.EXTEND_TYPE_FULL_IMAGES;
import static com.azizbekian.movies.misc.Constants.TYPE_MOVIE;

/**
 * Created by CargoMatrix, Inc. on April 21, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public class MovieModel implements MoviesContract.Model {

    private MoviesContract.Presenter mPresenter;
    private TraktTvApi.Default mTraktTvDefaultApi;
    private TraktTvApi.Search mTraktTvSearchApi;

    public MovieModel(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
        mTraktTvDefaultApi = MoviesApplication.getAppComponent().getDefaultApi();
        mTraktTvSearchApi = MoviesApplication.getAppComponent().getSearchApi();
    }

    @Override
    public Subscription loadMovies(int pageCounter) {
        return mTraktTvDefaultApi
                .getPopularMovies(pageCounter, DEFAULT_MOVIE_LIMIT, EXTEND_TYPE_FULL_IMAGES)
                .compose(RxUtils.applyIOtoMainThreadSchedulers())
                .subscribe(new Subscriber<List<SearchItem.Movie>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPresenter.onErrorLoadingMovieData();
                    }

                    @Override
                    public void onNext(List<SearchItem.Movie> movies) {
                        mPresenter.onMoviesLoaded(movies);
                    }
                });
    }

    @Override
    public Subscription performInitialSearch(SearchView searchView) {
        return RxSearchView
                .queryTextChanges(searchView)
                .compose(RxUtils.applyNotEmptyTextChangeTransformer())
                .map(charSequence -> {
                    mPresenter.resetSearch();
                    return mTraktTvSearchApi.searchMovies(charSequence.toString(), TYPE_MOVIE, 1);
                })
                .subscribe(new Observer<Call<List<SearchItem>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        mPresenter.onSearchError();
                    }

                    @Override
                    public void onNext(Call<List<SearchItem>> listCall) {
                        mPresenter.onPerformSearchCall(listCall);
                    }
                });
    }

    @Override
    public Call<List<SearchItem>> performAdditionalSearch() {
        Call<List<SearchItem>> searchCall = null;

        if (mPresenter.getSearchMode() == SEARCH_IDLE) {
            final boolean isAdapterEmpty = mPresenter.isSearchAdapterEmpty();
            if (!isAdapterEmpty) mPresenter.setSearchMode(MoviesContract.Presenter.SEARCH_FETCHING);

            if (mPresenter.isNetworkAvailable()) {
                mPresenter.setSearchMode(isAdapterEmpty ? SEARCH_IDLE : MoviesContract.Presenter.SEARCH_FETCHING);
                if (isAdapterEmpty) mPresenter.toggleSearchProgressBar(true);
                String query = mPresenter.getQuery();
                mPresenter.cancelSearch();
                searchCall = mTraktTvSearchApi.searchMovies(query, TYPE_MOVIE, mPresenter.getSearchPageCounter());
                mPresenter.onPerformSearchCall(searchCall);
            } else {
                mPresenter.showSnackBar(true);
            }
        }
        return searchCall;
    }

}
