package com.jagdish.popularmovies.adapter;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jagdish.popularmovies.R;
import com.jagdish.popularmovies.data.Video;
import com.jagdish.popularmovies.utility.AppConstants;
import com.squareup.picasso.Picasso;

import java.util.List;


public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosAdapterViewHolder> {

    private static final String TAG = VideosAdapter.class.getName();
    private Context mContext;
    private List<Video> mVideosList;

    public class VideosAdapterViewHolder extends RecyclerView.ViewHolder {
        public final ImageView mThumbnailImageView;
        public final TextView mVideoNameTextView;

        public VideosAdapterViewHolder(View view) {
            super(view);
            mThumbnailImageView = view.findViewById(R.id.video_thumbnail);
            mVideoNameTextView = view.findViewById(R.id.video_name);
        }
    }

    public VideosAdapter(Context context, List<Video> videosList) {
        this.mContext = context;
        this.mVideosList = videosList;
    }

    @Override
    public VideosAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.video_trailer_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new VideosAdapterViewHolder(view);
    }


    @Override
    public void onBindViewHolder(VideosAdapterViewHolder videosAdapterViewHolder, final int position) {

        final Video video = mVideosList.get(position);
        if (video.videoName != null) {
            videosAdapterViewHolder.mVideoNameTextView.setText(video.videoName);
        }
        if (video.videoUrl != null) {
            String thumbnails = String.format(AppConstants.YOUTUBE_THUMBNAILS_BASE_URL, video.videoUrl);
            Picasso.with(mContext).load(thumbnails).placeholder(mContext.getResources().getDrawable(R.drawable.ic_thumbnails_loading)).error(mContext.getResources().getDrawable(R.drawable.ic_thumbnails_no_image)).into(videosAdapterViewHolder.mThumbnailImageView);
        }

        videosAdapterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (video.videoUrl != null) {
                    onClickVideo(video.videoUrl);
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.unable_to_view_video), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onClickVideo(String videoUrl) {
        Intent appIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("vnd.youtube:" + videoUrl)); // get youtube app action

        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse(AppConstants.YOUTUBE_VIDEO_BASE_URL + videoUrl)); // get youtube weburl app action
        try {
            mContext.startActivity(appIntent);
        } catch (ActivityNotFoundException ex) {
            mContext.startActivity(webIntent);
        }
    }

    @Override
    public int getItemCount() {
        if (null == mVideosList) return 0;
        return mVideosList.size();
    }

}
