package com.azizbekian.example.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.azizbekian.example.misc.Constants.ANIM_DURATION_FADE;

/**
 * Utility class for making animations.
 * <p>
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class AnimationUtils {

    /**
     * Performs animation on a view.
     * If Android SDK is higher than API 21 - circular reveal animation would be show. Otherwise - an ordinary fade in/out animation would be shown.
     *
     * @param view The {@link View} to animate.
     * @param x    Starting x coordinate (for reveal animation).
     * @param y    Starting y coordiante (for reveal animation).
     * @param open If true - indicates, that circular reveal/fade in anim would be show. If false - circular hide/fade out anim would be shown.
     * @return the animator object, that has already been started.
     */
    public static Animator animateSearchClick(View view, int x, int y, boolean open) {
        if (AndroidVersionUtils.isHigherEqualToLollipop()) {
            if (open) return createRevealAnim(view, x, y);
            else return createHideAnim(view, x, y);
        } else return createFadeAnim(view, open);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Animator createRevealAnim(final View viewRoot, int x, int y) {

        float finalRadius = (float) Math.hypot(viewRoot.getMeasuredWidth(), viewRoot.getMeasuredHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius);
        anim.setDuration(ANIM_DURATION_FADE);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                viewRoot.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        anim.start();
        return anim;
    }

    private static Animator createFadeAnim(final View viewRoot, final boolean fadeIn) {

        ObjectAnimator animator = ObjectAnimator.ofFloat(viewRoot, View.ALPHA, fadeIn ? 0.0f : 1.0f, fadeIn ? 1.0f : 0.0f).setDuration(ANIM_DURATION_FADE);

        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                viewRoot.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(fadeIn ? View.VISIBLE : View.INVISIBLE);
            }
        });

        animator.start();
        return animator;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Animator createHideAnim(final View viewRoot, int x, int y) {

        float initialRadius = (float) Math.hypot(viewRoot.getWidth(), viewRoot.getHeight());

        Animator anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewRoot.setVisibility(View.GONE);
            }
        });

        anim.start();
        return anim;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("unchecked")
    public static Pair<View, String>[] createSafeTransitionParticipants(@NonNull Activity activity, boolean includeStatusBar, @Nullable Pair... otherParticipants) {
        View decor = activity.getWindow().getDecorView();
        View statusBar = null;
        if (includeStatusBar) {
            statusBar = decor.findViewById(android.R.id.statusBarBackground);
        }
        View navBar = decor.findViewById(android.R.id.navigationBarBackground);

        // Create pair of transition participants.
        List<Pair> participants = new ArrayList<>(3);
        addNonNullViewToTransitionParticipants(statusBar, participants);
        addNonNullViewToTransitionParticipants(navBar, participants);
        // only add transition participants if there's at least one none-null element
        if (otherParticipants != null && !(otherParticipants.length == 1 && otherParticipants[0] == null)) {
            participants.addAll(Arrays.asList(otherParticipants));
        }
        return participants.toArray(new Pair[participants.size()]);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void addNonNullViewToTransitionParticipants(View view, List<Pair> participants) {
        if (view == null) return;
        participants.add(new Pair<>(view, view.getTransitionName()));
    }

}
