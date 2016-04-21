package com.azizbekian.movies.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.azizbekian.movies.MoviesApplication;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.misc.Constants;
import com.azizbekian.movies.utils.AndroidVersionUtils;
import com.azizbekian.movies.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.azizbekian.movies.R;

import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.azizbekian.movies.misc.Constants.ANIM_DURATION_TRANSITION_SHORT;

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

    private String mCover;
    private String mTitle;
    private String mOverview;
    private String mYear;
    private String mRating;
    private String mImdb;
    private String mTrailer;

    private int mOrientation;
    private int mTop;
    private int mWidth;
    private int mHeight;

    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private int mCoverPhotoWidth, mCoverPhotoHeight;

    @Bind(R.id.movie_cover) ImageView mMovieCover;
    @Bind(R.id.movie_title) TextView mMovieTitle;
    @Bind(R.id.movie_overview) TextView mMovieOverview;
    @Bind(R.id.movie_year) TextView mMovieYear;
    @Bind(R.id.movie_rating) TextView mMovieRating;
    @Bind(R.id.movie_lower_divider) View mMovieLowerDivider;
    @Bind(R.id.movie_lower_bar) View mMovieLowerBar;
    @Bind(R.id.movie_imdb) TextView mMovieImdb;
    @Bind(R.id.movie_trailer) TextView mMovieTrailer;
    @Bind(R.id.label_rating) TextView mLabelRating;

    @Inject Picasso mPicasso;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        MoviesApplication.getAppComponent().inject(this);

        Bundle extras = getArguments();
        if (null != extras) {
            SearchItem.Movie movie = extras.getParcelable(TAG_MOVIE);
            if (null != movie) {
                mTitle = movie.title;
                mOverview = movie.overview;
                mYear = movie.year;
                mRating = movie.rating;
                mImdb = movie.getImdb();
                mTrailer = movie.trailer;
                mCover = movie.getThumb();
            }
            if (!AndroidVersionUtils.isHigherEqualToLollipop()) {
                mOrientation = extras.getInt(KEY_ORIENTATION);
                mTop = extras.getInt(KEY_TOP);
                mWidth = extras.getInt(KEY_WIDTH);
                mHeight = extras.getInt(KEY_HEIGHT);

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

        mMovieCover.measure(makeMeasureSpec(mCoverPhotoWidth, EXACTLY), makeMeasureSpec(mCoverPhotoHeight, EXACTLY));

        if (!AndroidVersionUtils.isHigherEqualToLollipop() && null == savedInstanceState) {
            ViewTreeObserver observer = mMovieCover.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    mMovieCover.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    mMovieCover.getLocationOnScreen(screenLocation);
                    mTopDelta = mTop - screenLocation[1];
                    mWidthScale = (float) mWidth / mMovieCover.getMeasuredWidth();
                    mHeightScale = (float) mHeight / mMovieCover.getMeasuredHeight();

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

        ViewUtils.setupToolbar((AppCompatActivity) getActivity(), (Toolbar) view.findViewById(R.id.toolbar), mTitle, true);
        mMovieTitle.setText(mTitle);
        mMovieOverview.setText(mOverview);
        mMovieYear.setText(mYear);

        if (TextUtils.isEmpty(mRating)) {
            mLabelRating.setVisibility(View.GONE);
        } else mMovieRating.setText(mRating);

        boolean isImdbEmpty = TextUtils.isEmpty(mImdb);
        boolean isTrailerEmpty = TextUtils.isEmpty(mTrailer);

        if (isImdbEmpty && isTrailerEmpty) {
            mMovieLowerDivider.setVisibility(View.GONE);
            mMovieLowerBar.setVisibility(View.GONE);
        } else if (isImdbEmpty) {
            mMovieImdb.setVisibility(View.GONE);
        } else if (isTrailerEmpty) {
            mMovieTrailer.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(mCover)) {
            mPicasso.load(mCover)
                    .error(R.drawable.placeholder)
                    .placeholder(R.drawable.placeholder)
                    .into(mMovieCover);
        } else mMovieCover.setImageResource(R.drawable.placeholder);
    }

    private void runEnterAnimation() {
        mMovieCover.setPivotX(0);
        mMovieCover.setPivotY(0);
        mMovieCover.setScaleX(mWidthScale);
        mMovieCover.setScaleY(mHeightScale);
        mMovieCover.setTranslationY(mTopDelta);

        mMovieCover.animate()
                .setDuration(ANIM_DURATION_TRANSITION_SHORT)
                .scaleX(1)
                .scaleY(1)
                .translationY(0)
                .setInterpolator(new AccelerateDecelerateInterpolator());
    }

    public void runExitAnimation(final Runnable endAction) {
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOrientation) {
            mMovieCover.setPivotX(mMovieCover.getWidth() / 2);
            mMovieCover.setPivotY(mMovieCover.getHeight() / 2);
            mTopDelta = 0;
            fadeOut = true;
        } else fadeOut = false;

        mMovieCover.animate()
                .setDuration(ANIM_DURATION_TRANSITION_SHORT)
                .scaleX(mWidthScale)
                .scaleY(mHeightScale)
                .translationY(mTopDelta)
                .withEndAction(endAction);

        if (fadeOut) mMovieCover.animate().alpha(0);
    }

    @Override
    public void onDestroyView() {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @OnClick({R.id.movie_imdb, R.id.movie_trailer})
    public void onLabelClicked(View v) {
        switch (v.getId()) {
            case R.id.movie_imdb:
                openUri(Constants.PREFIX_IMDB + mImdb);
                break;
            case R.id.movie_trailer:
                openUri(mTrailer);
                break;
            default:
                break;
        }
    }

    private void openUri(String uri) {
        if (TextUtils.isEmpty(uri)) return;

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(uri));
        getContext().startActivity(i);
    }

}