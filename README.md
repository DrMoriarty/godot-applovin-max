# AppLovin-Max plugin for godot engine.

You should have AppLovinMAX account in order to use this module https://www.applovin.com/max/

## Installation using NativeLib Addon

1. Add [NativeLib Addon](https://github.com/DrMoriarty/nativelib) into your project (search it in Godot's AssetLib).

2. Find `APPLOVINMAX` in plugins list and press "Install" button.

3. Set your SDK key in plugin variables list (see https://dash.applovin.com/o/account#keys).

4. Enable **Custom Build** and check **Plugins/App Lovin Max** for using in Android.

5. Add **res://addons/applovinmax-ios** to export resources for using in iOS.

## Installation using NativeLib-CLI

1. Install [NativeLib-CLI](https://github.com/DrMoriarty/nativelib-cli) in your system.

2. Make `nativelib -i applovinmax` in your project directory.

3. Set `AppLovin/SdkKey` in your project settings (see https://dash.applovin.com/o/account#keys).

4. Enable **Custom Build** and check **Plugins/App Lovin Max** for using in Android.

5. Add **res://addons/applovinmax-ios** to export resources for using in iOS.

## Notes
1. Permission **ACCESS_COARSE_LOCATION** will automatically be added when exporting to Android.

2. For iOS, consider using the ATT plugin (https://github.com/DrMoriarty/godot-ios-att) (required by Apple for iOS 14.0+).

## Adapters

The core module has only SDK for Applovin network. For using additional networks you should install specific adapters. For example install `applovinmax-facebook` for Facebook Audience Network. 

## Usage

Wrapper on gd-script will be in your autoloading list. Use global name `applovin_max` anywhere in your code to use API.

## API

### debugMediation()

Show debug mediation view.

### isInited() -> bool

Check is SDK inited or not.

### setUserId(uid: String)

Set custom user ID.

### isGdprApplies()

Check if GDPR applies to your user.

### setGdprConsent(consent: bool)

Set GDPR consent from your user.

### setAgeRestricted(restricted: bool)

Set age restriction for your user.

### setCCPAApplied(applied: bool)

Set if CCPA applied to your user.

### loadBanner(id: String, isTop: bool, callback_id: int)

Load banner with specific zone ID. `callback_id` is instance_id from callback object.

### loadInterstitial(id: String, callback_id: int)

Load interstitial with specific zone ID. `callback_id` is instance_id from callback object.

### loadRewardedVideo(id: String, callback_id: int)

Load rewarded ad with specific zone ID. `callback_id` is instance_id from callback object.

### loadMREC(id: String, gravity: int, callback_id: int)

Load MREC with specific zone ID. `gravity` is [Android Gravity](https://developer.android.com/reference/android/view/Gravity). `callback_id` is instance_id from callback object.

### bannerWidth(id: String) -> int

Returns current banner width. Returns 0 if there are no active banners.

### bannerHeight(id: String) -> int

Returns current banner height. Returns 0 if there are no active banners.

### showBanner(id: String)

Show banner with specific zone ID. The banner must be loaded before this call.

### hideBanner(id: String)

Hide banner with specific zone ID.

### removeBanner(id: String)

Completely remove banner view from the screen.

### showInterstitial(id: String)

Show interstitial with specific zone ID. The interstitial must be loaded before call.

### showRewardedVideo(id: String)

Show rewarded video ad with specific zone ID. The rewarded ad must be loaded before call.

### showMREC(id: String)

Show MREC with specific zone ID. The MREC must be loaded before call.

### removeMREC(id: String)

Remove MREC view from the screen.

## Callbacks

When load ad you specified instance_id of callback object. This object can have methods to get callbacks from the SDK.

For example: `applovin_max.loadBanner("zone_id", true, self.get_instance_id())`

### Rewarded video callbacks

_on_rewarded_video_ad_loaded(id: String)

_on_rewarded_video_ad_failed_to_load(id: String, error: String)

_on_rewarded_video_ad_opened(id: String)

_on_rewarded_video_ad_left_application(id: String)

_on_rewarded_video_ad_closed(id: String)

_on_rewarded_video_started(id: String)

_on_rewarded_video_completed(id: String)

_on_rewarded(id: String, reward: String, amount: int)

### Banner callbacks

_on_banner_loaded(id: String)

_on_banner_failed_to_load(id: String)

_on_banner_failed_to_load(id: String, error: String)

_on_banner_shown(id: String)

### MREC callbacks

_on_mrec_loaded(id: String)

_on_mrec_failed_to_load(id: String, error: String)

### Interstitial callbacks

_on_interstitial_loaded(id: String)

_on_interstitial_failed_to_load(id: String, error: String)

_on_interstitial_close(id: String)
