package com.azizbekian.example.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.azizbekian.example.MyApplication;
import com.azizbekian.example.entity.SearchItem;
import com.azizbekian.example.utils.AndroidVersionUtils;
import com.azizbekian.example.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.azizbekian.example.R;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.azizbekian.example.misc.Constants.ANIM_DURATION_TRANSITION_SHORT;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class DetailMovieFragment extends Fragment {

    public static final String TAG = DetailMovieFragment.class.getSimpleName();
    public static final String TAG_MOVIE = "tag_movie";
    public static final String KEY_ORIENTATION = "orientation";
    public static final String KEY_TOP = "top";
    public static final String KEY_WIDTH = "width";
    public static final String KEY_HEIGHT = "height";

    private String cover;
    private String title;
    private String overview;
    private String year;
    private String rating;
    private String imdb;
    private String trailer;

    private int orientation;
    private int top;
    private int width;
    private int height;

    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private int mCoverPhotoWidth, mCoverPhotoHeight;

    @Bind(R.id.movie_cover)
    ImageView movieCover;
    @Bind(R.id.movie_title)
    TextView movieTitle;
    @Bind(R.id.movie_overview)
    TextView movieOverview;
    @Bind(R.id.movie_year)
    TextView movieYear;
    @Bind(R.id.movie_rating)
    TextView movieRating;
    @Bind(R.id.movie_lower_divider)
    View movieLowerDivider;
    @Bind(R.id.movie_lower_bar)
    View movieLowerBar;
    @Bind(R.id.movie_imdb)
    TextView movieImdb;
    @Bind(R.id.movie_trailer)
    TextView movieTrailer;
    @Bind(R.id.label_rating)
    TextView labelRating;

    @Inject
    Picasso picasso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        MyApplication.getAppComponent().inject(this);

        Bundle extras = getArguments();
        if (null != extras) {
            SearchItem.Movie movie = extras.getParcelable(TAG_MOVIE);
            if (null != movie) {
                title = movie.title;
                overview = movie.overview;
                year = movie.year;
                rating = movie.rating;
                imdb = movie.getImdb();
                trailer = movie.trailer;
                cover = movie.getThumb();
            }
            if (!AndroidVersionUtils.isHigherEqualToLollipop()) {
                orientation = extras.getInt(KEY_ORIENTATION);
                top = extras.getInt(KEY_TOP);
                width = extras.getInt(KEY_WIDTH);
                height = extras.getInt(KEY_HEIGHT);

                mCoverPhotoWidth = getResources().getDimensionPixelSize(R.dimen.cover_photo_width);
                mCoverPhotoHeight = getResources().getDimensionPixelSize(R.dimen.cover_photo_height);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        movieCover.measure(makeMeasureSpec(mCoverPhotoWidth, EXACTLY), makeMeasureSpec(mCoverPhotoHeight, EXACTLY));

        if (!AndroidVersionUtils.isHigherEqualToLollipop() && null == savedInstanceState) {
            ViewTreeObserver observer = movieCover.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    movieCover.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    movieCover.getLocationOnScreen(screenLocation);
                    mTopDelta = top - screenLocation[1];
                    mWidthScale = (float) width / movieCover.getMeasuredWidth();
                    mHeightScale = (float) height / movieCover.getMeasuredHeight();

                    runEnterAnimation();

                    return true;
                }
            });
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ViewUtils.setupToolbar((AppCompatActivity) getActivity(), (Toolbar) view.findViewById(R.id.toolbar), title, true);
        movieTitle.setText(title);
        movieOverview.setText(overview);
        movieYear.setText(year);
        if (null == rating) {
            labelRating.setVisibility(View.GONE);
        } else movieRating.setText(rating);

        if (null == imdb && null == trailer) {
            movieLowerDivider.setVisibility(View.GONE);
            movieLowerBar.setVisibility(View.GONE);
        } else if (null == imdb) {
            movieImdb.setVisibility(View.GONE);
        } else if (null == trailer) {
            movieTrailer.setVisibility(View.GONE);
        }

        if (null != cover)
            picasso.load(cover).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(movieCover);
        else movieCover.setImageResource(R.drawable.placeholder);
    }

    private void runEnterAnimation() {
        movieCover.setPivotX(0);
        movieCover.setPivotY(0);
        movieCover.setScaleX(mWidthScale);
        movieCover.setScaleY(mHeightScale);
        movieCover.setTranslationY(mTopDelta);

        movieCover.animate().setDuration(ANIM_DURATION_TRANSITION_SHORT).
                scaleX(1).scaleY(1)
                .translationY(0).
                setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void runExitAnimation(final Runnable endAction) {
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != orientation) {
            movieCover.setPivotX(movieCover.getWidth() / 2);
            movieCover.setPivotY(movieCover.getHeight() / 2);
            mTopDelta = 0;
            fadeOut = true;
        } else {
            fadeOut = false;
        }

        movieCover.animate().setDuration(ANIM_DURATION_TRANSITION_SHORT).
                scaleX(mWidthScale).scaleY(mHeightScale)
                .translationY(mTopDelta).
                withEndAction(endAction);
        if (fadeOut) {
            movieCover.animate().alpha(0);
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @OnClick({R.id.movie_imdb, R.id.movie_trailer})
    public void onLabelClick(View v) {
        switch (v.getId()) {
            case R.id.movie_imdb:
                openUri("http://www.imdb.com/title/" + imdb);
                break;
            case R.id.movie_trailer:
                openUri(trailer);
                break;
            default:
                break;
        }
    }

    private void openUri(String uri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(uri));
        getContext().startActivity(i);
    }

}