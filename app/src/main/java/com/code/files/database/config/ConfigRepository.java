package com.code.files.database.config;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.code.files.network.model.config.Configuration;

import java.util.ArrayList;
import java.util.List;

public class ConfigRepository {
    private ConfigDao configDao;
    private Configuration configuration;

    public ConfigRepository(Application application) {
        ConfigDatabase database = ConfigDatabase.getInstance(application);
        configDao = database.configDao();
        configuration = configDao.getCongData(1);
    }

    public void insert(Configuration configuration) {
        ConfigDatabase.databaseWriteExecutor.execute(() -> {
            configDao.insertConfigData(configuration);
        });
    }

    public void update(Configuration configuration) {
        ConfigDatabase.databaseWriteExecutor.execute(()-> {
            configDao.updateConfigData(configuration);
        });
    }

    public void deleteAll(){
        ConfigDatabase.databaseWriteExecutor.execute(()->{
            configDao.deleteAllConfigData();
        });
    }

    public Configuration getConfigData(){
        return configuration;
    }
}
