package com.azizbekian.movies.fragment.movies;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.widget.ImageView;

import com.azizbekian.movies.activity.DetailMovieActivity;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.misc.MoviesModeType;
import com.azizbekian.movies.misc.SearchModeType;

import java.util.List;

import retrofit2.Call;
import rx.Subscription;

/**
 * Created by CargoMatrix, Inc. on April 21, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public interface MoviesContract {

    interface View {

        void setupToolbar();

        void setupMainProgressBar();

        void setupMainRecycler();

        /**
         * Control's {@code progressBar}'s visibility state.
         *
         * @param show if true, sets {@code mMainProgressBar}'s visibility to {@code View.VISIBLE},
         *             else - animates to {@code View.GONE}.
         */
        void showMainProgressBar(boolean show);

        /**
         * Show {@link Snackbar} in exceptional conditions.
         *
         * @param noInternet indicates, whether this {@code mSnackbar} is being show because of no
         *                   internet connection. If true - no internet text would be show.
         *                   Else - general error message.
         */
        void showSnackbar(boolean noInternet);

        void setupSearchLayout();

        /**
         * Shows or hides empty view, depending on {@code mMovieAdapter}'s size.
         */
        void showMainEmptyView(boolean show);

        void dispatchUnsubscribe(String key);

        void dispatchAddSubscription(String key, Subscription subscription);

        boolean isBottomReachedAndLoading();

        void showSearchEmptyView(boolean show);

        void resetSearchBottomReachedListener();

        void showSearchProgressBar(boolean show);

        void animateSearchView(boolean show);

        String getQuery();

        /**
         * Launches {@link DetailMovieActivity}, providing some more information to perform animation transition.
         */
        void launchDetailMovieActivity(ImageView transitionImageView, SearchItem.Movie movie);

        void launchActivity(Intent intent);

        void dismissSnackBar();
    }

    interface Presenter {

        int MOVIES_IDLE = 0;
        int MOVIES_FETCHING = 1;
        int SEARCH_IDLE = 2;
        int SEARCH_FETCHING = 3;

        void start();

        RecyclerView.Adapter createAdapter();

        RecyclerView.Adapter getSearchAdapter();

        boolean isAdapterEmpty();

        void finish();

        void loadMovies();

        boolean isMoviesModeIdle();

        boolean isSearchModeIdle();

        void setMoviesMode(@MoviesModeType int mode);

        void setSearchMode(@SearchModeType int mode);

        int getSearchMode();

        boolean isNetworkAvailable();

        void onErrorLoadingMovieData();

        void onMoviesLoaded(List<SearchItem.Movie> movies);

        int incrementMoviePageCounter();

        int getMoviePageCounter();

        void clearSearchPageCounter();

        int incrementSearchPageCounter();

        int getSearchPageCounter();

        void onSearchDataReceived(List<SearchItem> searchList);

        void toggleSearchProgressBar(boolean show);

        void addMovies(List<SearchItem.Movie> movies);

        void notifyMovieAdapterChanged();

        void onNetworkStateChanged();

        boolean onSearchMagnifierClicked(SearchView searchView);

        void cancelSearch();

        void revealSearchLayout(boolean show);

        View getView();

        void setupSearchLayout();

        void loadSearchData();

        void toggleSearchEmptyView();

        void resetSearchBottomReachedListener();

        void resetSearchData();

        boolean isSearchAdapterEmpty();

        String getQuery();

        void showSnackBar(boolean noConnection);

        void launchDetailMovieActivity(ImageView transitionImageView, SearchItem.Movie movie);

        void openUri(String uri);

        void resetSearch();

        void onSearchError();

        void onPerformSearchCall(Call<List<SearchItem>> call);
    }

    interface Model {
        String KEY_SEARCH = "key_search";
        String KEY_MOVIES = "key_movies";

        Subscription loadMovies(int pageCounter);

        Subscription performInitialSearch(SearchView searchView);

        Call<List<SearchItem>> performAdditionalSearch();
    }

}
