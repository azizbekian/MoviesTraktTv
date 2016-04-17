package com.azizbekian.example.adapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.azizbekian.example.R;
import com.azizbekian.example.entity.SearchItem;
import com.azizbekian.example.ui.activity.DetailMovieActivity;
import com.azizbekian.example.ui.fragment.MainFragment;
import com.azizbekian.example.utils.AndroidVersionUtils;
import com.azizbekian.example.utils.AnimationUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import static android.graphics.PorterDuff.Mode.MULTIPLY;
import static com.azizbekian.example.ui.fragment.DetailMovieFragment.TAG_MOVIE;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class MovieAdapter extends HeaderFooterRecyclerViewAdapter {

    private MainFragment mFragment;
    private Picasso mPicasso;
    private List<SearchItem.Movie> mMovies;

    public MovieAdapter(MainFragment fragment, List<SearchItem.Movie> movies, Picasso picasso) {
        mFragment = fragment;
        mMovies = movies;
        mPicasso = picasso;
    }

    public boolean isEmpty() {
        return null == mMovies || mMovies.size() == 0;
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
        return mMovies.size();
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
    protected MovieViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {
        footerViewHolder.itemView.setVisibility(mFragment.isContentLoading() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {

        final SearchItem.Movie movie = mMovies.get(position);
        final MovieViewHolder movieHolder = (MovieViewHolder) contentViewHolder;

        movieHolder.movieImdb.setOnClickListener(null);
        movieHolder.movieTrailer.setOnClickListener(null);
        movieHolder.movieContainer.setOnClickListener(null);

        movieHolder.movieTitle.setText(movie.title);
        movieHolder.movieOverview.setText(movie.overview);
        movieHolder.movieYear.setText(movie.year);
        movieHolder.movieRating.setText(movie.rating);

        // Not all movies have trailer and imdb uri
        // if this movie doesn't have both trailer uri and imdb uri, then hide the whole layout
        if (movie.getImdb() == null && movie.trailer == null) {
            movieHolder.movieLowerBar.setVisibility(View.GONE);
            movieHolder.movieLowerDivider.setVisibility(View.GONE);
        } else {
            movieHolder.movieLowerBar.setVisibility(View.VISIBLE);
            movieHolder.movieLowerDivider.setVisibility(View.VISIBLE);
            movieHolder.movieImdb.setVisibility(movie.getImdb().equals("") ? View.GONE : View.VISIBLE);
            movieHolder.movieTrailer.setVisibility(movie.trailer == null ? View.GONE : View.VISIBLE);
        }

        mPicasso.load(movie.getThumb()).error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder).into(movieHolder.movieCover);

        movieHolder.movieImdb.setOnClickListener(v -> openUri("http://www.imdb.com/title/"
                + movie.getImdb()));

        movieHolder.movieTrailer.setOnClickListener(v -> openUri(movie.trailer));

        movieHolder.movieContainer.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putParcelable(TAG_MOVIE, movie);

            if (AndroidVersionUtils.isHigherEqualToLollipop()) {
                final Pair<View, String>[] pairs = AnimationUtils
                        .createSafeTransitionParticipants(mFragment.getActivity(), false,
                                new Pair<>(movieHolder.movieCover, mFragment.getContext().getString(R.string.transition_cover)));

                DetailMovieActivity.launchActivity(mFragment.getContext(), bundle, pairs);
            } else {
                DetailMovieActivity.launchActivity(mFragment, bundle, movieHolder.movieCover);
            }
        });
    }

    private void openUri(String uri) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(uri));
        mFragment.getContext().startActivity(i);
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        View movieContainer;
        ImageView movieCover;
        TextView movieTitle;
        TextView movieOverview;
        TextView movieYear;
        TextView movieRating;
        TextView movieImdb;
        TextView movieTrailer;
        View movieLowerDivider;
        View movieLowerBar;

        public MovieViewHolder(View itemView) {
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
