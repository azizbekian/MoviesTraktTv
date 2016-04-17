package com.azizbekian.example.ui.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.azizbekian.example.R;
import com.azizbekian.example.ui.fragment.DetailMovieFragment;
import com.azizbekian.example.utils.AndroidVersionUtils;
import com.azizbekian.example.utils.FragmentUtils;

import static com.azizbekian.example.misc.Constants.ANIM_DURATION_TRANSITION_NORMAL;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class DetailMovieActivity extends AppCompatActivity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launchActivity(Context context, Bundle bundle, Pair<View, String>[] pairs) {
        Intent intent = new Intent(context, DetailMovieActivity.class);
        intent.putExtras(bundle);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                (MainActivity) context, pairs);
        context.startActivity(intent, options.toBundle());
    }

    public static void launchActivity(Fragment fragment, Bundle bundle, ImageView imageView) {
        Bundle transitionBundle = new Bundle();
        int[] screenLocation = new int[2];
        imageView.getLocationOnScreen(screenLocation);
        transitionBundle.putInt(DetailMovieFragment.KEY_ORIENTATION, fragment.getResources()
                .getConfiguration().orientation);
        transitionBundle.putInt(DetailMovieFragment.KEY_TOP, screenLocation[1]);
        transitionBundle.putInt(DetailMovieFragment.KEY_WIDTH, imageView.getWidth());
        transitionBundle.putInt(DetailMovieFragment.KEY_HEIGHT, imageView.getHeight());

        Intent intent = new Intent(fragment.getContext(), DetailMovieActivity.class);
        intent.putExtras(bundle);
        intent.putExtras(transitionBundle);
        fragment.getContext().startActivity(intent);
        fragment.getActivity().overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (null == savedInstanceState) {
            if (AndroidVersionUtils.isHigherEqualToLollipop()) {
                setupWindowAnimations();
                setupLayout();
            } else {
                FragmentUtils.addRootFragment(this, R.id.content_frame, DetailMovieFragment.class,
                        getIntent().getExtras(), DetailMovieFragment.TAG);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupWindowAnimations() {
        getWindow().getEnterTransition().setDuration(ANIM_DURATION_TRANSITION_NORMAL);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("all")
    private void setupLayout() {
        Slide slideTransition = new Slide(Gravity.LEFT);
        slideTransition.setDuration(ANIM_DURATION_TRANSITION_NORMAL);
        Fragment fragment = Fragment.instantiate(this, DetailMovieFragment.class.getName(),
                getIntent().getExtras());
        fragment.setReenterTransition(slideTransition);
        fragment.setExitTransition(slideTransition);
        fragment.setSharedElementEnterTransition(new ChangeBounds());

        getSupportFragmentManager().beginTransaction()
                .add(R.id.content_frame, fragment)
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // if not done, toolbar's arrow won't finish with transition
                if (AndroidVersionUtils.isHigherEqualToLollipop()) {
                    finishAfterTransition();
                } else {
                    onBackPressed();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final DetailMovieFragment fragment = (DetailMovieFragment) getSupportFragmentManager()
                .findFragmentByTag(DetailMovieFragment.TAG);
        if (null != fragment && fragment.isVisible()) {
            fragment.runExitAnimation(() -> {
                getSupportFragmentManager().beginTransaction().detach(fragment).commit();
                finish();
            });
        } else super.onBackPressed();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
