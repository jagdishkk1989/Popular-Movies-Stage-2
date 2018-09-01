package com.jagdish.popularmovies;

import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jagdish.popularmovies.adapter.GridSpacingItemDecoration;
import com.jagdish.popularmovies.adapter.MoviesAdapter;
import com.jagdish.popularmovies.data.Movie;
import com.jagdish.popularmovies.utility.AppConstants;
import com.jagdish.popularmovies.utility.ConnectionDetector;
import com.jagdish.popularmovies.utility.SessionManager;
import com.jagdish.popularmovies.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @BindView(R.id.recyclerview_movies)
    RecyclerView mRecyclerView;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    private CoordinatorLayout mCoordinatorLayout;
    private GridLayoutManager mGridLayoutManager;
    private MainViewModel mViewModel;

    private static final String BUNDLE_MOVIES_LIST = "moviesList";
    private static final String BUNDLE_GRID_SCROLL_POSITION = "gridScrollPos";
    private ConnectionDetector mConnectionDetector;

    private ArrayList<Movie> moviesList;
    private int recentScrollPos = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupRecyclerView();
        setTitle();

        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        mConnectionDetector = new ConnectionDetector(getApplicationContext());

        if (savedInstanceState == null) {
            int filterBy = SessionManager.getIntegerSharedPrefs(SessionManager.SORT_ORDER_BY);
            if (filterBy == AppConstants.SORT_BY_POPULAR_MOVIES || filterBy == AppConstants.SORT_BY_TOP_RATED_MOVIES) {
                if (mConnectionDetector.isConnectedToInternet()) {
                    bindData();
                } else {
                    showNoInternetDialog();
                }
            } else if (filterBy == AppConstants.SORT_BY_FAVORITE_MOVIES) {
                bindData();
            }
        } else {
            moviesList = savedInstanceState.getParcelableArrayList(BUNDLE_MOVIES_LIST);
            recentScrollPos = savedInstanceState.getInt(BUNDLE_GRID_SCROLL_POSITION);
            setAdapter(moviesList);
            mRecyclerView.scrollToPosition(recentScrollPos);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setupRecyclerView() {

        mGridLayoutManager = new GridLayoutManager(this, 3);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        int spanCount = 3; // 3 columns
        int spacing = 10; // 10px
        boolean includeEdge = false;
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        // set empty adapter
        moviesList = new ArrayList<Movie>();
        mRecyclerView.setAdapter(new MoviesAdapter(MainActivity.this, moviesList));
    }

    private void setTitle() {
        if (getSupportActionBar() != null) {
            if (SessionManager.getIntegerSharedPrefs(SessionManager.SORT_ORDER_BY) == AppConstants.SORT_BY_POPULAR_MOVIES) {
                getSupportActionBar().setTitle(R.string.lbl_popular_movies);
            } else if (SessionManager.getIntegerSharedPrefs(SessionManager.SORT_ORDER_BY) == AppConstants.SORT_BY_TOP_RATED_MOVIES) {
                getSupportActionBar().setTitle(R.string.lbl_top_rated_movies);
            } else {
                getSupportActionBar().setTitle(R.string.lbl_favorites_movies);
            }
        }
    }

    private void showNoInternetDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(getResources().getString(R.string.no_internet_connection));
        builder.setMessage(getResources().getString(R.string.message_no_internet));

        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(BUNDLE_MOVIES_LIST, moviesList);
        outState.putInt(BUNDLE_GRID_SCROLL_POSITION, ((GridLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showOrderByDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showOrderByDialog() {
        AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = getLayoutInflater().inflate(R.layout.dialog_sort_order, null);
        final RadioGroup rdoGroupOrderBy = layout.findViewById(R.id.rdoGroupOrderBy);
        final RadioButton rdoBtnPopularMovies = layout.findViewById(R.id.rdoBtnPopularMovies);
        final RadioButton rdoBtnTopRatedMovies = layout.findViewById(R.id.rdoBtnTopRatedMovies);
        final RadioButton rdoBtnFavoriteMovies = layout.findViewById(R.id.rdoBtnFavoriteMovies);

        int prefsValue = SessionManager.getIntegerSharedPrefs(SessionManager.SORT_ORDER_BY);
        if (prefsValue == AppConstants.SORT_BY_POPULAR_MOVIES) {
            rdoBtnPopularMovies.setChecked(true);
        } else if (prefsValue == AppConstants.SORT_BY_TOP_RATED_MOVIES) {
            rdoBtnTopRatedMovies.setChecked(true);
        } else if (prefsValue == AppConstants.SORT_BY_FAVORITE_MOVIES) {
            rdoBtnFavoriteMovies.setChecked(true);
        }

        builder.setCancelable(false);
        builder.setTitle(getResources().getString(R.string.sort_order_by));
        builder.setView(layout);
        builder.setPositiveButton(getResources().getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int filterBy = 0;
                int checkedId = rdoGroupOrderBy.getCheckedRadioButtonId();
                if (checkedId == rdoBtnPopularMovies.getId()) {
                    filterBy = AppConstants.SORT_BY_POPULAR_MOVIES;
                } else if (checkedId == rdoBtnTopRatedMovies.getId()) {
                    filterBy = AppConstants.SORT_BY_TOP_RATED_MOVIES;
                } else if (checkedId == rdoBtnFavoriteMovies.getId()) {
                    filterBy = AppConstants.SORT_BY_FAVORITE_MOVIES;
                }
                dialogInterface.dismiss();

                boolean canProceed = true;
                if (filterBy != AppConstants.SORT_BY_FAVORITE_MOVIES) {
                    if (mConnectionDetector.isConnectedToInternet()) {
                        canProceed = true;
                    } else {
                        canProceed = false;
                        showNoInternetDialog();
                    }
                }

                if (canProceed) {
                    SessionManager.setIntegerSharedPrefs(SessionManager.SORT_ORDER_BY, filterBy);
                    setTitle();
                    bindData();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void bindData() {
        final int filterBy = SessionManager.getIntegerSharedPrefs(SessionManager.SORT_ORDER_BY);

        if (filterBy == AppConstants.SORT_BY_POPULAR_MOVIES) {
            mViewModel.getPopularMovies(filterBy).observe(MainActivity.this,
                    new Observer<ArrayList<Movie>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<Movie> movies) {
                            setAdapter(movies);
                        }
                    });
        } else if (filterBy == AppConstants.SORT_BY_TOP_RATED_MOVIES) {
            mViewModel.getTopRatedMovies(filterBy).observe(MainActivity.this,
                    new Observer<ArrayList<Movie>>() {
                        @Override
                        public void onChanged(@Nullable ArrayList<Movie> movies) {
                            setAdapter(movies);
                        }
                    });
        } else if (filterBy == AppConstants.SORT_BY_FAVORITE_MOVIES) {
            mViewModel.getFavoriteMovies().observe(MainActivity.this,
                    new Observer<List<Movie>>() {
                        @Override
                        public void onChanged(@Nullable List<Movie> movies) {
                            // check condition again due to it coming on this method while change something on detail page like add to favorite OR remove (Reason is Observer check on change)
                            int currentOrder = SessionManager.getIntegerSharedPrefs(SessionManager.SORT_ORDER_BY);
                            if (currentOrder == AppConstants.SORT_BY_FAVORITE_MOVIES) {
                                if (movies != null) {
                                    ArrayList<Movie> tempResults = new ArrayList<>(movies);
                                    setAdapter(tempResults);
                                }
                            }
                        }
                    });
        } else {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.invalid_options), Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    private void setAdapter(ArrayList<Movie> moviesResult) {
        if (moviesResult != null && moviesResult.size() == 0) {
            Snackbar snackbar = Snackbar.make(mCoordinatorLayout, getResources().getString(R.string.no_movies_found), Snackbar.LENGTH_LONG);
            snackbar.show();
            //set empty list to refresh view
            this.moviesList = new ArrayList<>();
            mRecyclerView.setAdapter(new MoviesAdapter(MainActivity.this, moviesList));
        } else {
            if (moviesResult != null) {
                this.moviesList = moviesResult;
                mRecyclerView.setAdapter(new MoviesAdapter(MainActivity.this, moviesList));
            }
        }

    }

}
