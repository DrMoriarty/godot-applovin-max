#ifndef GODOT_APPLOVINMAX_H
#define GODOT_APPLOVINMAX_H

#include <Godot.hpp>
#include <Reference.hpp>

class AppLovinMax : public godot::Reference {
    GODOT_CLASS(AppLovinMax, godot::Reference);

    bool productionMode;
    bool bannerShown;
    bool gdprApplies;
    bool inited;

protected:

public:
    static void _register_methods();
    void _init();

    void init(const godot::String sdk_key, bool ProductionMode);
    bool isInited();
    void setUserId(const godot::String user_id);
    bool isGdprApplies();
    void debugMediation();
    void setGdprConsent(bool consent);
    void setAgeRestricted(bool age_restricted);
    void setCCPAApplied(bool ccpa_applied);
    
    void loadBanner(const godot::String bannerId, bool isOnTop, Object *callbackOb);
    void showBanner(const godot::String bannerId);
    void hideBanner(const godot::String bannerId);
    void removeBanner(const godot::String bannerId);
    void resize();
    int getBannerWidth(const godot::String bannerId);
    int getBannerHeight(const godot::String bannerId);
    void loadInterstitial(const godot::String interstitialId, Object *callbackOb);
    void showInterstitial(const godot::String interstitialId);
    void loadRewardedVideo(const godot::String rewardedId, Object *callbackOb);
    void showRewardedVideo(const godot::String rewardedId);

    AppLovinMax();
    ~AppLovinMax();
};

#endif
