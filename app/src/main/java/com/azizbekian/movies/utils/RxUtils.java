package com.azizbekian.movies.utils;

import android.text.TextUtils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by CargoMatrix, Inc. on April 21, 2016.
 *
 * @author Andranik Azizbekian (andranik.azizbekyan@cargomatrix.com)
 */
public class RxUtils {

    public static final int DURATION_DEBOUNCE = 300;

    private RxUtils() {
    }

    /**
     * {@link Observable.Transformer} that transforms the source observable to subscribe in the
     * io thread and observe on the Android's UI thread.
     */
    @SuppressWarnings("all")
    private static final Observable.Transformer IO_TO_MAIN_THREAD_SCHEDULER_TRANSFORMER = observable -> ((Observable) observable)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());

    @SuppressWarnings("all")
    private static final Observable.Transformer NOT_EMPTY_TEXT_CHANGE_TRANSFORMER = o -> ((Observable<CharSequence>) o)
            .skip(1)
            .debounce(RxUtils.DURATION_DEBOUNCE, TimeUnit.MILLISECONDS)
            .filter(charSequence -> !TextUtils.isEmpty(charSequence))
            .observeOn(AndroidSchedulers.mainThread());

    /**
     * Get {@link Observable.Transformer} that transforms the source observable to subscribe in
     * the io thread and observe on the Android's UI thread.
     * <p>
     * Because it doesn't interact with the emitted items it's safe ignore the unchecked casts.
     * <p>
     * This is a shorthand for the following code:
     * <pre>
     *      Observable.subscribeOn(Schedulers.io())
     *                 .observeOn(AndroidSchedulers.mainThread())
     *                 .subscribe(subscriber);
     * </pre>
     * Change to this:
     * <pre>
     *      Observable.compose(RxUtils.applyIOtoMainThreadSchedulers())
     *                 .subscribe(subscriber);
     * </pre>
     *
     * @return {@link Observable.Transformer}
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyIOtoMainThreadSchedulers() {
        return (Observable.Transformer<T, T>) IO_TO_MAIN_THREAD_SCHEDULER_TRANSFORMER;
    }

    /**
     * @return {@link rx.Observable.Transformer} that transforms source observable into
     * {@link CharSequence} emitting stream. Properties of emitted items of the stream:
     * <p>
     * <ul>
     * <li>First emission is skipped.</li>
     * <li>Debounced by {@value DURATION_DEBOUNCE} milliseconds.</li>
     * <li>Items are observed on {@code AndroidSchedulers.mainThread}.</li>
     * </ul>
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyNotEmptyTextChangeTransformer() {
        return (Observable.Transformer<T, T>) NOT_EMPTY_TEXT_CHANGE_TRANSFORMER;
    }
}
