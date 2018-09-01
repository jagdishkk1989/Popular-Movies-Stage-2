package com.jagdish.popularmovies.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.jagdish.popularmovies.data.Movie;

@Database(entities = {Movie.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase appDbInstance;

    public static AppDatabase getDatabase(Context context) {
        if (appDbInstance == null) {
            synchronized (AppDatabase.class) {
                if (appDbInstance == null) {
                    appDbInstance = Room.databaseBuilder(context,
                            AppDatabase.class, "popularmovies")
                            .build();
                }
            }
        }
        return appDbInstance;
    }

    public abstract MovieDao movieDao();
}