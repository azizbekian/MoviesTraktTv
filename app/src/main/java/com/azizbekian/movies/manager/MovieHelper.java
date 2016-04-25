package com.azizbekian.movies.manager;

import android.support.v7.widget.RecyclerView;

import com.azizbekian.movies.adapter.MovieAdapter;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.fragment.movies.MoviesContract;

import java.util.ArrayList;
import java.util.List;

import static com.azizbekian.movies.fragment.movies.MoviesContract.Presenter.MOVIES_IDLE;

/**
 * Created by CargoMatrix, Inc. on April 25, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public class MovieHelper {

    private MoviesContract.Presenter mPresenter;

    private MovieAdapter mMovieAdapter;
    private List<SearchItem.Movie> mMoviesList = new ArrayList<>();

    private int mMoviePageCounter = 1;
    private int mMode = MOVIES_IDLE;

    public MovieHelper(MoviesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public int getPageCounter() {
        return mMoviePageCounter;
    }

    public RecyclerView.Adapter getAdapter() {
        return mMovieAdapter = new MovieAdapter(mPresenter, mMoviesList);
    }

    public void notifyDataSetChanged() {
        mMovieAdapter.notifyDataSetChanged();
    }

    public void addMovies(List<SearchItem.Movie> movies) {
        mMoviesList.addAll(movies);
    }

    public int incrementPage() {
        return ++mMoviePageCounter;
    }

    public boolean isIdle() {
        return mMode == MOVIES_IDLE;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    public boolean isAdapterEmpty() {
        return mMovieAdapter.isEmpty();
    }
}
