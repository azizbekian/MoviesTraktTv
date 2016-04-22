package com.azizbekian.movies.fragment.movies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.azizbekian.movies.MoviesApplication;
import com.azizbekian.movies.adapter.MovieAdapter;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.manager.SearchHelper;
import com.azizbekian.movies.misc.MoviesModeType;
import com.azizbekian.movies.misc.SearchModeType;
import com.azizbekian.movies.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by CargoMatrix, Inc. on April 21, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public class MoviesPresenter implements MoviesContract.Presenter {

    private MoviesContract.View mView;
    private MoviesContract.Model mModel;
    private MovieAdapter mMovieAdapter;
    private List<SearchItem.Movie> mMovies = new ArrayList<>();
    private SearchHelper mSearchHelper;

    private int mMode = MOVIES_IDLE;
    private int mMoviePageCounter = 1;

    public MoviesPresenter(MoviesFragment fragment) {
        mView = fragment;
        mModel = new MovieModel(this);
        mSearchHelper = new SearchHelper(this);
    }

    @Override
    public void start() {
        if (null != mView) {
            mView.setupToolbar();
            mView.setupMainProgressBar();
            mView.setupMainRecycler();
        }

        loadMovies();
    }

    @Override
    public void loadMovies() {

        if (isMoviesModeIdle()) {
            final boolean isAdapterEmpty = isAdapterEmpty();
            if (!isAdapterEmpty) setMoviesMode(MoviesContract.Presenter.MOVIES_FETCHING);

            if (isNetworkAvailable()) {
                setMoviesMode(isAdapterEmpty ? MoviesContract.Presenter.MOVIES_IDLE : MoviesContract.Presenter.MOVIES_FETCHING);

                if (null != mView) {
                    if (isAdapterEmpty) mView.showMainProgressBar(true);

                    mView.dispatchUnsubscribe(MoviesContract.Model.KEY_MOVIES);
                    mView.dispatchAddSubscription(MoviesContract.Model.KEY_MOVIES, mModel.loadMovies(getMoviePageCounter()));
                }
            } else {
                if (null != mView) mView.showSnackbar(true);
            }
        }
    }

    @Override
    public void onMoviesLoaded(List<SearchItem.Movie> movies) {
        setMoviesMode(MoviesContract.Presenter.MOVIES_IDLE);
        if (isAdapterEmpty() && null != mView) mView.showMainProgressBar(false);

        incrementMoviePageCounter();
        addMovies(movies);
    }

    @Override
    public int getMoviePageCounter() {
        return mMoviePageCounter;
    }

    @Override
    public void notifyMovieAdapterChanged() {
        mMovieAdapter.notifyDataSetChanged();
        if (null != mView) mView.showMainEmptyView(isAdapterEmpty());
    }

    @Override
    public void addMovies(List<SearchItem.Movie> movies) {
        mMovies.addAll(movies);
        notifyMovieAdapterChanged();
    }

    @Override
    public int incrementMoviePageCounter() {
        return ++mMoviePageCounter;
    }

    @Override
    public void onErrorLoadingMovieData() {
        setMoviesMode(MoviesContract.Presenter.MOVIES_IDLE);
        if (null != mView) mView.showSnackbar(false);
    }

    @Override
    public boolean isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailable(MoviesApplication.getAppContext());
    }

    @Override
    public boolean isMoviesModeIdle() {
        return mMode == MOVIES_IDLE;
    }

    @Override
    public void setMoviesMode(@MoviesModeType int mode) {
        mMode = mode;
    }

    @Override
    public void cancelSearch() {
        mSearchHelper.cancelSearch();
    }

    @Override
    public void revealSearchLayout(boolean show) {
        if (null != mView) {
            if (!mSearchHelper.isInflated()) mSearchHelper.inflate();
            mView.animateSearchView(show);
        }
    }

    @Override
    public RecyclerView.Adapter createAdapter() {
        return mMovieAdapter = new MovieAdapter(this, mMovies);
    }

    @Override
    public boolean isAdapterEmpty() {
        return mMovieAdapter.isEmpty();
    }

    @Override
    public void onNetworkStateChanged() {
        if ((getMoviePageCounter() == 1
                || (null != mView && mView.isBottomReachedAndLoading()))
                && isNetworkAvailable()) {
            mView.dismissSnackBar();
            setMoviesMode(MoviesContract.Presenter.MOVIES_IDLE);
            loadMovies();
        }
    }

    @Override
    public boolean onSearchMagnifierClicked(SearchView searchView) {
        if (null != mView) {
            mView.dispatchAddSubscription(
                    MoviesContract.Model.KEY_SEARCH,
                    mModel.performInitialSearch(searchView));
        }
        return true;
    }

    @Override
    public void setSearchMode(@SearchModeType int mode) {
        mSearchHelper.setMode(mode);
    }

    @Override
    public void clearSearchPageCounter() {
        mSearchHelper.clearSearchPageCounter();
    }

    @Override
    public int incrementSearchPageCounter() {
        return mSearchHelper.incrementSearchPageCounter();
    }

    @Override
    public int getSearchPageCounter() {
        return mSearchHelper.getPageCounter();
    }

    @Override
    public void onSearchDataReceived(List<SearchItem> searchList) {
        if(isSearchAdapterEmpty()) mSearchHelper.setSearchList(searchList);
        else mSearchHelper.addSearchResult(searchList);
    }

    @Override
    public void toggleSearchProgressBar(boolean show) {
        if (null != mView) mView.showSearchProgressBar(show);
    }

    @Override
    public MoviesContract.View getView() {
        return mView;
    }

    @Override
    public void finish() {
        cancelSearch();
        mSearchHelper = null;
    }

    @Override
    public RecyclerView.Adapter getSearchAdapter() {
        return mSearchHelper.getSearchAdapter();
    }

    @Override
    public void setupSearchLayout() {
        if (null != mView) mView.setupSearchLayout();
    }

    @Override
    public void loadSearchData() {
        mSearchHelper.setCurrentSearchCall(mModel.performAdditionalSearch());
    }

    @Override
    public int getSearchMode() {
        return mSearchHelper.getMode();
    }

    @Override
    public void toggleSearchEmptyView() {
        if (null != mView) {
            if (!mSearchHelper.isAdapterEmpty()) {
                mView.showSearchEmptyView(false);
            } else mView.showSearchEmptyView(true);
        }
    }

    @Override
    public void resetSearchBottomReachedListener() {
        if (null != mView) mView.resetSearchBottomReachedListener();
    }

    @Override
    public void resetSearchData() {
        mSearchHelper.resetData();
    }

    @Override
    public void resetSearch() {
        setSearchMode(isSearchAdapterEmpty() ? MoviesContract.Presenter.SEARCH_IDLE : MoviesContract.Presenter.SEARCH_FETCHING);
        clearSearchPageCounter();
        toggleSearchProgressBar(true);
    }


    @Override
    public boolean isSearchAdapterEmpty() {
        return mSearchHelper.isAdapterEmpty();
    }

    @Override
    public String getQuery() {
        return null != mView ? mView.getQuery() : "";
    }

    @Override
    public void showSnackBar(boolean noConnection) {
        if (null != mView) mView.showSnackbar(noConnection);
    }

    @Override
    public void launchDetailMovieActivity(ImageView transitionImageView, SearchItem.Movie movie) {
        if (null != mView) mView.launchDetailMovieActivity(transitionImageView, movie);
    }

    @Override
    public void openUri(String uri) {
        if (TextUtils.isEmpty(uri)) return;
        if (null != mView) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(uri));
            mView.launchActivity(i);
        }
    }

    @Override
    public boolean isSearchModeIdle() {
        return mSearchHelper.getMode() == MoviesContract.Presenter.SEARCH_IDLE;
    }

    @Override
    public void onSearchError() {
        setSearchMode(SEARCH_IDLE);
    }

    @Override
    public void onPerformSearchCall(Call<List<SearchItem>> call) {
        call.enqueue(new Callback<List<SearchItem>>() {
            @Override
            public void onResponse(Call<List<SearchItem>> call, Response<List<SearchItem>> response) {
                setSearchMode(SEARCH_IDLE);
                toggleSearchProgressBar(false);
                List<SearchItem> searchItems = response.body();
                if (searchItems == null) {
                    onSearchDataReceived(Collections.emptyList());
                } else {
                    incrementSearchPageCounter();
                    onSearchDataReceived(searchItems);
                }
            }

            @Override
            public void onFailure(Call<List<SearchItem>> call, Throwable t) {
                setSearchMode(SEARCH_IDLE);
                if (isSearchAdapterEmpty()) toggleSearchProgressBar(false);
                onSearchDataReceived(Collections.emptyList());
            }
        });
    }
}
