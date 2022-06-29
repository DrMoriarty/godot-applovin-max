package ru.mobilap.applovinmax;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;

import org.godotengine.godot.Godot;
import org.godotengine.godot.GodotLib;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;

import com.applovin.mediation.MaxError;
import com.applovin.sdk.AppLovinMediationProvider;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkConfiguration;
import com.applovin.sdk.AppLovinPrivacySettings;
import com.applovin.sdk.AppLovinSdkSettings;
import com.applovin.sdk.AppLovinSdkUtils;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdListener;
import com.applovin.mediation.MaxRewardedAdListener;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.mediation.ads.MaxInterstitialAd;
import com.applovin.mediation.ads.MaxRewardedAd;

public class AppLovinMax extends GodotPlugin
{
    private final String TAG = AppLovinMax.class.getName();

    private HashMap<String, MaxInterstitialAd> interstitials = new HashMap<>();
    private HashMap<String, MaxAdView> banners = new HashMap<>();
    private HashMap<String, MaxAdView> mrecs = new HashMap<>();
    private HashMap<String, MaxRewardedAd> rewardeds = new HashMap<>();
    private HashMap<String, FrameLayout.LayoutParams> layoutParams = new HashMap<>();

    private boolean ProductionMode = true; // Store if is real or not

    private FrameLayout layout = null; // Store the layout
    private AppLovinSdk sdk = null;
    private boolean _inited = false;
    private boolean gdprApplies;

    /* Init
     * ********************************************************************** */

    /**
     * Prepare for work with ApplovinMax
     * @param boolean ProductionMode Tell if the enviroment is for real or test
     * @param int gdscript instance id
     */
    public void init(final String sdkKey, boolean ProductionMode) {

        this.ProductionMode = ProductionMode;
        layout = (FrameLayout)getActivity().getWindow().getDecorView().getRootView();
        sdk = AppLovinSdk.getInstance(sdkKey, new AppLovinSdkSettings(null), getActivity());
        //if(!ProductionMode) sdk.getSettings().setVerboseLogging( true );
        sdk.setMediationProvider( AppLovinMediationProvider.MAX );
        sdk.initializeSdk(new AppLovinSdk.SdkInitializationListener() {
                @Override
                public void onSdkInitialized(final AppLovinSdkConfiguration configuration)
                {
                    _inited = true;
                    Log.i(TAG, "ApplovinMAX initialized");
                    if ( configuration.getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.APPLIES ) {
                        // Show user consent dialog
                        Log.i(TAG, "GDPR applies");
                        gdprApplies = true;
                    } else if ( configuration.getConsentDialogState() == AppLovinSdkConfiguration.ConsentDialogState.DOES_NOT_APPLY ) {
                        // No need to show consent dialog, proceed with initialization
                        Log.i(TAG, "GDPR doesn't applies");
                        gdprApplies = false;
                    } else {
                        // Consent dialog state is unknown. Proceed with initialization, but check if the consent
                        // dialog should be shown on the next application initialization
                        Log.i(TAG, "GDPR status unknown");
                        gdprApplies = false;
                    }
                }
            } );
    }

    public boolean isInited() {
        return _inited;
    }

    public void setUserId(final String uid) {
        sdk.setUserIdentifier( uid );
    }

    public boolean isGdprApplies() {
        return gdprApplies;
    }
    
    public void debugMediation() {
        sdk.showMediationDebugger();
    }

    public void setGdprConsent(final boolean consent) {
        AppLovinPrivacySettings.setHasUserConsent( consent, getActivity() );
    }

    public void setAgeRestricted(final boolean ageRestricted) {
        AppLovinPrivacySettings.setIsAgeRestrictedUser( ageRestricted, getActivity() );
    }

    public void setCCPAApplied(final boolean ccpaApplied) {
        AppLovinPrivacySettings.setDoNotSell( ccpaApplied, getActivity() );
    }
    
