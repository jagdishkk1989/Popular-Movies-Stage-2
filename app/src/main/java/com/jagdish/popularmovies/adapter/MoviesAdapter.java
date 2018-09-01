package com.jagdish.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jagdish.popularmovies.MovieDetailActivity;
import com.jagdish.popularmovies.R;
import com.jagdish.popularmovies.data.Movie;
import com.jagdish.popularmovies.utility.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String TAG = MoviesAdapter.class.getName();
    private Context mContext;
    private List<Movie> movieDetailList;

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mPosterImageView;
        public final TextView mMovieName;

        public MoviesAdapterViewHolder(View view) {
            super(view);
            mPosterImageView = view.findViewById(R.id.movie_thumbnail);
            mMovieName = view.findViewById(R.id.movie_name);
        }
    }

    public MoviesAdapter(Context context, List<Movie> movieDetailList) {
        this.mContext = context;
        this.movieDetailList = movieDetailList;
    }

    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movies_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MoviesAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MoviesAdapterViewHolder moviesAdapterViewHolder, final int position) {

        final Movie movie = movieDetailList.get(position);
        String posterPath = AppConstants.POSTER_MOVIES_BASE_URL + AppConstants.POSTER_SIZE + movie.getPosterPath();

        Picasso.with(mContext).load(posterPath).placeholder(mContext.getResources().getDrawable(R.drawable.ic_thumbnails_loading)).error(mContext.getResources().getDrawable(R.drawable.ic_thumbnails_no_image)).into(moviesAdapterViewHolder.mPosterImageView);

        if (movie.getTitle() != null) {
            moviesAdapterViewHolder.mMovieName.setText(movie.getTitle());
        }

        moviesAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MovieDetailActivity.class);
                intent.putExtra("movieDetail", movieDetailList.get(position));
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (null == movieDetailList) return 0;
        return movieDetailList.size();
    }

}
