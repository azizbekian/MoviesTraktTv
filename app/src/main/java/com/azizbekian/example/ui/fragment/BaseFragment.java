package com.azizbekian.example.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class BaseFragment extends Fragment {

    private List<Subscription> mSubscriptionsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSubscriptionsList = new ArrayList<>();
    }

    /**
     * Adds this {@code subscription} to {@code mSubscriptionsList}, so that we can unsubscribe from all subscription when we are not interested in the result.
     *
     * @param subscription A subscription to add to the list.
     */
    protected void addSubscription(Subscription subscription) {
        mSubscriptionsList.add(subscription);
    }

    /**
     * Unsubscribes from all subscriptions from {@code mSubscriptionsList}.
     */
    @SuppressWarnings("all")
    protected void unsubscribeAll() {
        for (Subscription subscription : mSubscriptionsList) subscription.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribeAll();
    }
}
