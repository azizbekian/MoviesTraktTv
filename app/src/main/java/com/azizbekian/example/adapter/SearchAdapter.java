package com.azizbekian.example.adapter;

import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.azizbekian.example.entity.SearchItem;
import com.azizbekian.example.manager.SearchHelper;
import com.azizbekian.example.ui.activity.DetailMovieActivity;
import com.azizbekian.example.ui.fragment.DetailMovieFragment;
import com.azizbekian.example.ui.fragment.MainFragment;
import com.azizbekian.example.utils.AndroidVersionUtils;
import com.azizbekian.example.utils.AnimationUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.azizbekian.example.R;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class SearchAdapter extends HeaderFooterRecyclerViewAdapter {

    private SearchHelper mSearchHelper;
    private MainFragment mHostFragment;
    private List<SearchItem> mData;
    private Picasso mPicasso;

    public SearchAdapter(SearchHelper searchHelper, MainFragment hostFragment, List<SearchItem> data, Picasso picasso) {
        mSearchHelper = searchHelper;
        mHostFragment = hostFragment;
        mData = data;
        mPicasso = picasso;
    }

    public void setItems(List<SearchItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItems(List<SearchItem> data) {
        mData.addAll(data);
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return mData == null || mData.size() == 0;
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
    protected RecyclerView.ViewHolder onCreateFooterItemViewHolder(ViewGroup parent, int footerViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    protected SearchItemHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_movie_search, parent, false);
        return new SearchItemHolder(view);
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {

    }

    @Override
    protected void onBindFooterItemViewHolder(RecyclerView.ViewHolder footerViewHolder, int position) {
        footerViewHolder.itemView.setVisibility(mSearchHelper.isContentLoading() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        final SearchItemHolder searchItemHolder = (SearchItemHolder) contentViewHolder;
        final SearchItem searchItem = mData.get(position);
        searchItemHolder.movieTitle.setText(searchItem.movie.title);
        searchItemHolder.movieOverview.setText(searchItem.movie.overview);
        searchItemHolder.movieYear.setText(searchItem.movie.year);
        mPicasso.load(searchItem.movie.getThumb()).error(R.drawable.placeholder).placeholder(R.drawable.placeholder).into(searchItemHolder.movieCover);

        searchItemHolder.searchContainer.setOnClickListener(v -> {

            Log.i("vvv", searchItem.movie.toString());
            Bundle bundle = new Bundle();
            bundle.putParcelable(DetailMovieFragment.TAG_MOVIE, searchItem.movie);
            if (AndroidVersionUtils.isHigherEqualToLollipop()) {
                final Pair<View, String>[] pairs = AnimationUtils.createSafeTransitionParticipants(mHostFragment.getActivity(), false,
                        new Pair<>(searchItemHolder.movieTitle, mHostFragment.getContext().getString(R.string.transition_title)),
                        new Pair<>(searchItemHolder.movieOverview, mHostFragment.getContext().getString(R.string.transition_overview)),
                        new Pair<>(searchItemHolder.movieCover, mHostFragment.getContext().getString(R.string.transition_cover)));

                DetailMovieActivity.launchActivity(mHostFragment.getContext(), bundle, pairs);
            } else {
                DetailMovieActivity.launchActivity(mHostFragment, bundle, searchItemHolder.movieCover);
            }
        });
    }

    public static class SearchItemHolder extends RecyclerView.ViewHolder {

        View searchContainer;
        ImageView movieCover;
        TextView movieTitle;
        TextView movieOverview;
        TextView movieYear;

        public SearchItemHolder(View itemView) {
            super(itemView);

            searchContainer = itemView.findViewById(R.id.search_container);
            movieCover = (ImageView) itemView.findViewById(R.id.search_cover);
            movieTitle = (TextView) itemView.findViewById(R.id.search_title);
            movieOverview = (TextView) itemView.findViewById(R.id.search_overview);
            movieYear = (TextView) itemView.findViewById(R.id.search_year);
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }

}
