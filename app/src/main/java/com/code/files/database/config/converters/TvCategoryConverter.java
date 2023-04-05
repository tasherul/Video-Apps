package com.code.files.database.config.converters;

import androidx.room.TypeConverter;

import com.code.files.network.model.TvCategory;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class TvCategoryConverter {
    @TypeConverter
    public static String fromArrayList(List<TvCategory> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<TvCategory> jsonToList(String value){
        Type listType = new TypeToken<List<TvCategory>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
