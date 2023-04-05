package com.code.files.database.config.converters;

import androidx.room.TypeConverter;

import com.code.files.network.model.config.ApkUpdateInfo;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class ApkVersionInfoConverter {
    @TypeConverter
    public static String fromArrayList(ApkUpdateInfo adsConfig){
        Gson gson = new Gson();
        return gson.toJson(adsConfig);
    }
    @TypeConverter
    public static ApkUpdateInfo jsonToList(String value){
        Type listType = new TypeToken<ApkUpdateInfo>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
