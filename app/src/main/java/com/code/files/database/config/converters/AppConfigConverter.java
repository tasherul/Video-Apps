package com.code.files.database.config.converters;

import android.content.res.TypedArray;

import androidx.room.TypeConverter;

import com.code.files.network.model.config.AppConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class AppConfigConverter {
    @androidx.room.TypeConverter
    public static String fromArrayList(AppConfig appConfig){
        Gson gson = new Gson();
        return gson.toJson(appConfig);
    }
    @TypeConverter
    public static AppConfig jsonToList(String value){
        Type listType = new TypeToken<AppConfig>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }

}
