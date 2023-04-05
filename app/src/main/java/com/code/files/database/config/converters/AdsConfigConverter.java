package com.code.files.database.config.converters;

import androidx.room.TypeConverter;

import com.code.files.network.model.config.AdsConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class AdsConfigConverter {
    @TypeConverter
    public static String fromArrayList(AdsConfig adsConfig){
        Gson gson = new Gson();
        return gson.toJson(adsConfig);
    }
    @TypeConverter
    public static AdsConfig jsonToList(String value){
        Type listType = new TypeToken<AdsConfig>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