    /* Rewarded Video
     * ********************************************************************** */
    private MaxRewardedAd initRewardedVideo(final String id, final int callback_id)
    {
        Log.w(TAG, "Prepare rewarded video: "+id+" callback: "+Integer.toString(callback_id));

        MaxRewardedAd rewardedAd = MaxRewardedAd.getInstance(id, sdk, getActivity() );
        rewardedAd.setListener(new MaxRewardedAdListener() {
                @Override
                public void onAdLoaded(final MaxAd maxAd) {
                    // Rewarded ad is ready to be shown. rewardedAd.isReady() will now return 'true'
                    Log.i(TAG, "Rewarded: onAdLoaded");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_ad_loaded", new Object[] { id });
                }
                @Override
                public void onAdDisplayed(final MaxAd maxAd) {
                    Log.i(TAG, "Rewarded: onAdDisplayed");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_ad_opened", new Object[] { id });
                }
                @Override
                public void onAdClicked(final MaxAd maxAd) {
                    Log.i(TAG, "Rewarded: onAdClicked");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_ad_left_application", new Object[] { id });
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    // Rewarded ad failed to load. We recommend retrying with exponentially higher delays.
                    Log.i(TAG, "Rewarded: onAdLoadFailed");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_ad_failed_to_load", new Object[] { id, " "+ error });
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    // Rewarded ad failed to display. We recommend loading the next ad
                    Log.i(TAG, "Rewarded: onAdDisplayFailed");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_ad_failed_to_load", new Object[] { id, " "+error });
                }

                @Override
                public void onAdHidden(final MaxAd maxAd) {
                    // rewarded ad is hidden. Pre-load the next ad
                    Log.i(TAG, "Rewarded: onAdHidden");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_ad_closed", new Object[] { id });
                }
                @Override
                public void onRewardedVideoStarted(final MaxAd maxAd) {
                    Log.i(TAG, "Rewarded: onRewardedVideoStarted");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_started", new Object[] { id });
                }
                @Override
                public void onRewardedVideoCompleted(final MaxAd maxAd) {
                    Log.i(TAG, "Rewarded: onRewardedVideoCompleted");
                    GodotLib.calldeferred(callback_id, "_on_rewarded_video_completed", new Object[] { id });
                }
                @Override
                public void onUserRewarded(final MaxAd maxAd, final MaxReward maxReward) {
                    // Rewarded ad was displayed and user should receive the reward
                    Log.w(TAG, "Rewarded: " + String.format(" onUserRewarded! currency: %s amount: %d", maxReward.getLabel(), maxReward.getAmount()));
                    GodotLib.calldeferred(callback_id, "_on_rewarded", new Object[] { id, maxReward.getLabel(), maxReward.getAmount() });
                }
            });
        return rewardedAd;
    }

