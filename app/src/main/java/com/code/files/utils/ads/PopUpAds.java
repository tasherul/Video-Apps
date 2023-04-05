package com.code.files.utils.ads;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.code.files.database.DatabaseHelper;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.network.model.config.AdsConfig;
import com.code.files.network.model.config.AdsConfigNew;
import com.code.files.utils.Constants;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.code.files.utils.PreferenceUtils;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;

import org.jetbrains.annotations.NotNull;

public class PopUpAds {
    static InterstitialAd mInterstitial;
    private Activity activity;
    private LifecycleOwner lifecycleOwner;
    private ConfigViewModel configViewModel;

    public PopUpAds(Activity activity, LifecycleOwner lifecycleOwner, ConfigViewModel configViewModel) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        this.configViewModel = configViewModel;
    }

    public void showInterstitialAd() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();
            if (adsConfig.getInterstitialAd().equalsIgnoreCase(Constants.ADMOB)) {
                showAdmobInterstitialAds();
            } else if (adsConfig.getInterstitialAd().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)) {
                showFANInterstitialAds();
            } else if (adsConfig.getInterstitialAd().equalsIgnoreCase(Constants.APP_LOVIN)) {
                showAppLovinInterstitial();
            }else if (adsConfig.getInterstitialAd().equalsIgnoreCase(Constants.UNITY)){
                showUnityInterstitialAd();
            }
        }
    }


    MaxInterstitialAd maxInterstitialAd;
    int retryAttempt;

    private void showAppLovinInterstitial() {
        maxInterstitialAd = new MaxInterstitialAd(configViewModel.getConfigData().getAdsConfigNew().getInterstitialAdId(), activity);
        maxInterstitialAd.setListener(new MaxAdListener() {
            @Override
            public void onAdLoaded(MaxAd ad) {
                retryAttempt = 0;
//                if (maxInterstitialAd.isReady()){
//                    maxInterstitialAd.showAd();
//                }
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                maxInterstitialAd.loadAd();
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                maxInterstitialAd.loadAd();
            }
        });
        maxInterstitialAd.loadAd();
        if (!PreferenceUtils.isActivePlan(activity)) {
            if (maxInterstitialAd.isReady()) {
                maxInterstitialAd.showAd();
            }
        }
    }

    private void showAdmobInterstitialAds() {
        AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(activity, adsConfig.getInterstitialAdId(), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull @NotNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);
                mInterstitial = interstitialAd;
                mInterstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull @NotNull com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitial = null;
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        Log.e("Admob", "onAdShowedFullScreenContent: ");
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        mInterstitial = null;
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        Log.e("Admob", "onAdImpression: ");
                    }
                });
                if (!PreferenceUtils.isActivePlan(activity)) {
                    mInterstitial.show(activity);
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull @NotNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                mInterstitial = null;
            }
        });

    }

    private void showFANInterstitialAds() {
        final String TAG = "FAN";
        AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();
        final com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(activity, adsConfig.getInterstitialAdId());
        InterstitialAdListener listener = new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e(TAG, "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e(TAG, "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e(TAG, "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d(TAG, "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                if (!PreferenceUtils.isActivePlan(activity)) {
                    interstitialAd.show();
                }
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d(TAG, "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d(TAG, "Interstitial ad impression logged!");
            }
        };

        interstitialAd.loadAd(interstitialAd.buildLoadAdConfig()
                .withAdListener(listener)
                .build());
    }

    private void showUnityInterstitialAd() {
        AdsConfigNew adsConfigNew = configViewModel.getConfigData().getAdsConfigNew();
        String gameId = adsConfigNew.getUnityGameId();
        String adId = adsConfigNew.getInterstitialAdId();
        boolean testMode = adsConfigNew.isUnityTestMode();
        final UnityAdsLoadListener adsListener = new UnityAdsLoadListener();
        UnityAds.initialize(activity, gameId, testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                UnityAds.load(adId, adsListener);
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {

            }
        });
    }

    private class UnityAdsLoadListener implements IUnityAdsLoadListener {

        @Override
        public void onUnityAdsAdLoaded(String s) {
            AdsConfigNew adsConfigNew = configViewModel.getConfigData().getAdsConfigNew();
            String adId = adsConfigNew.getInterstitialAdId();
            UnityAds.show(activity, adId, new UnityAdsShowListener());
        }

        @Override
        public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {

        }
    }

    private class UnityAdsShowListener implements IUnityAdsShowListener{

        @Override
        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {

        }

        @Override
        public void onUnityAdsShowStart(String s) {

        }

        @Override
        public void onUnityAdsShowClick(String s) {

        }

        @Override
        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {

        }
    }
}