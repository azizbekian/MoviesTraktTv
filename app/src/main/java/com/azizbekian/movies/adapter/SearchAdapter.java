package com.azizbekian.movies.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.azizbekian.movies.R;
import com.azizbekian.movies.entity.SearchItem;
import com.azizbekian.movies.fragment.movies.MoviesContract;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.azizbekian.movies.MoviesApplication.getAppComponent;

/**
 * Created on April 02, 2016.
 *
 * @author Andranik Azizbekian (azizbekyanandranik@gmail.com)
 */
public class SearchAdapter extends HeaderFooterRecyclerViewAdapter {

    private MoviesContract.Presenter mPresenter;
    private List<SearchItem> mData;
    private Picasso mPicasso;

    public SearchAdapter(MoviesContract.Presenter presenter, List<SearchItem> data) {
        mPresenter = presenter;
        mData = data;
        mPicasso = getAppComponent().getPicasso();
    }

    public void setItems(List<SearchItem> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItems(List<SearchItem> data) {
        mData.addAll(data);
        notifyDataSetChanged();
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
        footerViewHolder.itemView.setVisibility(!mPresenter.isSearchModeIdle() && !mPresenter.isSearchAdapterEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {

        final SearchItemHolder searchItemHolder = (SearchItemHolder) contentViewHolder;
        final SearchItem searchItem = mData.get(position);

        searchItemHolder.movieTitle.setText(searchItem.movie.title);
        searchItemHolder.movieOverview.setText(searchItem.movie.overview);
        searchItemHolder.movieYear.setText(searchItem.movie.year);

        mPicasso.load(searchItem.movie.getThumb())
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(searchItemHolder.movieCover);

        searchItemHolder.searchContainer.setOnClickListener(v ->
                mPresenter.launchDetailMovieActivity(searchItemHolder.movieCover, searchItem.movie));
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);

        if (holder instanceof SearchItemHolder) {
            SearchItemHolder searchItemHolder = (SearchItemHolder) holder;
            searchItemHolder.searchContainer.setOnClickListener(null);
        }
    }

    public boolean isEmpty() {
        return mData == null || mData.size() == 0;
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
