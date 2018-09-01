package com.jagdish.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jagdish.popularmovies.R;
import com.jagdish.popularmovies.data.Review;

import java.util.List;


public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {

    private static final String TAG = ReviewsAdapter.class.getName();
    private Context mContext;
    private List<Review> mReviewsList;

    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mAuthorTextView;
        public final TextView mReviewTextView;

        public ReviewsAdapterViewHolder(View view) {
            super(view);
            mAuthorTextView = view.findViewById(R.id.author);
            mReviewTextView = view.findViewById(R.id.review);
        }
    }

    public ReviewsAdapter(Context context, List<Review> reviewsList) {
        this.mContext = context;
        this.mReviewsList = reviewsList;
    }

    @Override
    public ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.review_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ReviewsAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ReviewsAdapterViewHolder reviewsAdapterViewHolder, final int position) {

        final Review review = mReviewsList.get(position);
        if (review.author != null) {
            reviewsAdapterViewHolder.mAuthorTextView.setText(review.author);
        }
        if (review.content != null) {
            reviewsAdapterViewHolder.mReviewTextView.setText(review.content);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mReviewsList) return 0;
        return mReviewsList.size();
    }

}
