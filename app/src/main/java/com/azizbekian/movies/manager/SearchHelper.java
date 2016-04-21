package com.azizbekian.movies.manager;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewStub;
import android.widget.ProgressBar;

import com.azizbekian.movies.R;
import com.azizbekian.movies.adapter.SearchAdapter;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.fragment.movies.MoviesFragment;
import com.azizbekian.movies.listener.BottomReachedScrollListener;
import com.azizbekian.movies.rest.TraktTvApi;
import com.azizbekian.movies.utils.AnimationUtils;
import com.azizbekian.movies.utils.NetworkUtils;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rx.Observable;
import rx.Observer;
import rx.Subscription;

import static android.graphics.PorterDuff.Mode.MULTIPLY;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.azizbekian.movies.misc.Constants.ANIM_DURATION_FADE;
import static com.azizbekian.movies.misc.Constants.TYPE_MOVIE;

/**
 * This class handles searching functionality.
 * Be aware, it keeps references to context, you have to null-ify this object when it's not needed.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class SearchHelper {

    TraktTvApi.Search mTraktTvSearchApi;

    private MoviesFragment mFragment;
    private View mSearchEmptyView;
    private View mRoot;
    private ProgressBar mProgressBar;

    private SearchAdapter mSearchAdapter;
    private final Handler mUiHandler = new Handler(Looper.getMainLooper());
    private final List<SearchItem> mEmptyList = Collections.emptyList();
    private Call<List<SearchItem>> sCurrentSearchCall;
    private BottomReachedScrollListener mBottomReachedListener;

    private int mPageCounter = 2;

    private static final int IDLE = 0;
    private static final int FETCHING = 1;
    private static int sMode = IDLE;

    public SearchHelper(@NonNull MoviesFragment hostFragment, TraktTvApi.Search traktTvSearchApi) {
        this.mFragment = hostFragment;
        this.mTraktTvSearchApi = traktTvSearchApi;
        View view = mFragment.getView();
        if (null != view) {
            mRoot = ((ViewStub) view.findViewById(R.id.viewstub_search)).inflate();
            // at this point our layout is hidden and hasn't been given a chance to measure it's size
            // we need to manually measure it to get rid of reveal animation first time issue
            mRoot.measure(makeMeasureSpec(view.getWidth(), EXACTLY), makeMeasureSpec(view.getHeight(), EXACTLY));

            mProgressBar = (ProgressBar) mRoot.findViewById(R.id.search_progress_indicator);
            mProgressBar.getIndeterminateDrawable()
                    .setColorFilter(ContextCompat.getColor(mFragment.getContext(), R.color.lightGreen300), MULTIPLY);
            mSearchEmptyView = mRoot.findViewById(R.id.search_empty_view);

            RecyclerView mSearchRecyclerView = (RecyclerView) mRoot.findViewById(R.id.searchRecyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mRoot.getContext());
            mSearchRecyclerView.setLayoutManager(linearLayoutManager);
            mSearchRecyclerView.setHasFixedSize(true);
            mSearchAdapter = new SearchAdapter(this, mFragment, mEmptyList);
            mSearchRecyclerView.setAdapter(mSearchAdapter);

            mBottomReachedListener = new BottomReachedScrollListener(linearLayoutManager, this::loadSearchResult);
            mSearchRecyclerView.addOnScrollListener(mBottomReachedListener);
        }
    }

    /**
     * Shows or hides empty view, depending on {@code mMovieAdapter}'s size.
     */
    private void toggleEmptyView() {
        if (!mSearchAdapter.isEmpty()) {
            mSearchEmptyView.animate()
                    .alpha(0.0f)
                    .setDuration(ANIM_DURATION_FADE)
                    .withEndAction(() -> {
                        if (null != mSearchEmptyView) {
                            mSearchEmptyView.setVisibility(View.GONE);
                            mSearchEmptyView.setAlpha(1.0f);
                        }
                    });
        } else mSearchEmptyView.setVisibility(View.VISIBLE);
    }

    public void setSearchResult(List<SearchItem> result) {
        mSearchAdapter.setItems(result);
        toggleEmptyView();
        mBottomReachedListener.reset();
    }

    public void addSearchResult(List<SearchItem> result) {
        mSearchAdapter.addItems(result);
        toggleEmptyView();
    }

    public void resetData() {
        cancelSearch();
        mPageCounter = 2;
        setSearchResult(mEmptyList);
    }

    /**
     * Performs request to fetch data from server.
     */
    private void loadSearchResult() {
        // if the content is being loaded - ignore this request
        if (sMode == IDLE) {
            final boolean isAdapterEmpty = mSearchAdapter.isEmpty();
            if (!isAdapterEmpty) sMode = FETCHING;

            if (NetworkUtils.isNetworkAvailable(mFragment.getContext())) {
                sMode = isAdapterEmpty ? IDLE : FETCHING;
                if (isAdapterEmpty) toggleProgressBar(true);
                String query = mFragment.getQuery();
                cancelSearch();
                sCurrentSearchCall = mTraktTvSearchApi.searchMovies(query, TYPE_MOVIE, mPageCounter);
                sCurrentSearchCall.enqueue(new Callback<List<SearchItem>>() {
                    @Override
                    public void onResponse(Call<List<SearchItem>> call, Response<List<SearchItem>> response) {
                        sMode = IDLE;
                        toggleProgressBar(false);
                        List<SearchItem> searchItems = response.body();
                        if (searchItems == null) {
                            // something went wrong, show empty list
                            setSearchResult(mEmptyList);
                        } else {
                            // successfully downloaded, can increment paging
                            ++mPageCounter;
                            if (isAdapterEmpty) setSearchResult(searchItems);
                            else addSearchResult(searchItems);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<SearchItem>> call, Throwable t) {
                        sMode = IDLE;
                        if (isAdapterEmpty) toggleProgressBar(false);
                        // an error occurred, show empty list
                        setSearchResult(mEmptyList);
                    }
                });
            } else {
                mFragment.showSnackbar(true);
            }
        }
    }

    /**
     * Control's {@code progressBar}'s visibility state.
     *
     * @param show if true, sets {@code progressBar}'s visibility to {@code View.VISIBLE}, else - animates to {@code View.GONE}.
     */
    public void toggleProgressBar(boolean show) {
        if (!show) {
            mProgressBar.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .withEndAction(() -> {
                        if (null != mProgressBar) {
                            mProgressBar.setVisibility(View.GONE);
                            mProgressBar.setAlpha(1.0f);
                        }
                    });
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
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
     * When {@link MoviesFragment#mSearchView} content is being changed, request is being sent to server in order to fetch appropriate search data.
     *
     * @param observable Filtered observable. If the stream has reached this point, then the call should be sent.
     * @return Subscription to hold on, in order to unsubscribe later.
     */
    public Subscription onSearchTextChange(Observable<CharSequence> observable) {
        return observable.map(charSequence -> {
            sMode = FETCHING;
            mPageCounter = 2;
            mUiHandler.post(() -> toggleProgressBar(true));
            cancelSearch();
            sCurrentSearchCall = mTraktTvSearchApi.searchMovies(charSequence.toString(), TYPE_MOVIE, 1);
            return sCurrentSearchCall;
        })
                .subscribe(new Observer<Call<List<SearchItem>>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        sMode = IDLE;
                    }

                    @Override
                    public void onNext(Call<List<SearchItem>> listCall) {
                        listCall.enqueue(new Callback<List<SearchItem>>() {
                            @Override
                            public void onResponse(Call<List<SearchItem>> call, Response<List<SearchItem>> response) {
                                sMode = IDLE;
                                toggleProgressBar(false);
                                List<SearchItem> searchItems = response.body();
                                setSearchResult(searchItems == null ? mEmptyList : searchItems);
                            }

                            @Override
                            public void onFailure(Call<List<SearchItem>> call, Throwable t) {
                                sMode = IDLE;
                                toggleProgressBar(false);
                                // an error occurred, show empty list
                                setSearchResult(mEmptyList);
                            }
                        });
                    }
                });
    }

    /**
     * Cancel current search call.
     */
    public void cancelSearch() {
        if (null != sCurrentSearchCall) sCurrentSearchCall.cancel();
    }

    /**
     * Opens search view with appropriate animation applied.
     *
     * @param show boolean, indicating whether searchview should be opened or close. If true - opens search view, closes otherwise.
     */
    public void animateSearchView(boolean show) {

        if (show) {
            AnimationUtils.animateSearchClick(mRoot, mRoot.getMeasuredHeight(), mRoot.getTop(), true);
        } else {
            // we do not want all data displayed when user presses search again
            resetData();
            AnimationUtils.animateSearchClick(mRoot, mRoot.getRight(), mRoot.getTop(), false);
        }
    }

}
