package com.code.files.database.config;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.code.files.network.model.config.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Configuration.class}, exportSchema = false, version = 1)
public abstract class ConfigDatabase extends RoomDatabase {
    private static ConfigDatabase instance;
    private static final int NUMBER_OF_THREADS = 4;

    public abstract ConfigDao configDao();
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized ConfigDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ConfigDatabase.class, "config_data_db")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
