package com.jagdish.popularmovies.webservices;

import com.jagdish.popularmovies.data.Movie;
import com.jagdish.popularmovies.data.Review;
import com.jagdish.popularmovies.data.Video;
import com.jagdish.popularmovies.utility.AppConstants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestApiClient {

    @GET(AppConstants.ACTION_TOP_RATED_MOVIES)
    Call<ApiResponse<Movie>> getTopRatedMovies();

    @GET(AppConstants.ACTION_POPULAR_MOVIES)
    Call<ApiResponse<Movie>> getPopularMovies();

    //
    @GET(AppConstants.ACTION_GET_REVIEWS)
    Call<ApiResponse<Review>> getReviews(@Path("id") String id);

    @GET(AppConstants.ACTION_GET_VIDEOS)
    Call<ApiResponse<Video>> getVideos(@Path("id") String id);

}