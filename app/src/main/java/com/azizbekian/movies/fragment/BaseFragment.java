package com.azizbekian.movies.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Subscription;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class BaseFragment extends Fragment {

    private ArrayMap<String, Subscription> mSubscriptionsList = new ArrayMap<>();

    /**
     * Adds this {@code subscription} to {@code mSubscriptionsList}, so that we can unsubscribe from all
     * subscription when we are not interested in the result.
     *
     * @param subscription A subscription to add to the list.
     */
    protected void addSubscription(String key, Subscription subscription) {
        mSubscriptionsList.put(key, subscription);
    }

    /**
     * Unsubscribes from all subscriptions from {@code mSubscriptionsList}.
     */
    @SuppressWarnings("all")
    protected void unsubscribeAll() {
        for (Subscription subscription : mSubscriptionsList.values()) subscription.unsubscribe();
    }

    protected void unsubscribe(String key) {
        if (mSubscriptionsList.containsKey(key)) {
            mSubscriptionsList.get(key).unsubscribe();
        }
    }

    @Override
    public void onDestroyView() {
        unsubscribeAll();
        super.onDestroyView();
    }
}
