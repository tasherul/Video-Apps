package com.code.files.network.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AdsConfigNew {

    @SerializedName("reward_ad")
    @Expose
    private String rewordAd;
    @SerializedName("reward_ad_id")
    @Expose
    private String rewordAdId;
    @SerializedName("banner_ad")
    @Expose
    private String bannerAd;
    @SerializedName("banner_ad_id")
    @Expose
    private String bannerAdId;
    @SerializedName("interstitial_ad")
    @Expose
    private String interstitialAd;
    @SerializedName("interstitial_ad_id")
    @Expose
    private String interstitialAdId;
    @SerializedName("interstitial_ad_interval")
    @Expose
    private String interstitialAdInterval;
    @SerializedName("native_ad")
    @Expose
    private String nativeAd;
    @SerializedName("native_ad_id")
    @Expose
    private String nativeAdId;
    @SerializedName("native_ad_interval")
    @Expose
    private String nativeAdInterval;
    @SerializedName("unity_android_game_id")
    @Expose
    private String unityGameId;
    @SerializedName("unity_test_mode")
    @Expose
    private boolean unityTestMode;

    public String getRewordAd() {
        return rewordAd;
    }

    public void setRewordAd(String rewordAd) {
        this.rewordAd = rewordAd;
    }

    public String getRewordAdId() {
        return rewordAdId;
    }

    public void setRewordAdId(String rewordAdId) {
        this.rewordAdId = rewordAdId;
    }

    public String getBannerAd() {
        return bannerAd;
    }

    public void setBannerAd(String bannerAd) {
        this.bannerAd = bannerAd;
    }

    public String getBannerAdId() {
        return bannerAdId;
    }

    public void setBannerAdId(String bannerAdId) {
        this.bannerAdId = bannerAdId;
    }

    public String getInterstitialAd() {
        return interstitialAd;
    }

    public void setInterstitialAd(String interstitialAd) {
        this.interstitialAd = interstitialAd;
    }

    public String getInterstitialAdId() {
        return interstitialAdId;
    }

    public void setInterstitialAdId(String interstitialAdId) {
        this.interstitialAdId = interstitialAdId;
    }

    public String getInterstitialAdInterval() {
        return interstitialAdInterval;
    }

    public void setInterstitialAdInterval(String interstitialAdInterval) {
        this.interstitialAdInterval = interstitialAdInterval;
    }

    public String getNativeAd() {
        return nativeAd;
    }

    public void setNativeAd(String nativeAd) {
        this.nativeAd = nativeAd;
    }

    public String getNativeAdId() {
        return nativeAdId;
    }

    public void setNativeAdId(String nativeAdId) {
        this.nativeAdId = nativeAdId;
    }

    public String getNativeAdInterval() {
        return nativeAdInterval;
    }

    public void setNativeAdInterval(String nativeAdInterval) {
        this.nativeAdInterval = nativeAdInterval;
    }

    public String getUnityGameId() {
        return unityGameId;
    }

    public void setUnityGameId(String unityGameId) {
        this.unityGameId = unityGameId;
    }

    public boolean isUnityTestMode() {
        return unityTestMode;
    }

    public void setUnityTestMode(boolean unityTestMode) {
        this.unityTestMode = unityTestMode;
    }
}