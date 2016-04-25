package com.azizbekian.movies.manager;

import com.azizbekian.movies.adapter.SearchAdapter;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.fragment.movies.MoviesContract;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;

import static com.azizbekian.movies.fragment.movies.MoviesContract.Presenter.SEARCH_IDLE;

/**
 * This class handles searching functionality.
 * Be aware, it keeps references to context, you have to null-ify this object when it's not needed.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class SearchHelper {

    private MoviesContract.Presenter mPresenter;

    private SearchAdapter mSearchAdapter;
    private Call<List<SearchItem>> mCurrentSearchCall;

    private int mPageCounter = 1;
    private int mMode = SEARCH_IDLE;

    private boolean mIsInflated = false;

    public SearchHelper(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
        mSearchAdapter = new SearchAdapter(mPresenter, Collections.emptyList());
    }

    public void inflate() {
        mPresenter.setupSearchLayout();
        mIsInflated = true;
    }

    public boolean isInflated() {
        return mIsInflated;
    }

    public SearchAdapter getSearchAdapter() {
        return mSearchAdapter;
    }

    public boolean isAdapterEmpty() {
        return null != mSearchAdapter && mSearchAdapter.isEmpty();
    }

    public void setSearchList(List<SearchItem> searchList) {
        mSearchAdapter.setItems(searchList);
        mPresenter.toggleSearchEmptyView();
        mPresenter.resetSearchBottomReachedListener();
    }

    public void addSearchResult(List<SearchItem> result) {
        mSearchAdapter.addItems(result);
        mPresenter.toggleSearchEmptyView();
    }

    public void setMode(int mMode) {
        this.mMode = mMode;
    }

    public int getMode() {
        return mMode;
    }

    public int incrementSearchPageCounter() {
        return ++mPageCounter;
    }

    public int getPageCounter() {
        return mPageCounter;
    }

    public void cancelSearch() {
        if (null != mCurrentSearchCall) mCurrentSearchCall.cancel();
    }

    public void resetData() {
        cancelSearch();
        mPageCounter = 1;
        setMode(MoviesContract.Presenter.SEARCH_IDLE);
        setSearchList(Collections.emptyList());
        mSearchAdapter.notifyDataSetChanged();
    }

    public void setCurrentSearchCall(Call<List<SearchItem>> mCurrentSearchCall) {
        this.mCurrentSearchCall = mCurrentSearchCall;
    }
}
