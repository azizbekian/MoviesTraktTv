package com.azizbekian.movies.misc;

import android.support.annotation.IntDef;

import com.azizbekian.movies.fragment.movies.MoviesContract;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by CargoMatrix, Inc. on April 22, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef({
        MoviesContract.Presenter.MOVIES_FETCHING, MoviesContract.Presenter.MOVIES_IDLE
})
public @interface MoviesModeType {
}