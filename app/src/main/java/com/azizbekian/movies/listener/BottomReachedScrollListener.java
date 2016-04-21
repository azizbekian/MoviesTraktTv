package com.azizbekian.movies.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * A subclass of {@link RecyclerView.OnScrollListener},
 * that fires {@link IBottomReached#onBottomReached()} when the end of {@link RecyclerView} is reached.
 * The <b>end</b> is specified with {@code threshold} parameter.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class BottomReachedScrollListener extends RecyclerView.OnScrollListener {

    private LinearLayoutManager linearLayoutManager;
    private IBottomReached iBottomReached;

    private final int VISIBLE_THRESHOLD;
    private int previousTotal = 0;
    private boolean loading = true;

    public BottomReachedScrollListener(LinearLayoutManager linearLayoutManager,
                                       IBottomReached iBottomReached) {
        this(linearLayoutManager, iBottomReached, 5);
    }

    public BottomReachedScrollListener(LinearLayoutManager linearLayoutManager,
                                       IBottomReached iBottomReached, int threshold) {
        this.linearLayoutManager = linearLayoutManager;
        this.iBottomReached = iBottomReached;
        VISIBLE_THRESHOLD = threshold;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = linearLayoutManager.getItemCount();
        int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
            loading = true;
            if (null != iBottomReached) iBottomReached.onBottomReached();
        }
    }

    /**
     * Interface, for dealing with "bottom reached" event.
     */
    public interface IBottomReached {
        /**
         * The code, that will be fired, when the end of the {@link RecyclerView} has been reached.
         */
        void onBottomReached();
    }

    /**
     * When the {@link java.util.List} that's being show via {@link RecyclerView}'s adapter is being
     * entirely changed, this method should be called.
     * When items are being added to the same {@link java.util.List}, no need to call this method.
     */
    public void reset() {
        previousTotal = 0;
    }

    /**
     * Indicates the status.
     *
     * @return true - if user has reached the end of {@link RecyclerView}. False - otherwise.
     */
    public boolean isLoading() {
        return loading;
    }
}
