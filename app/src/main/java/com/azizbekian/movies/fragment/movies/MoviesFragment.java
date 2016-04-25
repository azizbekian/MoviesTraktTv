package com.azizbekian.movies.fragment.movies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
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
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.azizbekian.movies.R;
import com.azizbekian.movies.activity.DetailMovieActivity;
import com.azizbekian.movies.entity.ConnectivityChangeEvent;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.fragment.BaseFragment;
import com.azizbekian.movies.listener.BottomReachedScrollListener;
import com.azizbekian.movies.utils.AndroidVersionUtils;
import com.azizbekian.movies.utils.AnimationUtils;
import com.azizbekian.movies.utils.ViewUtils;
import com.jakewharton.rxbinding.view.RxView;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import rx.Subscription;
import rx.functions.Action1;

import static android.graphics.PorterDuff.Mode.MULTIPLY;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.azizbekian.movies.fragment.DetailMovieFragment.TAG_MOVIE;
import static com.azizbekian.movies.misc.Constants.ANIM_DURATION_FADE;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MoviesFragment extends BaseFragment implements MoviesContract.View {

    public static final String TAG = MoviesFragment.class.getSimpleName();

    private static final String KEY_SEARCHVIEW_CLOSE = "key_searchview_close";

    @Bind(R.id.coordinatorLayout) CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.recyclerView) RecyclerView mMoviesRecycler;
    @Bind(R.id.empty_view) View mEmptyView;
    @Bind(R.id.progress_indicator) ProgressBar mMainProgressBar;
    @Bind(R.id.viewstub_search) View mSearchRootView;
    private ProgressBar mSearchProgressBar;
    private View mSearchEmptyView;

    private Snackbar mSnackbar;
    private SearchView mSearchView;

    private BottomReachedScrollListener mBottomReachedListener;
    private BottomReachedScrollListener mSearchBottomReachedListener;

    private MoviesContract.Presenter mPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.main_menu, menu);
        MenuItem mSearchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        addSubscription(KEY_SEARCHVIEW_CLOSE, RxView
                .clicks(mSearchView.findViewById(R.id.search_close_btn))
                .subscribe(aVoid -> {
                    mPresenter.resetSearchData();
                    EditText et = (EditText) mSearchView.findViewById(R.id.search_src_text);
                    et.setText("");
                    mSearchView.setQuery("", false);
                }));

        MenuItemCompat.setOnActionExpandListener(mSearchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mPresenter.revealSearchLayout(false);
                        mPresenter.resetSearchData();
                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mPresenter.revealSearchLayout(true);
                        return true;
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mPresenter.onSearchMagnifierClicked(mSearchView);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        mPresenter = new MoviesPresenter(this);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        mPresenter.finish();
    }

    @Override
    public void setupToolbar() {
        if (null != getView())
            ViewUtils.setupToolbar((AppCompatActivity) getActivity(),
                    (Toolbar) getView().findViewById(R.id.toolbar),
                    getString(R.string.title_popular_movies), false);

    }

    @Override
    public void setupMainProgressBar() {
        mMainProgressBar.getIndeterminateDrawable()
                .setColorFilter(ContextCompat.getColor(getContext(), R.color.lightGreen300), MULTIPLY);
    }

    @Override
    public void setupMainRecycler() {
        mMoviesRecycler.setHasFixedSize(true);
        mMoviesRecycler.setAdapter(mPresenter.createMovieAdapter());
        showMainEmptyView(mPresenter.isMovieAdapterEmpty());
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mMoviesRecycler.getLayoutManager();
        mBottomReachedListener = new BottomReachedScrollListener(linearLayoutManager, () -> mPresenter.loadMovies());
        mMoviesRecycler.addOnScrollListener(mBottomReachedListener);
    }

    @Override
    public void setupSearchLayout() {
        if (null != getView()) {
            mSearchRootView = ((ViewStub) getView().findViewById(R.id.viewstub_search)).inflate();
            // at this point our layout is hidden and hasn't been given a chance to measure it's size
            // we need to manually measure it to get rid of reveal animation first time issue
            mSearchRootView.measure(makeMeasureSpec(getView().getWidth(), EXACTLY),
                    makeMeasureSpec(getView().getHeight(), EXACTLY));

            mSearchProgressBar = (ProgressBar) mSearchRootView.findViewById(R.id.search_progress_indicator);
            mSearchProgressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(getContext(), R.color.lightGreen300), MULTIPLY);
            mSearchEmptyView = mSearchRootView.findViewById(R.id.search_empty_view);

            RecyclerView mSearchRecyclerView = (RecyclerView) mSearchRootView.findViewById(R.id.searchRecyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mSearchRootView.getContext());
            mSearchRecyclerView.setLayoutManager(linearLayoutManager);
            mSearchRecyclerView.setHasFixedSize(true);
            mSearchRecyclerView.setAdapter(mPresenter.getSearchAdapter());

            mSearchBottomReachedListener = new BottomReachedScrollListener(linearLayoutManager, () -> mPresenter.loadSearchData());
            mSearchRecyclerView.addOnScrollListener(mSearchBottomReachedListener);
        }
    }

    @Override
    public void dispatchUnsubscribe(String key) {
        unsubscribe(key);
    }

    @Override
    public void dispatchAddSubscription(String key, Subscription subscription) {
        addSubscription(key, subscription);
    }

    @Override
    public void showSnackbar(boolean noInternet) {

        mSnackbar = Snackbar
                .make(mCoordinatorLayout, getString(R.string.error_no_internet),
                        Snackbar.LENGTH_INDEFINITE)
                .setAction(noInternet ? getString(R.string.error_retry).toUpperCase() :
                        getString(R.string.message_went_wrong).toUpperCase(), view -> {
                    mPresenter.loadMovies();
                });

        mSnackbar.setActionTextColor(Color.RED);

        View sbView = mSnackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        mSnackbar.show();
    }

    @Override
    public void showMainEmptyView(boolean show) {
        if (!show) {
            mEmptyView.animate()
                    .alpha(0f)
                    .setDuration(ANIM_DURATION_FADE)
                    .withEndAction(() -> {
                        mEmptyView.setVisibility(View.GONE);
                        mEmptyView.setAlpha(1f);
                    });
        } else mEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showMainProgressBar(boolean show) {
        if (!show) {
            mMainProgressBar.animate()
                    .alpha(0f)
                    .setDuration(ANIM_DURATION_FADE)
                    .withEndAction(() -> {
                        mMainProgressBar.setVisibility(View.GONE);
                        mMainProgressBar.setAlpha(1f);
                    });
        } else {
            mMainProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showSearchEmptyView(boolean show) {
        if (!show) {
            mSearchEmptyView.animate()
                    .alpha(0f)
                    .setDuration(ANIM_DURATION_FADE)
                    .withEndAction(() -> {
                        if (null != mSearchEmptyView) {
                            mSearchEmptyView.setVisibility(View.GONE);
                            mSearchEmptyView.setAlpha(1f);
                        }
                    });
        } else mSearchEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showSearchProgressBar(boolean show) {
        if (!show) {
            mSearchProgressBar.animate()
                    .alpha(0f)
                    .setDuration(ANIM_DURATION_FADE)
                    .withEndAction(() -> {
                        if (null != mSearchProgressBar) {
                            mSearchProgressBar.setVisibility(View.GONE);
                            mSearchProgressBar.setAlpha(1f);
                        }
                    });
        } else {
            mSearchProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void resetSearchBottomReachedListener() {
        mSearchBottomReachedListener.reset();
    }

    @Override
    public boolean isBottomReachedAndLoading() {
        return mBottomReachedListener.isLoading();
    }

    @Override
    public boolean isSearchBottomReachedAndLoading() {
        return mSearchBottomReachedListener.isLoading();
    }

    @Override
    public String getQuery() {
        return mSearchView.getQuery().toString();
    }

    @Override
    public void animateSearchView(boolean show) {
        if (show) {
            AnimationUtils.animateSearchClick(mSearchRootView, mSearchRootView.getMeasuredHeight(), mSearchRootView.getTop(), true);
        } else {
            // we do not want all data displayed when user presses search again
            mPresenter.resetSearchData();
            AnimationUtils.animateSearchClick(mSearchRootView, mSearchRootView.getRight(), mSearchRootView.getTop(), false);
        }
    }

    @Override
    public void launchDetailMovieActivity(ImageView transitionImageView, SearchItem.Movie movie) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(TAG_MOVIE, movie);

        if (AndroidVersionUtils.isHigherEqualToLollipop()) {
            final Pair<View, String>[] pairs = AnimationUtils
                    .createSafeTransitionParticipants(getActivity(), false,
                            new Pair<>(transitionImageView, getString(R.string.transition_cover)));

            DetailMovieActivity.launchActivity(getContext(), bundle, pairs);
        } else {
            DetailMovieActivity.launchActivity(this, bundle, transitionImageView);
        }
    }

    @Override
    public void launchActivity(Intent intent) {
        startActivity(intent);
    }

    @Override
    public void dismissSnackBar() {
        if (null != mSnackbar) mSnackbar.dismiss();
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
        mPresenter.onNetworkStateChanged();
    }
}
