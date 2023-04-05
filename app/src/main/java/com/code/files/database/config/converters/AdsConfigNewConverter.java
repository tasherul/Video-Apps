package com.code.files.database.config.converters;

import androidx.room.TypeConverter;

import com.code.files.network.model.config.AdsConfigNew;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class AdsConfigNewConverter {
    @TypeConverter
    public static String fromArrayList(AdsConfigNew adsConfig){
        Gson gson = new Gson();
        return gson.toJson(adsConfig);
    }
    @TypeConverter
    public static AdsConfigNew jsonToList(String value){
        Type listType = new TypeToken<AdsConfigNew>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
