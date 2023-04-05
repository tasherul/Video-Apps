package com.code.files.utils.ads;

import android.app.Activity;
import android.os.Bundle;


import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.lifecycle.LifecycleOwner;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.code.files.database.DatabaseHelper;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.network.model.config.AdsConfig;
import com.code.files.network.model.config.AdsConfigNew;
import com.code.files.utils.Constants;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ixidev.gdpr.GDPRChecker;
import com.code.files.utils.PreferenceUtils;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.IUnityBannerListener;
import com.unity3d.services.banners.UnityBanners;


public class BannerAds {
    private Activity activity;
    private LifecycleOwner lifecycleOwner;
    private RelativeLayout mAdViewLayout;
    private ConfigViewModel configViewModel;

    public BannerAds(Activity activity,LifecycleOwner lifecycleOwner,  RelativeLayout mAdViewLayout, ConfigViewModel configViewModel) {
        this.activity = activity;
        this.lifecycleOwner = lifecycleOwner;
        this.mAdViewLayout = mAdViewLayout;
        this.configViewModel = configViewModel;
    }

    public  void showBannerAds(){
        if (!PreferenceUtils.isActivePlan(activity)){
            String adNetwork = configViewModel.getConfigData().getAdsConfigNew().getBannerAd();
            if (adNetwork.equalsIgnoreCase(Constants.ADMOB)){
                showAdmobBannerAds();
            }else if (adNetwork.equalsIgnoreCase(Constants.NETWORK_AUDIENCE)){
                showFANBanner();
            }else if (adNetwork.equalsIgnoreCase(Constants.APP_LOVIN)){
                showAppLovinBanner();
            }else if (adNetwork.equalsIgnoreCase(Constants.UNITY)){
                showUnityAds();
            }
        }
    }

    private void showAppLovinBanner() {
        if (!PreferenceUtils.isActivePlan(activity)){
            MaxAdView adView = new MaxAdView(configViewModel.getConfigData().getAdsConfigNew().getBannerAdId(), activity);
            adView.setListener(new MaxAdViewAdListener() {
                @Override
                public void onAdExpanded(MaxAd ad) {

                }

                @Override
                public void onAdCollapsed(MaxAd ad) {

                }

                @Override
                public void onAdLoaded(MaxAd ad) {
                    adView.setVisibility(View.VISIBLE);
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

                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {

                }
            });

            adView.setVisibility(View.GONE);
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int heightDP = MaxAdFormat.BANNER.getAdaptiveSize(activity).getHeight();
            int height = AppLovinSdkUtils.dpToPx(activity, heightDP);

            adView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
            mAdViewLayout.addView(adView);
            adView.loadAd();
        }
    }

    private  void showAdmobBannerAds() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();
            if (adsConfig.getBannerAd().equalsIgnoreCase("admob")){
                AdView mAdView = new AdView(activity);
                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(adsConfig.getBannerAdId());
                AdRequest.Builder builder = new AdRequest.Builder();
                GDPRChecker.Request request = GDPRChecker.getRequest();

                if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                    // load non Personalized ads
                    Bundle extras = new Bundle();
                    extras.putString("npa", "1");
                    builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                } // else do nothing , it will load PERSONALIZED ads
                mAdView.loadAd(builder.build());
                mAdViewLayout.addView(mAdView);
            }
        }
    }


    private  void showFANBanner() {
        if (!PreferenceUtils.isActivePlan(activity)) {
            AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();
            if (adsConfig.getBannerAd().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)){
                com.facebook.ads.AdView adView = new com.facebook.ads.AdView(activity, adsConfig.getBannerAdId(), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                /*this is for test ad*/
                mAdViewLayout.addView(adView);
                // Request an ad
                adView.loadAd();
            }
        }
    }

    private void showUnityAds(){
        AdsConfigNew adsConfigNew = configViewModel.getConfigData().getAdsConfigNew();
        String gameId = adsConfigNew.getUnityGameId();
        String adId = adsConfigNew.getBannerAdId();
        boolean testMode = adsConfigNew.isUnityTestMode();
        final IUnityBannerListener myListner = new UnityBannerListener();
        UnityBanners.setBannerListener(myListner);
        UnityAds.initialize(activity, gameId, testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                UnityBanners.loadBanner(activity, adId);
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
                //UnityBanners.destroy();
            }
        });

    }

    private class UnityBannerListener implements IUnityBannerListener{

        @Override
        public void onUnityBannerLoaded(String s, View view) {
            mAdViewLayout.addView(view);
        }

        @Override
        public void onUnityBannerUnloaded(String s) {

        }

        @Override
        public void onUnityBannerShow(String s) {

        }

        @Override
        public void onUnityBannerClick(String s) {

        }

        @Override
        public void onUnityBannerHide(String s) {

        }

        @Override
        public void onUnityBannerError(String s) {

        }
    }

}