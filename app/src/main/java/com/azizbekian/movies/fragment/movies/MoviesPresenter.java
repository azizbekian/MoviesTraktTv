package com.azizbekian.movies.fragment.movies;

import com.azizbekian.movies.MoviesApplication;

/**
 * Created by CargoMatrix, Inc. on April 21, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public class MoviesPresenter implements MoviesContract.Presenter {

    private MoviesContract.View mView;
    private MoviesContract.Model mModel;

    public MoviesPresenter(MoviesContract.View view) {
        this.mView = view;
        this.mModel = new MovieModel();
    }

    @Override
    public void start() {
        if (null != mView) {
            mView.setupToolbar();
            mView.setupProgressBar();
            mView.setupRecycler();
        }

        MoviesApplication.getAppContext();
    }

    @Override
    public void finish() {

    }
}
