package com.code.files.network.model.config;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.code.files.database.config.converters.AdsConfigConverter;
import com.code.files.database.config.converters.AdsConfigNewConverter;
import com.code.files.database.config.converters.ApkVersionInfoConverter;
import com.code.files.database.config.converters.AppConfigConverter;
import com.code.files.database.config.converters.CountryConverter;
import com.code.files.database.config.converters.GenreConverter;
import com.code.files.database.config.converters.PaymentConfigConverter;
import com.code.files.database.config.converters.TvCategoryConverter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.code.files.models.single_details.Country;
import com.code.files.models.single_details.Genre;
import com.code.files.network.model.TvCategory;

import java.util.List;

@Entity(tableName = "config_table")
public class Configuration {
    @PrimaryKey()
    @ColumnInfo(name = "app_config_id")
    private int id;

    @ColumnInfo(name = "app_config")
    @TypeConverters(AppConfigConverter.class)
    @SerializedName("app_config")
    @Expose
    private AppConfig appConfig;

    @ColumnInfo(name = "ads_config")
    @TypeConverters(AdsConfigConverter.class)
    @SerializedName("ads_config")
    @Expose
    private AdsConfig adsConfig;

    @ColumnInfo(name = "ads_config_new")
    @TypeConverters(AdsConfigNewConverter.class)
    @SerializedName("ads_config_new")
    @Expose
    private AdsConfigNew adsConfigNew;

    @ColumnInfo(name = "payment_config")
    @TypeConverters(PaymentConfigConverter.class)
    @SerializedName("payment_config")
    @Expose
    private PaymentConfig paymentConfig;

    @ColumnInfo(name = "genre")
    @TypeConverters(GenreConverter.class)
    @SerializedName("genre")
    @Expose
    private List<Genre> genre = null;

    @ColumnInfo(name = "country")
    @TypeConverters(CountryConverter.class)
    @SerializedName("country")
    @Expose
    private List<Country> country = null;

    @ColumnInfo(name = "tv_category")
    @TypeConverters(TvCategoryConverter.class)
    @SerializedName("tv_category")
    @Expose
    private List<TvCategory> tvCategory = null;

    @ColumnInfo(name = "apk_version_info")
    @TypeConverters(ApkVersionInfoConverter.class)
    @SerializedName("apk_version_info")
    @Expose
    private ApkUpdateInfo apkUpdateInfo;

    public Configuration() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AppConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public AdsConfig getAdsConfig() {
        return adsConfig;
    }

    public void setAdsConfig(AdsConfig adsConfig) {
        this.adsConfig = adsConfig;
    }

    public PaymentConfig getPaymentConfig() {
        return paymentConfig;
    }

    public void setPaymentConfig(PaymentConfig paymentConfig) {
        this.paymentConfig = paymentConfig;
    }

    public AdsConfigNew getAdsConfigNew() {
        return adsConfigNew;
    }

    public void setAdsConfigNew(AdsConfigNew adsConfigNew) {
        this.adsConfigNew = adsConfigNew;
    }

    public List<Genre> getGenre() {
        return genre;
    }

    public void setGenre(List<Genre> genre) {
        this.genre = genre;
    }

    public List<Country> getCountry() {
        return country;
    }

    public void setCountry(List<Country> country) {
        this.country = country;
    }

    public List<TvCategory> getTvCategory() {
        return tvCategory;
    }

    public void setTvCategory(List<TvCategory> tvCategory) {
        this.tvCategory = tvCategory;
    }

    public ApkUpdateInfo getApkUpdateInfo() {
        return apkUpdateInfo;
    }

    public void setApkUpdateInfo(ApkUpdateInfo apkUpdateInfo) {
        this.apkUpdateInfo = apkUpdateInfo;
    }
}
