package com.azizbekian.movies.fragment.movies;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.widget.ImageView;

import com.azizbekian.movies.MoviesApplication;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.manager.MovieHelper;
import com.azizbekian.movies.manager.SearchHelper;
import com.azizbekian.movies.misc.MoviesModeType;
import com.azizbekian.movies.misc.SearchModeType;
import com.azizbekian.movies.utils.NetworkUtils;

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
    private MovieHelper mMovieHelper;
    private SearchHelper mSearchHelper;

    public MoviesPresenter(MoviesFragment fragment) {
        mView = fragment;
        mModel = new MovieModel(this);
        mMovieHelper = new MovieHelper(this);
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
    public void finish() {
        cancelSearch();
        mMovieHelper = null;
        mSearchHelper = null;
    }

    // ---- START MAIN ----

    @Override
    public int getMoviePageCounter() {
        return mMovieHelper.getPageCounter();
    }

    @Override
    public void notifyMovieAdapterChanged() {
        mMovieHelper.notifyDataSetChanged();
        if (null != mView) mView.showMainEmptyView(isMovieAdapterEmpty());
    }

    @Override
    public void addMovies(List<SearchItem.Movie> movies) {
        mMovieHelper.addMovies(movies);
        notifyMovieAdapterChanged();
    }

    @Override
    public void onMoviesLoaded(List<SearchItem.Movie> movies) {
        setMoviesMode(MoviesContract.Presenter.MOVIES_IDLE);
        if (isMovieAdapterEmpty() && null != mView) mView.showMainProgressBar(false);

        incrementMoviePageCounter();
        addMovies(movies);
    }

    @Override
    public int incrementMoviePageCounter() {
        return mMovieHelper.incrementPage();
    }

    @Override
    public void onErrorLoadingMovieData() {
        setMoviesMode(MOVIES_FETCHING);
        if (null != mView) mView.showSnackbar(false);
    }

    @Override
    public boolean isMoviesModeIdle() {
        return mMovieHelper.isIdle();
    }

    @Override
    public void setMoviesMode(@MoviesModeType int mode) {
        mMovieHelper.setMode(mode);
    }

    @Override
    public void loadMovies() {
        if (!isNetworkAvailable()) {
            if (!isMovieAdapterEmpty()) setMoviesMode(MOVIES_FETCHING);
            if (null != mView) mView.showSnackbar(true);
        } else if (isMoviesModeIdle()) {
            final boolean isAdapterEmpty = isMovieAdapterEmpty();
            if (!isAdapterEmpty) setMoviesMode(MoviesContract.Presenter.MOVIES_FETCHING);
            if (null != mView) {
                if (isAdapterEmpty) mView.showMainProgressBar(true);
                mView.dispatchUnsubscribe(MoviesContract.Model.KEY_MOVIES);
                mView.dispatchAddSubscription(MoviesContract.Model.KEY_MOVIES, mModel.loadMovies(getMoviePageCounter()));
            }
        }
    }

    @Override
    public RecyclerView.Adapter createMovieAdapter() {
        return mMovieHelper.getAdapter();
    }

    @Override
    public boolean isMovieAdapterEmpty() {
        return mMovieHelper.isAdapterEmpty();
    }

    // ---- END MAIN ----

    // ---- START SEARCH ----

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
    public boolean onSearchMagnifierClicked(SearchView searchView) {
        if (null != mView) {
            mView.dispatchAddSubscription(
                    MoviesContract.Model.KEY_SEARCH,
                    mModel.listenQueryChange(searchView));
        }
        return true;
    }

    @Override
    public void setSearchMode(@SearchModeType int mode) {
        mSearchHelper.setMode(mode);
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
    public void onSearchDataReceived(List<SearchItem> searchList, boolean append) {
        if (!isSearchAdapterEmpty() && append) {
            mSearchHelper.addSearchResult(searchList);
        } else mSearchHelper.setSearchList(searchList);
    }

    @Override
    public void toggleSearchProgressBar(boolean show) {
        if (null != mView) mView.showSearchProgressBar(show);
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
        mModel.performSearch(getQuery());
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
    public boolean isSearchAdapterEmpty() {
        return mSearchHelper.isAdapterEmpty();
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
        if (null != call && isSearchModeIdle()) {
            setSearchMode(isSearchAdapterEmpty() ? SEARCH_IDLE : MoviesContract.Presenter.SEARCH_FETCHING);
            if (isSearchAdapterEmpty()) toggleSearchProgressBar(true);

            cancelSearch();
            setSearchCall(call);

            call.enqueue(new Callback<List<SearchItem>>() {
                @Override
                public void onResponse(Call<List<SearchItem>> call, Response<List<SearchItem>> response) {
                    setSearchMode(SEARCH_IDLE);
                    toggleSearchProgressBar(false);
                    List<SearchItem> searchItems = response.body();
                    if (searchItems == null) {
                        onSearchDataReceived(Collections.emptyList(), false);
                    } else {
                        incrementSearchPageCounter();
                        onSearchDataReceived(searchItems, true);
                    }
                }

                @Override
                public void onFailure(Call<List<SearchItem>> call, Throwable t) {
                    setSearchMode(SEARCH_IDLE);
                    if (isSearchAdapterEmpty()) toggleSearchProgressBar(false);
                    onSearchDataReceived(Collections.emptyList(), false);
                }
            });
        }
    }

    @Override
    public void setSearchCall(Call<List<SearchItem>> call) {
        mSearchHelper.setCurrentSearchCall(call);
    }

    // ---- END SEARCH ----

    @Override
    public boolean isNetworkAvailable() {
        return NetworkUtils.isNetworkAvailable(MoviesApplication.getAppContext());
    }

    @Override
    public void onNetworkStateChanged() {
        if (null != mView) {
            if (isNetworkAvailable()) {
                if (mView.isBottomReachedAndLoading() || !isMoviesModeIdle()) {
                    mView.dismissSnackBar();
                    setMoviesMode(MoviesContract.Presenter.MOVIES_IDLE);
                    loadMovies();
                } else if (mView.isSearchBottomReachedAndLoading() || !isSearchModeIdle()) {
                    setSearchMode(SEARCH_IDLE);
                    loadSearchData();
                }
            }
        }
    }

    @Override
    public MoviesContract.View getView() {
        return mView;
    }

    @Override
    public String getQuery() {
        return null != mView ? mView.getQuery() : "";
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
    public void onQueryChanged(String newQuery) {
        resetSearchData();
        mModel.performSearch(newQuery);
    }
}
