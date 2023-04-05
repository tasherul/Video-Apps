package com.code.files.database.config.converters;

import androidx.room.TypeConverter;

import com.code.files.network.model.config.PaymentConfig;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class PaymentConfigConverter {
    @TypeConverter
    public static String fromArrayList(PaymentConfig paymentConfig){
        Gson gson = new Gson();
        return gson.toJson(paymentConfig);
    }
    @TypeConverter
    public static PaymentConfig jsonToList(String value){
        Type listType = new TypeToken<PaymentConfig>(){}.getType();
        Gson gson = new Gson();
        return gson.fromJson(value, listType);
    }
}
