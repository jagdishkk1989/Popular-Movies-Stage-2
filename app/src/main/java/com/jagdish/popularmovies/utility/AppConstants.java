package com.jagdish.popularmovies.utility;


public class AppConstants {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String ACTION_POPULAR_MOVIES = "movie/popular?";
    public static final String ACTION_TOP_RATED_MOVIES = "movie/top_rated?";
    public static final String ACTION_GET_REVIEWS = "movie/{id}/reviews";
    public static final String ACTION_GET_VIDEOS = "movie/{id}/videos";

    public static final String POSTER_MOVIES_BASE_URL = "http://image.tmdb.org/t/p/";
    public static final String POSTER_SIZE = "w300";

    public static final String API_KEY_PARAM = "api_key";

    public static final String YOUTUBE_THUMBNAILS_BASE_URL = "https://img.youtube.com/vi/%s/0.jpg";
    public static final String YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v=";

    public static final  int SORT_BY_POPULAR_MOVIES = 1;
    public static final int SORT_BY_TOP_RATED_MOVIES = 2;
    public static final int SORT_BY_FAVORITE_MOVIES = 3;
}
