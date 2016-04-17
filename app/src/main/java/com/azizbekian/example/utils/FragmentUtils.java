package com.azizbekian.example.utils;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

/**
 * Utility class for fragment transactions.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
@SuppressWarnings("unused")
public class FragmentUtils {

    public static final String TAG_ROOT_FRAGMENT = "fragment_root";

    /**
     * This method instantiates provided {@code fragmentClass} with optional {@code fragmentBundle} attached to fragment,
     * and adds instantiated fragment to activity's {@code frameResId} with optinal {@code tag}.
     *
     * @param activity       - hosting activity
     * @param frameResId     - Layout id where fragment should be placed
     * @param fragmentClass  - The fragment class, that should be instantiated
     * @param fragmentBundle - This bundle gets supplied to the fragment
     * @param tag            - Fragment transaction's tag
     */
    public static void addRootFragment(AppCompatActivity activity, @IdRes int frameResId,
                                       Class fragmentClass, @Nullable Bundle fragmentBundle,
                                       @Nullable String tag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(frameResId, Fragment.instantiate(activity, fragmentClass.getName(), fragmentBundle), tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * This method adds {@code fragment} to activity's {@code frameResId} with optinal {@code tag}.
     *
     * @param activity   - Hosting activity
     * @param frameResId - Layout id where fragment should be placed
     * @param fragment   - Fragment instance
     * @param tag        - Fragment transaction's tag
     */
    public static void addRootFragment(AppCompatActivity activity, @IdRes int frameResId,
                                       Fragment fragment, @Nullable String tag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(frameResId, fragment, tag);
        ft.commitAllowingStateLoss();
    }

    /**
     * Replaces existing fragment with a {@link Fragment}, that gets instantiated from {@code clazz} with optional {@code fragmentBundle} attached.
     *
     * @param activity       - Hosting activity
     * @param frameResId     - Layout id where fragment should be placed
     * @param clazz          - Fragment's class that should be instantiated
     * @param fragmentBundle - This bundle gets supplied to the fragment
     * @param addToBackStack - If true transaction would be added to back stack.
     * @param tag            - Fragment transaction's tag
     */
    public static void replace(AppCompatActivity activity, @IdRes int frameResId, Class clazz,
                               @Nullable Bundle fragmentBundle, boolean addToBackStack, @Nullable String tag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(frameResId, Fragment.instantiate(activity, clazz.getName(), fragmentBundle), tag);
        if (addToBackStack) ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    /**
     * Replaces existing fragment with {@code fragment}.
     *
     * @param activity       - Hosting activity
     * @param frameResId     - Layout id where fragment should be placed
     * @param fragment       - Fragment instance
     * @param addToBackStack - If true transaction would be added to back stack.
     * @param tag            - Fragment transaction's tag
     */
    public static void replace(FragmentActivity activity, @IdRes int frameResId, Fragment fragment,
                               boolean addToBackStack, @Nullable String tag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(frameResId, fragment, tag);
        if (addToBackStack) ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    /**
     * Instantiates a fragment from {@code clazz} with optional {@code bundle} supplied, and adds the fragment to {@code frameResId}
     *
     * @param activity       - Hosting activity
     * @param frameResId     - Layout id where fragment should be placed
     * @param clazz          - Fragment's class that should be instantiated
     * @param bundle         - This bundle gets supplied to the fragment
     * @param addToBackStack - If true transaction would be added to back stack.
     * @param tag            - Fragment transaction's tag
     */
    public static void add(FragmentActivity activity, @IdRes int frameResId, Class clazz,
                           @Nullable Bundle bundle, boolean addToBackStack, @Nullable String tag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(frameResId, Fragment.instantiate(activity, clazz.getName(), bundle), tag);
        if (addToBackStack) ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    /**
     * Adds provided {@code fragment} to {@code frameResId}.
     *
     * @param activity       - Hosting activity
     * @param frameResId     - Layout id where fragment should be placed
     * @param fragment       - Fragment instance
     * @param addToBackStack - If true transaction would be added to back stack.
     * @param tag            - Fragment transaction's tag
     */
    public static void add(FragmentActivity activity, @IdRes int frameResId, Fragment fragment,
                           boolean addToBackStack, @Nullable String tag) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.add(frameResId, fragment, tag);
        if (addToBackStack) ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commitAllowingStateLoss();
    }

    /**
     * Pops off the last fragment in the provided activity's stack.
     *
     * @param activity The activity which fragment's stack will be popped
     */
    public static void pop(FragmentActivity activity) {
        activity.getSupportFragmentManager().popBackStack();
    }
}
