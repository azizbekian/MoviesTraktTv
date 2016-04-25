package com.azizbekian.movies.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.azizbekian.movies.MoviesApplication;
import com.azizbekian.movies.R;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.fragment.movies.MoviesContract;
import com.azizbekian.movies.misc.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.graphics.PorterDuff.Mode.MULTIPLY;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MovieAdapter extends HeaderFooterRecyclerViewAdapter {

    private MoviesContract.Presenter mPresenter;
    private Picasso mPicasso;
    private List<SearchItem.Movie> mData;

    public MovieAdapter(MoviesContract.Presenter presenter, List<SearchItem.Movie> movies) {
        mPresenter = presenter;
        mData = movies;
        mPicasso = MoviesApplication.getAppComponent().getPicasso();
    }

    @Override
    protected int getHeaderItemCount() {
        return 0;
    }

    @Override
    protected int getFooterItemCount() {
        return 1;
    }

    @Override
    protected int getContentItemCount() {
        return mData.size();
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        return null;
    }

    @Override
    protected FooterViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    protected ContentViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {
        footerViewHolder.itemView.setVisibility(!mPresenter.isMoviesModeIdle() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {

        final SearchItem.Movie movie = mData.get(position);
        final ContentViewHolder contentHolder = (ContentViewHolder) contentViewHolder;

        contentHolder.movieTitle.setText(movie.title);
        contentHolder.movieOverview.setText(movie.overview);
        contentHolder.movieYear.setText(movie.year);
        contentHolder.movieRating.setText(movie.rating);

        // Not all movies have trailer and imdb uri
        // if this movie doesn't have both trailer uri and imdb URI - hide the whole layout
        if (null == movie.getImdb() && null == movie.trailer) {
            contentHolder.movieLowerBar.setVisibility(View.GONE);
            contentHolder.movieLowerDivider.setVisibility(View.GONE);
        } else {
            contentHolder.movieLowerBar.setVisibility(View.VISIBLE);
            contentHolder.movieLowerDivider.setVisibility(View.VISIBLE);
            contentHolder.movieImdb.setVisibility(TextUtils.isEmpty(movie.getImdb()) ? View.GONE : View.VISIBLE);
            contentHolder.movieTrailer.setVisibility(TextUtils.isEmpty(movie.trailer) ? View.GONE : View.VISIBLE);
        }

        mPicasso.load(movie.getThumb())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(contentHolder.movieCover);

        contentHolder.movieImdb.setOnClickListener(v -> mPresenter.openUri(Constants.PREFIX_IMDB + movie.getImdb()));
        contentHolder.movieTrailer.setOnClickListener(v -> mPresenter.openUri(movie.trailer));
        contentHolder.movieContainer.setOnClickListener(v -> mPresenter.launchDetailMovieActivity(contentHolder.movieCover, movie));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder instanceof ContentViewHolder) {
            ContentViewHolder contentHolder = (ContentViewHolder) holder;
            contentHolder.movieImdb.setOnClickListener(null);
            contentHolder.movieTrailer.setOnClickListener(null);
            contentHolder.movieContainer.setOnClickListener(null);
        }
    }

    /**
     * @return True if adapter is empty. False otherwise.
     */
    public boolean isEmpty() {
        return null == mData || mData.size() == 0;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder {

        public View movieContainer;
        public ImageView movieCover;
        public TextView movieTitle;
        public TextView movieOverview;
        public TextView movieYear;
        public TextView movieRating;
        public TextView movieImdb;
        public TextView movieTrailer;
        public View movieLowerDivider;
        public View movieLowerBar;

        public ContentViewHolder(View itemView) {
            super(itemView);

            movieContainer = itemView.findViewById(R.id.movie_container);
            movieCover = (ImageView) itemView.findViewById(R.id.movie_cover);
            movieTitle = (TextView) itemView.findViewById(R.id.movie_title);
            movieOverview = (TextView) itemView.findViewById(R.id.movie_overview);
            movieYear = (TextView) itemView.findViewById(R.id.movie_year);
            movieRating = (TextView) itemView.findViewById(R.id.movie_rating);
            movieImdb = (TextView) itemView.findViewById(R.id.movie_imdb);
            movieTrailer = (TextView) itemView.findViewById(R.id.movie_trailer);
            movieLowerDivider = itemView.findViewById(R.id.movie_lower_divider);
            movieLowerBar = itemView.findViewById(R.id.movie_lower_bar);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
            ProgressBar progressBar = (ProgressBar) itemView.findViewById(R.id.recyclerView_progressbar);
            progressBar.getIndeterminateDrawable().setColorFilter(ContextCompat
                    .getColor(itemView.getContext(), R.color.lightGreen300), MULTIPLY);
        }
    }

}
