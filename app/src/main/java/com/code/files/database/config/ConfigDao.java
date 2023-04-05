package com.code.files.database.config;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.code.files.network.model.config.Configuration;

import java.util.List;

@Dao
public interface ConfigDao {

    @Query("SELECT * FROM config_table WHERE app_config_id=:id")
    Configuration getCongData(int id);

    @Insert(onConflict = REPLACE)
    void insertConfigData(Configuration configuration);

    @Update
    void updateConfigData(Configuration configuration);

    @Query("DELETE FROM config_table")
    void deleteAllConfigData();
}
