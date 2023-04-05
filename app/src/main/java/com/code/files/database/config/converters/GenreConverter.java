package com.code.files.database.config.converters;

import androidx.room.TypeConverter;


import com.code.files.models.single_details.Genre;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class GenreConverter {
    @TypeConverter
    public static String fromArrayList(List<Genre> list){
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    @TypeConverter
    public static List<Genre> jsonToList(String value){
        Type listType = new TypeToken<List<Genre>>() {}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
