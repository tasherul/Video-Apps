package com.code.files.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.ads.MaxRewardedAd;
import com.code.files.PurchasePlanActivity;
import com.code.files.database.config.ConfigViewModel;
import com.code.files.network.model.config.AdsConfig;
import com.code.files.network.model.config.AdsConfigNew;
import com.code.files.utils.ads.PopUpAds;
import com.facebook.ads.Ad;
import com.facebook.ads.RewardedAdListener;
import com.facebook.ads.RewardedVideoAd;
import com.facebook.ads.RewardedVideoAdListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.oxoo.spagreen.R;
import com.unity3d.ads.IUnityAdsInitializationListener;
import com.unity3d.ads.IUnityAdsLoadListener;
import com.unity3d.ads.IUnityAdsShowListener;
import com.unity3d.ads.UnityAds;

public class SubscriptionDialog {
    private static final String TAG = "SubscriptionDialog";
    private Dialog dialog;
    private ConfigViewModel configViewModel;
    private Activity context;
    private RelativeLayout watchAdButton, subscriptionButton;
    private TextView text_view_watch_ads;
    private OnAdLoadingCallback onAdLoadingCallback;

    public SubscriptionDialog(Activity context, ConfigViewModel configViewModel) {
        this.context = context;
        this.configViewModel = configViewModel;
    }

    public void dismissDialog(){
        if (dialog != null)
            dialog.dismiss();
    }

