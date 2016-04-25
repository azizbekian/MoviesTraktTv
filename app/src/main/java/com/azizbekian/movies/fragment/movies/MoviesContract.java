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

        /**
         * Sets up {@link android.support.v7.widget.Toolbar} in the hosting activity.
         */
        void setupToolbar();

        /**
         * Sets up {@link android.widget.ProgressBar} in hosting fragment.
         */
        void setupMainProgressBar();

        /**
         * Sets up the {@link RecyclerView}, where the list of top movies would be populated.
         */
        void setupMainRecycler();

        /**
         * Controls {@code progressBar}'s visibility state.
         *
         * @param show if true, sets {@code mMainProgressBar}'s visibility to {@code View.VISIBLE},
         *             else - animates to {@code View.GONE}.
         */
        void showMainProgressBar(boolean show);

        /**
         * Shows {@link Snackbar} in exceptional conditions.
         *
         * @param noInternet indicates, whether this {@code mSnackbar} is being show because of no
         *                   internet connection. If true - no internet text would be show.
         *                   Else - general error message.
         */
        void showSnackbar(boolean noInternet);

        /**
         * Inflates and sets up search layout.
         */
        void setupSearchLayout();

        /**
         * Shows or hides empty view, depending on {@code mMovieAdapter}'s size.
         */
        void showMainEmptyView(boolean show);

        /**
         * Unsubscribes from the {@link Subscription} with specified {@code key}.
         *
         * @param key
         */
        void dispatchUnsubscribe(String key);

        /**
         * Adds {@code subscription} with specified {@code key} in the
         * {@link android.support.v4.util.ArrayMap ArrayMap} in {@link com.azizbekian.movies.fragment.BaseFragment}.
         */
        void dispatchAddSubscription(String key, Subscription subscription);

        /**
         * @return True if user has scrolled to the bottom of
         * {@link RecyclerView} and the data hasn't been loaded yet.
         * False otherwise.
         */
        boolean isBottomReachedAndLoading();

        /**
         * @return True if user has scrolled to the bottom of
         * {@link RecyclerView} and the data hasn't been loaded yet.
         * False otherwise.
         */
        boolean isSearchBottomReachedAndLoading();

        /**
         * @param show If true shows empty view. Otherwise hides empty view.
         */
        void showSearchEmptyView(boolean show);

        /**
         * Resets listener.
         */
        void resetSearchBottomReachedListener();

        /**
         * @param show If true shows progress bar.
         */
        void showSearchProgressBar(boolean show);

        /**
         * @param show If true animates search layout in. Else hides search layout.
         */
        void animateSearchView(boolean show);

        /**
         * @return The text in the search view's {@link android.widget.EditText EditText}.
         */
        String getQuery();

        /**
         * Launches {@link DetailMovieActivity}, providing some more information to perform animation transition.
         */
        void launchDetailMovieActivity(ImageView transitionImageView, SearchItem.Movie movie);

        /**
         * Launches activity with specified {@code intent}.
         */
        void launchActivity(Intent intent);

        /**
         * Dismisses snackbar if some is being shown.
         */
        void dismissSnackBar();
    }

    interface Presenter {

        int MOVIES_IDLE = 0;
        int MOVIES_FETCHING = 1;
        int SEARCH_IDLE = 2;
        int SEARCH_FETCHING = 3;

        /**
         * Performs initial action.
         */
        void start();

        /**
         * @return The adapter for main top movies' list.
         */
        RecyclerView.Adapter createMovieAdapter();

        /**
         * @return The adapter for search list.
         */
        RecyclerView.Adapter getSearchAdapter();

        /**
         * @return True if movies' adapter is empty. False otherwise.
         */
        boolean isMovieAdapterEmpty();

        /**
         * Perform cleaning up.
         */
        void finish();

        /**
         * Loads movies from server.
         */
        void loadMovies();

        /**
         * @return True if {@link com.azizbekian.movies.manager.MovieHelper#mMode mode}
         * is {@link Presenter#MOVIES_IDLE}.
         */
        boolean isMoviesModeIdle();

        /**
         * @return True if {@link com.azizbekian.movies.manager.SearchHelper#mMode mode}
         * is {@link Presenter#SEARCH_IDLE}.
         */
        boolean isSearchModeIdle();

        /**
         * @param mode The mode to retain in {@link com.azizbekian.movies.manager.MovieHelper#mMode mode}.
         */
        void setMoviesMode(@MoviesModeType int mode);

        /**
         * @param mode The mode to retain in {@link com.azizbekian.movies.manager.SearchHelper#mMode mode}.
         */
        void setSearchMode(@SearchModeType int mode);

        /**
         * @return True if there is internet connection. False otherwise.
         */
        boolean isNetworkAvailable();

        /**
         * Handles case when error occured while loading data.
         */
        void onErrorLoadingMovieData();

        /**
         * Populates {@link RecyclerView} with {@code movies}.
         */
        void onMoviesLoaded(List<SearchItem.Movie> movies);

        /**
         * Increments movies' page counter.
         */
        int incrementMoviePageCounter();

        /**
         * @return Movies' page counter value.
         */
        int getMoviePageCounter();

        /**
         * Increments search page counter.
         */
        int incrementSearchPageCounter();

        /**
         * @return Search page counter value.
         */
        int getSearchPageCounter();

        /**
         * @param searchList The data to populate with the {@link RecyclerView}.
         * @param append     If true, adds {@code searchList} to current data set.
         *                   If false replaces old set with {@code searchList}.
         */
        void onSearchDataReceived(List<SearchItem> searchList, boolean append);

        /**
         * @param show If true shows progress bar.
         */
        void toggleSearchProgressBar(boolean show);

        /**
         * @param movies Adds {@code movies} to movies' list.
         */
        void addMovies(List<SearchItem.Movie> movies);

        /**
         * Notifies movie adapter.
         */
        void notifyMovieAdapterChanged();

        /**
         * Indicates, that a internet connectivity state has been changed.
         */
        void onNetworkStateChanged();

        /**
         * Handles actions to be performed when {@code searchView} has been clicked.
         */
        boolean onSearchMagnifierClicked(SearchView searchView);

        /**
         * Cancels current search {@link Call}.
         */
        void cancelSearch();

        /**
         * @param show If true animates search layout in with reveal animation.
         *             If false hides search layout with reveal animation.
         */
        void revealSearchLayout(boolean show);

        /**
         * @return The {@link View} associated with presenter.
         */
        View getView();

        /**
         * Sets up search layout.
         */
        void setupSearchLayout();

        /**
         * Load search data.
         */
        void loadSearchData();

        /**
         * Controls search's empty view visibility state.
         */
        void toggleSearchEmptyView();

        /**
         * Resets search bottom reached listener.
         */
        void resetSearchBottomReachedListener();

        /**
         * Resets search data.
         */
        void resetSearchData();

        /**
         * @return True if search adapter is empty. False otherwise.
         */
        boolean isSearchAdapterEmpty();

        /**
         * @return The text that is currently displayed in {@link SearchView}.
         */
        String getQuery();

        /**
         * Launches {@link DetailMovieActivity} providing more info to perform
         * transition animation.
         */
        void launchDetailMovieActivity(ImageView transitionImageView, SearchItem.Movie movie);

        /**
         * Opens specified {@code uri}.
         */
        void openUri(String uri);

        /**
         * Handles error raised during search call.
         */
        void onSearchError();

        /**
         * Performs search.
         */
        void onPerformSearchCall(Call<List<SearchItem>> call);

        /**
         * @param newQuery New query that is displayed in {@link SearchView}.
         */
        void onQueryChanged(String newQuery);

        /**
         * @param call The {@link Call} to retain in
         *             {@link com.azizbekian.movies.manager.SearchHelper SearchHelper}.
         */
        void setSearchCall(Call<List<SearchItem>> call);
    }

    interface Model {
        String KEY_SEARCH = "key_search";
        String KEY_MOVIES = "key_movies";

        /**
         * Loads movies.
         */
        Subscription loadMovies(int pageCounter);

        /**
         * Listens for text changes in {@code searchView}.
         */
        Subscription listenQueryChange(SearchView searchView);

        /**
         * Performs search with specified {@code query}.
         */
        Call<List<SearchItem>> performSearch(String query);
    }

}
