package com.jagdish.popularmovies;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jagdish.popularmovies.adapter.ReviewsAdapter;
import com.jagdish.popularmovies.adapter.VideosAdapter;
import com.jagdish.popularmovies.webservices.ApiResponse;
import com.jagdish.popularmovies.data.Movie;
import com.jagdish.popularmovies.data.Review;
import com.jagdish.popularmovies.data.Video;
import com.jagdish.popularmovies.db.AppDatabase;
import com.jagdish.popularmovies.utility.AppConstants;
import com.jagdish.popularmovies.utility.ConnectionDetector;
import com.jagdish.popularmovies.webservices.RestApiClient;
import com.jagdish.popularmovies.webservices.ServiceGenerator;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getName();

    @BindView(R.id.posterImgView)
    ImageView posterImgView;

    @BindView(R.id.titleValue)
    TextView titleValue;

    @BindView(R.id.releaseDateValue)
    TextView releaseDateValue;

    @BindView(R.id.voteAverageValue)
    TextView voteAverageValue;

    @BindView(R.id.overviewValue)
    TextView overviewValue;

    @BindView(R.id.recyclerview_videos)
    RecyclerView recyclerViewVideos;

    @BindView(R.id.recyclerview_reviews)
    RecyclerView recyclerViewReviews;

    @BindView(R.id.reviewsNotFound)
    TextView reviewsNotFound;

    @BindView(R.id.trailersNotFound)
    TextView trailersNotFound;

    private boolean isFavorite = false;
    private Movie movieDetail;
    private AppDatabase mDatabase;
    private RestApiClient mRestApiClient;
    private ConnectionDetector mConnectionDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mConnectionDetector = new ConnectionDetector(this);
        mDatabase = AppDatabase.getDatabase(this);
        mRestApiClient = ServiceGenerator.createService(RestApiClient.class);

        if (getIntent().hasExtra("movieDetail")) {
            movieDetail = getIntent().getParcelableExtra("movieDetail");
            if (movieDetail != null) {

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        Movie movie = mDatabase.movieDao().getMovieById(movieDetail.getId());
                        if (movie != null) {
                            isFavorite = true;
                        } else {
                            Log.d(TAG, "This is not favorite movie");
                        }
                    }
                });
                setDetail(movieDetail);
            }
        }

        setupRecyclerView();
        if (mConnectionDetector.isConnectedToInternet()) {
            getVideos();
            getReviews();
        } else {

        }
    }

    private void setupRecyclerView() {

        LinearLayoutManager lnrLayoutManagerVideos = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerViewVideos.setLayoutManager(lnrLayoutManagerVideos);

        LinearLayoutManager lnrLayoutManagerReviews = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        recyclerViewReviews.setLayoutManager(lnrLayoutManagerReviews);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);

        MenuItem favMenuItem = menu.findItem(R.id.favorite);
        favMenuItem.setIcon(isFavorite ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to previous activity
        } else if (item.getItemId() == R.id.favorite) {
            if (isFavorite) {
                updateInDb(false);
            } else {
                updateInDb(true);
            }
            item.setIcon(isFavourite() ? R.drawable.ic_favorite_white_24dp : R.drawable.ic_favorite_border_white_24dp);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setDetail(Movie movieDetail) {

        Picasso.with(MovieDetailActivity.this).load(AppConstants.POSTER_MOVIES_BASE_URL + AppConstants.POSTER_SIZE + movieDetail.getPosterPath()).placeholder(getResources().getDrawable(R.drawable.ic_thumbnails_loading)).error(getResources().getDrawable(R.drawable.ic_thumbnails_no_image)).into(posterImgView);

        titleValue.setText(movieDetail.getTitle());
        releaseDateValue.setText(movieDetail.getReleaseDate());
        voteAverageValue.setText(String.valueOf(movieDetail.getVoteAverage()));
        overviewValue.setText(movieDetail.getOverview());

    }


    private boolean isFavourite() {
        return !isFavorite;
    }

    private void updateInDb(final boolean addToFavorite) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (addToFavorite) {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.favorite_movie_added), Snackbar.LENGTH_SHORT).show();
                    mDatabase.movieDao().insert(movieDetail);

                } else {
                    Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.favorite_movie_removed), Snackbar.LENGTH_SHORT).show();
                    mDatabase.movieDao().delete(movieDetail);
                }
            }
        });
    }

    // Get Videos
    private void getVideos() {

        Call<ApiResponse<Video>> call = mRestApiClient.getVideos(movieDetail.getId());

        call.enqueue(new Callback<ApiResponse<Video>>() {
            @Override
            public void onResponse(Call<ApiResponse<Video>> call,
                                   Response<ApiResponse<Video>> response) {
                List<Video> result = response.body().results;
                if (result.size() == 0) {
                    trailersNotFound.setVisibility(View.VISIBLE);
                    recyclerViewVideos.setVisibility(View.GONE);
                } else {
                    trailersNotFound.setVisibility(View.GONE);
                    recyclerViewVideos.setVisibility(View.VISIBLE);
                    recyclerViewVideos.setAdapter(new VideosAdapter(MovieDetailActivity.this, result));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Video>> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this,
                        getString(R.string.unable_to_fetch_trailers), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Get Reviews
    private void getReviews() {

        Call<ApiResponse<Review>> call = mRestApiClient.getReviews(movieDetail.getId());

        call.enqueue(new Callback<ApiResponse<Review>>() {
            @Override
            public void onResponse(Call<ApiResponse<Review>> call,
                                   Response<ApiResponse<Review>> response) {
                List<Review> result = response.body().results;
                if (result.size() == 0) {
                    reviewsNotFound.setVisibility(View.VISIBLE);
                    recyclerViewReviews.setVisibility(View.GONE);
                } else {
                    reviewsNotFound.setVisibility(View.GONE);
                    recyclerViewReviews.setVisibility(View.VISIBLE);
                    recyclerViewReviews.setAdapter(new ReviewsAdapter(MovieDetailActivity.this, result));

                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Review>> call, Throwable t) {
                Toast.makeText(MovieDetailActivity.this,
                        getString(R.string.unable_to_fetch_reviews), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