    public void showDialog() {
        this.dialog = new Dialog(context, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        ((Activity) context).getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wlp.gravity = Gravity.BOTTOM;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.paid_content_dialog);

        watchAdButton = (RelativeLayout) dialog.findViewById(R.id.relative_layout_watch_ads);
        text_view_watch_ads = (TextView) dialog.findViewById(R.id.text_view_watch_ads);
        watchAdButton.setVisibility(View.VISIBLE);
        subscriptionButton = dialog.findViewById(R.id.subscribe_bt);

        watchAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               AdsConfigNew adsConfigNew = configViewModel.getConfigData().getAdsConfigNew();
               if (!adsConfigNew.getRewordAd().equalsIgnoreCase("disable")){
                   if (adsConfigNew.getRewordAd().equalsIgnoreCase(Constants.ADMOB)){
                       loadAdmobAd();
                   }else if(adsConfigNew.getRewordAd().equalsIgnoreCase(Constants.NETWORK_AUDIENCE)){
                       showFANRewarded();
                   }else if (adsConfigNew.getRewordAd().equalsIgnoreCase(Constants.APP_LOVIN)){
                       showApplovinRewardAd();
                   }else if (adsConfigNew.getRewordAd().equalsIgnoreCase(Constants.UNITY)){
                       showUnityRewardedAds();
                   }
               }
            }
        });
        subscriptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, PurchasePlanActivity.class);
                context.startActivity(intent);
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });
        if (dialog !=null)
            dialog.show();
    }

    private RewardedAd mRewardedAd = null;
    private boolean isEarnedRewarded = false;
    private void loadAdmobAd() {
        AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();
        if (!adsConfig.getRewordAd().equalsIgnoreCase("disable")){
            if (adsConfig.getRewordAd().equalsIgnoreCase(Constants.ADMOB)){
                String adId = adsConfig.getRewordAdId();

                AdRequest adRequest = new AdRequest.Builder().build();
                text_view_watch_ads.setText(R.string.ad_is_loading);
                RewardedAd.load(context, adId,
                        adRequest, new RewardedAdLoadCallback() {
                            @Override
                            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                                // Handle the error.
                                 mRewardedAd = null;
                                Toast.makeText(context, "Failed to load ad", Toast.LENGTH_SHORT).show();
                                text_view_watch_ads.setText(R.string.failed_to_load_ad);
                            }

                            @Override
                            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                                mRewardedAd = rewardedAd;
                                Log.d("RewardedAD", "Ad was loaded.");
                                text_view_watch_ads.setText(R.string.ad_loaded_successfully);
                                if (rewardedAd != null){
                                    showAdmobAd();
                                }

                                if (onAdLoadingCallback != null){
                                    onAdLoadingCallback.onFailedToShow();
                                }

                            }
                        });
            }
        }
    }

    private void showAdmobAd(){
        //show ad
        if (mRewardedAd != null) {
            //ful screen
            mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad was shown.");
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.d(TAG, "Ad failed to show.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad was dismissed.");
                    mRewardedAd = null;
                    if (dialog != null){
                        dialog.dismiss();
                    }

                    if (onAdLoadingCallback != null){
                        if (isEarnedRewarded)
                            onAdLoadingCallback.onAdShowedSuccessfully();
                    }
                }
            });


            mRewardedAd.show(context, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                    isEarnedRewarded = true;
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            Toast.makeText(context, "Ad not ready yet.", Toast.LENGTH_SHORT).show();
        }
    }

    public interface OnAdLoadingCallback{
        void onAdShowedSuccessfully();
        void onFailedToShow();
    }

    public void setOnAdLoadingCallback(OnAdLoadingCallback onAdLoadingCallback) {
        this.onAdLoadingCallback = onAdLoadingCallback;
    }

    private RewardedVideoAd rewardedVideoAd;
    private  void showFANRewarded(){
        rewardedVideoAd = new RewardedVideoAd(context, configViewModel.getConfigData().getAdsConfigNew().getRewordAdId());

        text_view_watch_ads.setText(context.getString(R.string.ad_is_loading));
        rewardedVideoAd.loadAd(
                rewardedVideoAd.buildLoadAdConfig()
                .withAdListener(listenerFan)
                .build()
        );
    }

    RewardedVideoAdListener listenerFan = new RewardedVideoAdListener() {
        @Override
        public void onRewardedVideoCompleted() {

        }

        @Override
        public void onRewardedVideoClosed() {
            if (dialog != null){
                dialog.dismiss();
            }

            if (onAdLoadingCallback != null){
                onAdLoadingCallback.onAdShowedSuccessfully();
            }
        }

        @Override
        public void onError(Ad ad, com.facebook.ads.AdError adError) {

        }

        @Override
        public void onAdLoaded(Ad ad) {
            rewardedVideoAd.show();
        }

        @Override
        public void onAdClicked(Ad ad) {

        }

        @Override
        public void onLoggingImpression(Ad ad) {

        }
    };


    private MaxRewardedAd rewardedAd;
    private int           retryAttempt;

    private void showApplovinRewardAd(){
     AdsConfigNew adsConfig = configViewModel.getConfigData().getAdsConfigNew();
     String id = adsConfig.getRewordAdId();
        rewardedAd = MaxRewardedAd.getInstance( id, context );
        rewardedAd.setListener(listener );

        rewardedAd.loadAd();

    }

    MaxRewardedAdListener listener = new MaxRewardedAdListener() {
        @Override
        public void onRewardedVideoStarted(MaxAd ad) {

        }

        @Override
        public void onRewardedVideoCompleted(MaxAd ad) {
            if (onAdLoadingCallback != null){
                onAdLoadingCallback.onAdShowedSuccessfully();
            }
        }

        @Override
        public void onUserRewarded(MaxAd ad, MaxReward reward) {

        }

        @Override
        public void onAdLoaded(MaxAd ad) {
            text_view_watch_ads.setText(context.getString(R.string.ad_loaded_successfully));
            if (ad != null)
            {
                rewardedAd.showAd();
            }
        }

        @Override
        public void onAdDisplayed(MaxAd ad) {

        }

        @Override
        public void onAdHidden(MaxAd ad) {
            rewardedAd.loadAd();
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
    };


    //unity ads
    private void showUnityRewardedAds() {
        AdsConfigNew adsConfigNew = configViewModel.getConfigData().getAdsConfigNew();
        String gameId = adsConfigNew.getUnityGameId();
        String adId = adsConfigNew.getRewordAdId();
        boolean testMode = adsConfigNew.isUnityTestMode();
        final UnityAdsLoadListener adsListener = new UnityAdsLoadListener();
        UnityAds.initialize(context, gameId, testMode, new IUnityAdsInitializationListener() {
            @Override
            public void onInitializationComplete() {
                UnityAds.load(adId, adsListener);
                text_view_watch_ads.setText(context.getString(R.string.ad_is_loading));
            }

            @Override
            public void onInitializationFailed(UnityAds.UnityAdsInitializationError unityAdsInitializationError, String s) {
                Log.e(TAG, "onInitializationFailed: Unity rewarded ad failed to initialize" );
                text_view_watch_ads.setText(context.getString(R.string.failed_to_load_ad));
            }
        });
    }

    private class UnityAdsLoadListener implements IUnityAdsLoadListener{

        @Override
        public void onUnityAdsAdLoaded(String s) {
            AdsConfigNew adsConfigNew = configViewModel.getConfigData().getAdsConfigNew();
            String adId = adsConfigNew.getRewordAdId();
            text_view_watch_ads.setText(context.getString(R.string.ad_loaded_successfully));
            UnityAds.show(context, adId, new UnityAdsShowListener());

        }

        @Override
        public void onUnityAdsFailedToLoad(String s, UnityAds.UnityAdsLoadError unityAdsLoadError, String s1) {
            text_view_watch_ads.setText(context.getString(R.string.failed_to_load_ad));
        }
    }

    private class UnityAdsShowListener implements IUnityAdsShowListener{

        @Override
        public void onUnityAdsShowFailure(String s, UnityAds.UnityAdsShowError unityAdsShowError, String s1) {
            if (onAdLoadingCallback != null){
                onAdLoadingCallback.onFailedToShow();
            }
        }

        @Override
        public void onUnityAdsShowStart(String s) {

        }

        @Override
        public void onUnityAdsShowClick(String s) {

        }

        @Override
        public void onUnityAdsShowComplete(String s, UnityAds.UnityAdsShowCompletionState unityAdsShowCompletionState) {
            if (onAdLoadingCallback != null){
                onAdLoadingCallback.onAdShowedSuccessfully();
            }
        }
    }
}
