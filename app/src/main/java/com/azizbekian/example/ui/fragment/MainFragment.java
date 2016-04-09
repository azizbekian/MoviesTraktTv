package com.azizbekian.example.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.azizbekian.example.R;
import com.azizbekian.example.adapter.MovieAdapter;
import com.azizbekian.example.entity.ConnectivityChangeEvent;
import com.azizbekian.example.entity.SearchItem;
import com.azizbekian.example.listener.BottomReachedScrollListener;
import com.azizbekian.example.manager.RxManager;
import com.azizbekian.example.manager.SearchHelper;
import com.azizbekian.example.rest.TraktTvApi;
import com.azizbekian.example.utils.ConnectivityUtils;
import com.azizbekian.example.utils.ViewUtils;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.Subscriber;

import static android.graphics.PorterDuff.Mode.MULTIPLY;
import static com.azizbekian.example.MyApplication.getAppComponent;
import static com.azizbekian.example.misc.Constants.ANIM_DURATION_FADE;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MainFragment extends BaseFragment {

    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout coordinatorLayout;
    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.empty_view)
    View emptyView;
    @Bind(R.id.progress_indicator)
    ProgressBar progressBar;
    Snackbar snackbar;
    SearchView searchView;

    @Inject
    TraktTvApi.Default traktTvDefaultApi;

    private MovieAdapter mMovieAdapter;
    private List<SearchItem.Movie> mMovies;
    private SearchHelper mSearchHelper;

    private BottomReachedScrollListener mBottomReachedListener;

    private int mPageCounter = 1;

    private static final int IDLE = 0;
    private static final int FETCHING = 1;
    private static int sMode = IDLE;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getAppComponent().inject(this);

        setRetainInstance(true);
        setHasOptionsMenu(true);
        mMovies = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        if (null != mSearchHelper) mSearchHelper.cancelSearch();
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) mSearchItem.getActionView();
        MenuItemCompat.setOnActionExpandListener(mSearchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchHelper.animateSearchView(false);
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mSearchHelper.animateSearchView(true);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            if (null == mSearchHelper)
                mSearchHelper = new SearchHelper(this, getAppComponent().getSearchApi());
            addSubscription(mSearchHelper.onSearchTextChange(RxSearchView.queryTextChanges(searchView)
                    .debounce(400, MILLISECONDS)
                    .filter(charSequence -> charSequence != null && charSequence.length() > 0)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewUtils.setupToolbar((AppCompatActivity) getActivity(), (Toolbar) view.findViewById(R.id.toolbar), getString(R.string.title_popular_movies), false);
        progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.lightGreen300), MULTIPLY);
        setupRecyclerView();
        loadMovies();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mSearchHelper = null;
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        mMovieAdapter = new MovieAdapter(this, mMovies, getAppComponent().getPicasso());
        recyclerView.setAdapter(mMovieAdapter);
        toggleEmptyView();
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        mBottomReachedListener = new BottomReachedScrollListener(linearLayoutManager, this::loadMovies);
        recyclerView.addOnScrollListener(mBottomReachedListener);
    }

    /**
     * Performs request to fetch data from server.
     */
    private void loadMovies() {
        // if the content is being loaded - ignore this request
        if (sMode == IDLE) {
            final boolean isAdapterEmpty = mMovieAdapter.isEmpty();
            // needed for correctly showing footer view
            if (!isAdapterEmpty) sMode = FETCHING;

            if (ConnectivityUtils.isNetworkAvailable(getActivity())) {
                sMode = isAdapterEmpty ? IDLE : FETCHING;
                if (isAdapterEmpty) toggleProgressBar(true);

                unsubscribeAll();
                addSubscription(RxManager.getPopularMovies(traktTvDefaultApi, mPageCounter)
                        .subscribe(new Subscriber<List<SearchItem.Movie>>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                sMode = IDLE;
                                showSnackbar(false);
                            }

                            @Override
                            public void onNext(List<SearchItem.Movie> movies) {
                                sMode = IDLE;
                                if (isAdapterEmpty) toggleProgressBar(false);

                                // successfully downloaded, can increment paging
                                ++mPageCounter;

                                mMovies.addAll(movies);
                                mMovieAdapter.notifyDataSetChanged();
                                toggleEmptyView();
                            }
                        }));
            } else {
                showSnackbar(true);
            }
        }
    }

    /**
     * Show {@link Snackbar} in exceptional conditions.
     *
     * @param noInternet indicates, whether this {@code snackbar} is being show because of no internet connection. If true - no internet text would be show. Else - general error message.
     */
    public void showSnackbar(boolean noInternet) {
        snackbar = Snackbar
                .make(coordinatorLayout, getString(R.string.error_no_internet), Snackbar.LENGTH_INDEFINITE)
                .setAction(noInternet ? getString(R.string.error_retry).toUpperCase() : getString(R.string.message_went_wrong).toUpperCase(), view -> {
                    loadMovies();
                });

        snackbar.setActionTextColor(Color.RED);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    /**
     * Shows or hides empty view, depending on {@code mMovieAdapter}'s size.
     */
    private void toggleEmptyView() {
        if (!mMovieAdapter.isEmpty()) {
            emptyView.animate().alpha(0.0f).setDuration(ANIM_DURATION_FADE).withEndAction(() -> {
                emptyView.setVisibility(View.GONE);
                emptyView.setAlpha(1.0f);
            });
        } else emptyView.setVisibility(View.VISIBLE);
    }

    /**
     * Control's {@code progressBar}'s visibility state.
     *
     * @param show if true, sets {@code progressBar}'s visibility to {@code View.VISIBLE}, else - animates to {@code View.GONE}.
     */
    private void toggleProgressBar(boolean show) {
        if (!show) {
            progressBar.animate().alpha(0.0f).setDuration(ANIM_DURATION_FADE).withEndAction(() -> {
                progressBar.setVisibility(View.GONE);
                progressBar.setAlpha(1.0f);
            });
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Indicating whether currently the data is being fetched from server.
     *
     * @return true if data is being loaded, false otherwise.
     */
    public boolean isContentLoading() {
        return sMode == FETCHING;
    }

    /**
     * We listen for connectivity change broadcast for corner cases.
     * E.g.: user opens the app, there is no internet connection, snackbar is being show.
     * When connection establishes we want the data to be loaded, not relay on user's action.
     *
     * @param ev empty event
     */
    @SuppressWarnings("unused")
    public void onEvent(ConnectivityChangeEvent ev) {
        if ((mPageCounter == 1 || mBottomReachedListener.isLoading()) && ConnectivityUtils.isNetworkAvailable(getActivity())) {
            if (null != snackbar) snackbar.dismiss();
            sMode = IDLE;
            loadMovies();
        }
    }

    /**
     * Fetches the current text in {@code searchView}.
     *
     * @return String with the query, that user has typed.
     */
    public String getQuery() {
        return searchView.getQuery().toString();
    }
}