    /**
     * Load a Rewarded Video
     * @param String id AdMod Rewarded video ID
     */
    public void loadRewardedVideo(final String id, final int callback_id) {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    MaxRewardedAd rewardedAd = initRewardedVideo(id, callback_id);
                    rewardedAd.loadAd();
                    rewardeds.put(id, rewardedAd);
                }
            });
    }

    /**
     * Show a Rewarded Video
     */
    public void showRewardedVideo(final String id) {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(rewardeds.containsKey(id)) {
                        MaxRewardedAd rewardedAd = rewardeds.get(id);
                        rewardedAd.showAd();
                    } else {
                        Log.w(TAG, "showRewardedVideo - rewarded not loaded");
                    }
                }
            });
    }

    /* Banner
     * ********************************************************************** */

    private MaxAdView initBanner(final String id, final boolean isOnTop, final int callback_id)
    {

        // Create an ad view with a specific zone to load ads for
        MaxAdView adView = new MaxAdView( id, sdk, getActivity() );

        // Optional: Set listeners
        adView.setListener( new MaxAdViewAdListener() {
                @Override
                public void onAdLoaded(final MaxAd maxAd) {
                    Log.w(TAG, "Banner: onAdLoaded");
                    GodotLib.calldeferred(callback_id, "_on_banner_loaded", new Object[]{ id });
                }
                @Override
                public void onAdHidden(final MaxAd maxAd) {
                }
                @Override
                public void onAdDisplayed(final MaxAd maxAd) {
                    Log.w(TAG, "Banner: onAdDisplayed");
                    GodotLib.calldeferred(callback_id, "_on_banner_shown", new Object[]{ id });
                }
                @Override
                public void onAdClicked(final MaxAd maxAd) {
                }

                @Override
                public void onAdLoadFailed(String adUnitId, MaxError error) {
                    Log.w(TAG, "Banner: onAdLoadFailed");
                    GodotLib.calldeferred(callback_id, "_on_banner_failed_to_load", new Object[]{ id, ""+ error.getMessage() });
                }

                @Override
                public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                    Log.w(TAG, "Banner: onAdLoadFailed");
                    GodotLib.calldeferred(callback_id, "_on_banner_failed_to_load", new Object[]{ id, ""+error.getMessage() });
                }

            @Override
                public void onAdExpanded(final MaxAd maxAd) {
                }
                @Override
                public void onAdCollapsed(final MaxAd maxAd) {
                }
            });

        FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(
                                                                         FrameLayout.LayoutParams.MATCH_PARENT,
                                                                         AppLovinSdkUtils.dpToPx( getActivity(), 50 )
                                                                         );
        if(isOnTop) adParams.gravity = Gravity.TOP;
        else adParams.gravity = Gravity.BOTTOM;
        adView.setBackgroundColor(/* Color.WHITE */Color.TRANSPARENT);
        layoutParams.put(id, adParams);
        return adView;
    }

    /**
     * Load a banner
     * @param String id AdMod Banner ID
     * @param boolean isOnTop To made the banner top or bottom
     */
    public void loadBanner(final String id, final boolean isOnTop, final int callback_id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(!banners.containsKey(id)) {
                        MaxAdView adView = initBanner(id, isOnTop, callback_id);
                        adView.loadAd();
                        banners.put(id, adView);
                    } else {
                        MaxAdView adView = banners.get(id);
                        adView.loadAd();
                    }
                }
            });
    }

    /**
     * Show the banner
     */
    public void showBanner(final String id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(banners.containsKey(id)) {
                        MaxAdView b = banners.get(id);
                        if(b.getParent() != null) {
                            Log.w(TAG, "Banner already shown: "+id);
                            return;
                        }
                        FrameLayout.LayoutParams adParams = layoutParams.get(id);
                        layout.addView(b, adParams);
                        b.setVisibility(View.VISIBLE);
                        b.startAutoRefresh();
                        for (String key : banners.keySet()) {
                            if(!key.equals(id)) {
                                MaxAdView b2 = banners.get(key);
                                b2.setVisibility(View.GONE);
                                b2.stopAutoRefresh();
                            }
                        }
                        Log.d(TAG, "Show Banner");
                    } else {
                        Log.w(TAG, "Banner not found: "+id);
                    }
                }
            });
    }

    public void removeBanner(final String id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(banners.containsKey(id)) {
                        MaxAdView b = banners.get(id);
                        banners.remove(id);
                        layout.removeView(b); // Remove the banner
                        Log.d(TAG, "Remove Banner");
                    } else {
                        Log.w(TAG, "Banner not found: "+id);
                    }
                }
            });
    }

    /**
     * Hide the banner
     */
    public void hideBanner(final String id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(banners.containsKey(id)) {
                        MaxAdView b = banners.get(id);
                        b.setVisibility(View.GONE);
                        b.stopAutoRefresh();
                        Log.d(TAG, "Hide Banner");
                    } else {
                        Log.w(TAG, "Banner not found: "+id);
                    }
                }
            });
    }

    /**
     * Get the banner width
     * @return int Banner width
     */
    public int getBannerWidth(final String id)
    {
        if(banners.containsKey(id)) {
            MaxAdView b = banners.get(id);
            if(b != null) {
                int w = b.getWidth();
                /*
                if(w == 0) {
                    Resources r = activity.getResources();
                    w = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, b.getSize().width, r.getDisplayMetrics());
                }
                */
                return w;
            } else
                return 0;
        } else {
            return 0;
        }
    }

    /**
     * Get the banner height
     * @return int Banner height
     */
    public int getBannerHeight(final String id)
    {
        if(banners.containsKey(id)) {
            MaxAdView b = banners.get(id);
            if(b != null) {
                int h = b.getHeight();
                /*
                if (h == 0) {
                    Resources r = activity.getResources();
                    h = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, b.getSize().height, r.getDisplayMetrics());
                }
                */
                return h;
            } else
                return 0;
        } else {
            return 0;
        }
    }

    /* MREC
     * ********************************************************************** */

    private MaxAdView initMREC(final String id, final int gravity, final int callback_id)
    {

        // Create an ad view with a specific zone to load ads for
        MaxAdView adView = new MaxAdView( id, MaxAdFormat.MREC, sdk, getActivity() );

        // Optional: Set listeners
        adView.setListener( new MaxAdViewAdListener() {
                @Override
                public void onAdLoaded(final MaxAd maxAd) {
                    Log.w(TAG, "MREC: onAdLoaded");
                    GodotLib.calldeferred(callback_id, "_on_mrec_loaded", new Object[]{ id });
                }
                @Override
                public void onAdHidden(final MaxAd maxAd) {
                }
                @Override
                public void onAdDisplayed(final MaxAd maxAd) {
                }
                @Override
                public void onAdClicked(final MaxAd maxAd) {
                }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                        Log.w(TAG, "MREC: onAdLoadFailed");
                        GodotLib.calldeferred(callback_id, "_on_mrec_failed_to_load", new Object[]{ id, ""+error.getMessage() });
                    }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }

            @Override
                public void onAdExpanded(final MaxAd maxAd) {
                }
                @Override
                public void onAdCollapsed(final MaxAd maxAd) {
                }
            });

        FrameLayout.LayoutParams adParams = new FrameLayout.LayoutParams(AppLovinSdkUtils.dpToPx(getActivity(), 300), AppLovinSdkUtils.dpToPx(getActivity(), 250));
        adParams.gravity = gravity;
        adView.setBackgroundColor(/* Color.WHITE */Color.TRANSPARENT);
        layoutParams.put(id, adParams);
        return adView;
    }

    /**
     * Load a banner
     * @param String id AdMod Banner ID
     * @param boolean isOnTop To made the banner top or bottom
     */
    public void loadMREC(final String id, final int gravity, final int callback_id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(!mrecs.containsKey(id)) {
                        MaxAdView adView = initMREC(id, gravity, callback_id);
                        adView.loadAd();
                        mrecs.put(id, adView);
                    } else {
                        MaxAdView adView = mrecs.get(id);
                        adView.loadAd();
                    }
                }
            });
    }

    /**
     * Show the banner
     */
    public void showMREC(final String id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(mrecs.containsKey(id)) {
                        MaxAdView b = mrecs.get(id);
                        FrameLayout.LayoutParams adParams = layoutParams.get(id);
                        layout.addView(b, adParams);
                        b.setVisibility(View.VISIBLE);
                        b.startAutoRefresh();
                        Log.d(TAG, "Show MREC");
                    } else {
                        Log.w(TAG, "MREC not found: "+id);
                    }
                }
            });
    }

    public void removeMREC(final String id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(mrecs.containsKey(id)) {
                        MaxAdView b = mrecs.get(id);
                        mrecs.remove(id);
                        layout.removeView(b); // Remove the MREC
                        Log.d(TAG, "Remove MREC");
                    } else {
                        Log.w(TAG, "MREC not found: "+id);
                    }
                }
            });
    }

    /* Interstitial
     * ********************************************************************** */
    private MaxInterstitialAd initInterstitial(final String id, final int callback_id)
    {
        MaxInterstitialAd interstitial = new MaxInterstitialAd( id, sdk, getActivity() );
        interstitial.setListener(new MaxAdListener() {
                @Override
                public void onAdLoaded(final MaxAd maxAd) {
                    // Interstitial ad is ready to be shown. interstitialAd.isReady() will now return 'true'
                    Log.i(TAG, "Interstitial: onAdLoaded");
                    GodotLib.calldeferred(callback_id, "_on_interstitial_loaded", new Object[] { id });
                }
                @Override
                public void onAdDisplayed(final MaxAd maxAd) {
                    Log.i(TAG, "Interstitial: onAdDisplayed");
                }
                @Override
                public void onAdClicked(final MaxAd maxAd) {
                    Log.i(TAG, "Interstitial: onAdClicked");
                }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {
                Log.i(TAG, "Interstitial: onAdLoadFailed");
                GodotLib.calldeferred(callback_id, "_on_interstitial_failed_to_load", new Object[] { id, ""+error.getMessage() });
            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {
                Log.i(TAG, "Interstitial: onAdDisplayFailed");
                GodotLib.calldeferred(callback_id, "_on_interstitial_failed_to_load", new Object[] { id, ""+error.getMessage() });
            }

            @Override
                public void onAdHidden(final MaxAd maxAd) {
                    Log.i(TAG, "Interstitial: onAdHidden");
                    GodotLib.calldeferred(callback_id, "_on_interstitial_close", new Object[] { id });
                }
            });
        return interstitial;
    }

    /**
     * Load a interstitial
     * @param String id AdMod Interstitial ID
     */
    public void loadInterstitial(final String id, final int callback_id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    // Load an ad for a given zone
                    MaxInterstitialAd interstitial = initInterstitial(id, callback_id);
                    interstitial.loadAd();
                    interstitials.put(id, interstitial);
                }
            });
    }

    /**
     * Show the interstitial
     */
    public void showInterstitial(final String id)
    {
        getActivity().runOnUiThread(new Runnable() {
                @Override public void run() {
                    if(interstitials.containsKey(id)) {
                        MaxInterstitialAd interstitial = interstitials.get(id);
                        interstitial.showAd();
                    } else {
                        Log.w(TAG, "Interstitial not found: " + id);
                    }
                }
            });
    }

    /* Utils
     * ********************************************************************** */


    /* Definitions
     * ********************************************************************** */
    public AppLovinMax(Godot godot) 
    {
        super(godot);
    }

    @Override
    public String getPluginName() {
        return "AppLovinMax";
    }

    @Override
    public List<String> getPluginMethods() {
        return Arrays.asList(
                "init", "isInited", "setUserId", "debugMediation", "isGdprApplies", "setGdprConsent", "setAgeRestricted", "setCCPAApplied", 
                // banner
                "loadBanner", "showBanner", "hideBanner", "removeBanner", "getBannerWidth", "getBannerHeight",
                // Interstitial
                "loadInterstitial", "showInterstitial",
                // Rewarded video
                "loadRewardedVideo", "showRewardedVideo",
                // MREC
                "loadMREC", "showMREC", "removeMREC"
        );
    }

    /*
    @Override
    public Set<SignalInfo> getPluginSignals() {
        return Collections.singleton(loggedInSignal);
    }
    */

    @Override
    public View onMainCreate(Activity activity) {
        return null;
    }
}
