package com.jagdish.popularmovies.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.jagdish.popularmovies.data.Movie;
import com.jagdish.popularmovies.db.AppDatabase;
import com.jagdish.popularmovies.utility.AppConstants;
import com.jagdish.popularmovies.webservices.ApiResponse;
import com.jagdish.popularmovies.webservices.RestApiClient;
import com.jagdish.popularmovies.webservices.ServiceGenerator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainViewModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<Movie>> popularMovies;
    private MutableLiveData<ArrayList<Movie>> topRatedMovies;
    private LiveData<List<Movie>> favoriteMovies;
    private AppDatabase database;
    private RestApiClient apiClient;

    public MainViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getDatabase(getApplication());
        apiClient = ServiceGenerator.createService(RestApiClient.class);
    }

    public LiveData<ArrayList<Movie>> getPopularMovies(int filterBy) {
        if (popularMovies == null) {
            popularMovies = new MutableLiveData<>();
            loadMovies(filterBy);
        }
        return popularMovies;
    }

    public LiveData<ArrayList<Movie>> getTopRatedMovies(int filterBy) {
        if (topRatedMovies == null) {
            topRatedMovies = new MutableLiveData<>();
            loadMovies(filterBy);
        }
        return topRatedMovies;
    }

    public LiveData<List<Movie>> getFavoriteMovies() {
        if (favoriteMovies == null) {
            favoriteMovies = new MutableLiveData<>();
            getFavoritesFromDatabase();
        }
        return favoriteMovies;
    }


    public void loadMovies(int filterBy) {

        if (filterBy == AppConstants.SORT_BY_POPULAR_MOVIES) {
            Call<ApiResponse<Movie>> call = apiClient.getPopularMovies();

            call.enqueue(new Callback<ApiResponse<Movie>>() {
                @Override
                public void onResponse(Call<ApiResponse<Movie>> call, Response<ApiResponse<Movie>> response) {
                    if (response.isSuccessful()) {
                        ArrayList<Movie> result = response.body().results;
                        ArrayList<Movie> value = popularMovies.getValue();
                        if (value == null || value.isEmpty()) {
                            popularMovies.setValue(result);
                        } else {
                            value.addAll(result);
                            popularMovies.setValue(value);
                        }
//                        status.setValue(0);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                    popularMovies = null;
                }
            });

        } else if (filterBy == AppConstants.SORT_BY_TOP_RATED_MOVIES) {
            Call<ApiResponse<Movie>> call = apiClient.getTopRatedMovies();

            call.enqueue(new Callback<ApiResponse<Movie>>() {
                @Override
                public void onResponse(Call<ApiResponse<Movie>> call, Response<ApiResponse<Movie>> response) {
                    if (response.isSuccessful()) {
                        if (response.isSuccessful()) {
                            ArrayList<Movie> result = response.body().results;
                            ArrayList<Movie> value = topRatedMovies.getValue();
                            if (value == null || value.isEmpty()) {
                                topRatedMovies.setValue(result);
                            } else {
                                value.addAll(result);
                                topRatedMovies.setValue(value);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<Movie>> call, Throwable t) {
                    topRatedMovies = null;
                }
            });
        }
    }

    private void getFavoritesFromDatabase() {
        favoriteMovies = database.movieDao().getFavoritesMovies();
    }
}
