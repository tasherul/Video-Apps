package com.code.files.utils.ads;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.nativeAds.MaxNativeAdListener;
import com.applovin.mediation.nativeAds.MaxNativeAdLoader;
import com.applovin.mediation.nativeAds.MaxNativeAdView;
import com.code.files.StripePaymentActivity;
import com.code.files.database.DatabaseHelper;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.network.model.config.Configuration;
import com.code.files.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.facebook.ads.NativeBannerAdView;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.nativead.NativeAd;
import com.oxoo.spagreen.R;
import com.code.files.utils.PreferenceUtils;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class NativeAds {
    private static final String TAG = NativeAds.class.getSimpleName();
    private static com.google.android.gms.ads.nativead.NativeAd ad = null;

    private Activity activity;
    private LifecycleOwner lifecycleOwner;
    private TemplateView templateView;
    private RelativeLayout relativeLayout;
    private ConfigViewModel configViewModel;
    private Configuration configData;

    public NativeAds(Activity activity,LifecycleOwner lifecycleOwner, TemplateView templateView, RelativeLayout relativeLayout, ConfigViewModel configViewModel) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        this.templateView = templateView;
        this.relativeLayout = relativeLayout;
        this.configViewModel = configViewModel;
        templateView.setVisibility(View.GONE);
        configData = configViewModel.getConfigData();
    }

    public void showNativeAds() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            if (configData.getAdsConfigNew().getNativeAd().equalsIgnoreCase(Constants.ADMOB)) {
                showAdmobNativeAds();
            }else if (configData.getAdsConfigNew().getNativeAd().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)){
                templateView.setVisibility(View.GONE);
                ad = null;
                showFANNativeBannerAd();
            }else if (configData.getAdsConfigNew().getNativeAd().equalsIgnoreCase(Constants.APP_LOVIN)){
                templateView.setVisibility(View.GONE);
                ad = null;
                showAppLovinNativeAd();
            }
        }
    }

    private MaxNativeAdLoader nativeAdLoader;
    private MaxAd nativeAd;

    private void showAppLovinNativeAd() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            nativeAdLoader = new MaxNativeAdLoader(configData.getAdsConfigNew().getNativeAdId(), activity);
            nativeAdLoader.setNativeAdListener(new MaxNativeAdListener() {
                @Override
                public void onNativeAdLoaded(@Nullable MaxNativeAdView maxNativeAdView, MaxAd maxAd) {
                    super.onNativeAdLoaded(maxNativeAdView, maxAd);
                    if (nativeAd != null){
                        nativeAdLoader.destroy(nativeAd);
                    }
                    nativeAd = maxAd;
                    relativeLayout.setVisibility(View.VISIBLE);
                    relativeLayout.removeAllViews();
                    relativeLayout.addView(maxNativeAdView);
                }

                @Override
                public void onNativeAdLoadFailed(String s, MaxError maxError) {
                    super.onNativeAdLoadFailed(s, maxError);
                }

                @Override
                public void onNativeAdClicked(MaxAd maxAd) {
                    super.onNativeAdClicked(maxAd);
                }
            });

            nativeAdLoader.loadAd();
        }
    }

    private void showAdmobNativeAds() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            if (configData.getAdsConfigNew().getNativeAd().equalsIgnoreCase(Constants.ADMOB)) {
                String nativeAdId = configData.getAdsConfigNew().getNativeAdId();
                MobileAds.initialize(activity);
                AdLoader adLoader = new AdLoader.Builder(activity, nativeAdId)
                        .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            public void onNativeAdLoaded(@NonNull @NotNull NativeAd nativeAd) {
                                templateView.setVisibility(View.VISIBLE);
                                ad = nativeAd;
                                templateView.setNativeAd(nativeAd);
                            }
                        }).build();
                adLoader.loadAd(new AdRequest.Builder().build());
            } else {
                templateView.setVisibility(View.GONE);
            }
        } else {
            templateView.setVisibility(View.GONE);
        }
    }

    public static void releaseAdmobNativeAd() {
        if (ad != null) {
            ad.destroy();
            Log.e(TAG, "Admob Native ad destroyed");
        }
    }

    private void showFANNativeBannerAd() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            String nativeAdId = configData.getAdsConfigNew().getNativeAdId();
            NativeBannerAd nativeBannerAd =
                    new NativeBannerAd(activity, nativeAdId);
            NativeAdListener listener = new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {

                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    Log.e(TAG, "FAN Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    View adView = NativeBannerAdView.render(activity, nativeBannerAd,
                            NativeBannerAdView.Type.HEIGHT_100);
                    relativeLayout.addView(adView);
                }

                @Override
                public void onAdClicked(Ad ad) {

                }

                @Override
                public void onLoggingImpression(Ad ad) {

                }
            };

            //initiate a request to load an ad
            nativeBannerAd.loadAd(
                    nativeBannerAd.buildLoadAdConfig()
                            .withAdListener(listener)
                            .build());
        }
    }
}
