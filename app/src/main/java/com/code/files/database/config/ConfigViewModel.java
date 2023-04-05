package com.code.files.database.config;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.code.files.network.model.config.Configuration;

import java.util.ArrayList;
import java.util.List;


public class ConfigViewModel extends AndroidViewModel {
    private ConfigRepository repository;
    private Configuration configuration;

    public ConfigViewModel( Application application){
        super(application);
        repository = new ConfigRepository(application);
        configuration = repository.getConfigData();
    }

    public void insert(Configuration configuration){
        repository.insert(configuration);
    }

    public void update(Configuration configuration){
        repository.update(configuration);
    }

    public void deleteAll(){
        repository.deleteAll();
    }


    public Configuration getConfigData(){
         return configuration;
    }

}
