package com.azizbekian.movies.fragment.movies;

/**
 * Created by CargoMatrix, Inc. on April 21, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public interface MoviesContract {

    interface View {

        void setupToolbar();

        void setupProgressBar();

        void setupRecycler();
    }

    interface Presenter {

        void start();

        void finish();
    }

    interface Model {

        
    }
}
