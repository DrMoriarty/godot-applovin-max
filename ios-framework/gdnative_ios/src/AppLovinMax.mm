#import <Foundation/Foundation.h>
#import <AppLovinSDK/AppLovinSDK.h>
#import <AppLovinSDK/MAAdDelegate.h>
#include "AppLovinMax.hpp"

using namespace godot;

#define BANNER_ENABLE_DELAY 5
 
static NSMutableDictionary *banners = nil;
static NSMutableDictionary *interstitials = nil;
static NSMutableDictionary *rewardeds = nil;
static NSString *lastBannerId = nil;
static ALSdk *sdk = nil;

@interface InterstitialWrapper: NSObject<MAAdDelegate>
@property (nonatomic, assign) Object* cbOb;
@property (nonatomic, strong) MAInterstitialAd* interstitial;
@end

@implementation InterstitialWrapper
- (instancetype)initWithAdUnitId:(NSString*)idStr andCallbackOb:(Object*)callbackOb {
    if((self = [super init])) {
        _cbOb = callbackOb;
        _interstitial = [[MAInterstitialAd alloc] initWithAdUnitIdentifier:idStr sdk:sdk];
        _interstitial.delegate = self;
        [_interstitial loadAd];
    }
    return self;
}

- (void)didLoadAd:(MAAd *)ad
{
    NSLog(@"MAX: interstitial loaded %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_interstitial_loaded", args);
}

- (void)didFailToLoadAdForAdUnitIdentifier:(NSString *)adUnitIdentifier withErrorCode:(NSInteger)errorCode
{
    NSLog(@"MAX: interstitial loading failed %@", adUnitIdentifier);
    NSString *err = [NSString stringWithFormat:@"%d", (int)errorCode];
    Array args = Array();
    args.append(String(adUnitIdentifier.UTF8String));
    args.append(String(err.UTF8String));
    _cbOb->call_deferred("_on_interstitial_failed_to_load", args);
}

- (void)didDisplayAd:(MAAd *)ad
{
    NSLog(@"MAX: interstitial display ad %@", ad.adUnitIdentifier);
}

- (void)didHideAd:(MAAd *)ad
{
    NSLog(@"MAX: interstitial hide ad %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_interstitial_close", args);
}

- (void)didClickAd:(MAAd *)ad
{
    NSLog(@"MAX: interstitial click ad %@", ad.adUnitIdentifier);
}

- (void)didFailToDisplayAd:(MAAd *)ad withErrorCode:(NSInteger)errorCode
{
    NSLog(@"MAX: interstitial display failed %@", ad.adUnitIdentifier);
    NSString *err = [NSString stringWithFormat:@"%d", (int)errorCode];
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    args.append(String(err.UTF8String));
    _cbOb->call_deferred("_on_interstitial_failed_to_load", args);
}

@end


@interface RewardedWrapper: NSObject<MARewardedAdDelegate>
@property (nonatomic, assign) Object* cbOb;
@property (nonatomic, strong) MARewardedAd* rewarded;
@end

@implementation RewardedWrapper
- (instancetype)initWithAdUnitId:(NSString*)idStr andCallbackOb:(Object*)callbackOb {
    if((self = [super init])) {
        _cbOb = callbackOb;
        _rewarded = [MARewardedAd sharedWithAdUnitIdentifier:idStr sdk:sdk];
        _rewarded.delegate = self;
        [_rewarded loadAd];
    }
    return self;
}

- (void)didLoadAd:(MAAd *)ad
{
    NSLog(@"MAX: rewarded loaded %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_ad_loaded", args);
}

- (void)didFailToLoadAdForAdUnitIdentifier:(NSString *)adUnitIdentifier withErrorCode:(NSInteger)errorCode
{
    NSLog(@"MAX: rewarded loading failed %@", adUnitIdentifier);
    NSString *err = [NSString stringWithFormat:@"%d", (int)errorCode];
    Array args = Array();
    args.append(String(adUnitIdentifier.UTF8String));
    args.append(String(err.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_ad_failed_to_load", args);
}

- (void)didDisplayAd:(MAAd *)ad
{
    NSLog(@"MAX: rewarded display ad %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_ad_opened", args);
}

- (void)didHideAd:(MAAd *)ad
{
    NSLog(@"MAX: rewarded hide ad %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_ad_closed", args);
}

- (void)didClickAd:(MAAd *)ad
{
    NSLog(@"MAX: rewarded click ad %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_ad_left_application", args);
}

- (void)didFailToDisplayAd:(MAAd *)ad withErrorCode:(NSInteger)errorCode
{
    NSLog(@"MAX: rewarded display failed %@", ad.adUnitIdentifier);
    NSString *err = [NSString stringWithFormat:@"%d", (int)errorCode];
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    args.append(String(err.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_ad_failed_to_load", args);
}

- (void)didStartRewardedVideoForAd:(MAAd *)ad {
    NSLog(@"MAX: rewarded start ad %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_started", args);
}

- (void)didCompleteRewardedVideoForAd:(MAAd *)ad {
    NSLog(@"MAX: rewarded complete ad %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_rewarded_video_completed", args);
}

- (void)didRewardUserForAd:(MAAd *)ad withReward:(MAReward *)reward
{
    NSLog(@"MAX: rewarded: %@ %@ %d", ad.adUnitIdentifier, reward.label, (int)reward.amount);
    // Rewarded ad was displayed and user should receive the reward
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    args.append(String(reward.label.UTF8String));
    args.append((int)reward.amount);
    _cbOb->call_deferred("_on_rewarded", args);
}

@end


@interface BannerWrapper: NSObject<MAAdViewAdDelegate>
@property (nonatomic, readonly) Object* cbOb;
@property (nonatomic, strong) MAAdView* banner;
@property (nonatomic, readonly) BOOL onTop;
@end

@implementation BannerWrapper
- (instancetype)initWithAdUnitId:(NSString*)idStr andCallbackOb:(Object*)callbackOb top:(BOOL)isOnTop {
    if((self = [super init])) {
        _cbOb = callbackOb;
        _onTop = isOnTop;
        _banner = [[MAAdView alloc] initWithAdUnitIdentifier:idStr sdk:sdk];
        _banner.delegate = self;
        CGRect fr = UIApplication.sharedApplication.keyWindow.rootViewController.view.frame;
        if(isOnTop) {
            fr.origin = CGPointZero;
            fr.size.height = fr.size.width > 320 ? 90 : 50;
        } else {
            CGFloat H = fr.size.height;
            fr.size.height = fr.size.width > 320 ? 90 : 50;
            fr.origin = CGPointMake(0, H - fr.size.height);
        }
        _banner.frame = fr;
        [_banner loadAd];
    }
    return self;
}

- (void)didLoadAd:(MAAd *)ad
{
    NSLog(@"MAX: banner loaded %@", ad.adUnitIdentifier);
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    _cbOb->call_deferred("_on_banner_loaded", args);
}

- (void)didFailToLoadAdForAdUnitIdentifier:(NSString *)adUnitIdentifier withErrorCode:(NSInteger)errorCode
{
    NSLog(@"MAX: banner loading failed %@", adUnitIdentifier);
    NSString *err = [NSString stringWithFormat:@"%d", (int)errorCode];
    Array args = Array();
    args.append(String(adUnitIdentifier.UTF8String));
    args.append(String(err.UTF8String));
    _cbOb->call_deferred("_on_banner_failed_to_load", args);
}

- (void)didDisplayAd:(MAAd *)ad
{
    NSLog(@"MAX: banner display ad %@", ad.adUnitIdentifier);
}

- (void)didHideAd:(MAAd *)ad
{
    NSLog(@"MAX: banner hide ad %@", ad.adUnitIdentifier);
}

- (void)didClickAd:(MAAd *)ad
{
    NSLog(@"MAX: banner click ad %@", ad.adUnitIdentifier);
}

- (void)didFailToDisplayAd:(MAAd *)ad withErrorCode:(NSInteger)errorCode
{
    NSLog(@"MAX: banner display failed %@", ad.adUnitIdentifier);
    NSString *err = [NSString stringWithFormat:@"%d", (int)errorCode];
    Array args = Array();
    args.append(String(ad.adUnitIdentifier.UTF8String));
    args.append(String(err.UTF8String));
    _cbOb->call_deferred("_on_banner_failed_to_load", args);
}
- (void)didExpandAd:(MAAd *)ad {}
- (void)didCollapseAd:(MAAd *)ad {}
@end



AppLovinMax::AppLovinMax() {
    banners = [NSMutableDictionary new];
    interstitials = [NSMutableDictionary new];
    rewardeds = [NSMutableDictionary new];
    inited = false;
}

AppLovinMax::~AppLovinMax() {
}

void AppLovinMax::_init() {
}

void AppLovinMax::init(const String sdk_key, bool ProductionMode) {
    NSString *key = [NSString stringWithCString:sdk_key.utf8().get_data() encoding: NSUTF8StringEncoding];
    productionMode = ProductionMode;
    sdk = [ALSdk sharedWithKey:key];
    sdk.mediationProvider = @"max";
    //[sdk.settings setIsVerboseLogging:!ProductionMode];
    [sdk initializeSdkWithCompletionHandler:^(ALSdkConfiguration * _Nonnull configuration) {
        inited = true;
        if(configuration.consentDialogState == ALConsentDialogStateApplies) {
            NSLog(@"MAX: GDPR applies");
            gdprApplies = true;
        } else if (configuration.consentDialogState == ALConsentDialogStateDoesNotApply) {
            NSLog(@"MAX: GDPR doesn't applies");
            gdprApplies = false;
        } else {
            NSLog(@"MAX: GDPR status unknown");
            gdprApplies = false;
        }
    }];
}

bool AppLovinMax::isInited() {
    return inited;
}

void AppLovinMax::setUserId(const String user_id) {
    NSString *uid = [NSString stringWithCString:user_id.utf8().get_data() encoding: NSUTF8StringEncoding];
    [sdk setUserIdentifier:uid];
}

bool AppLovinMax::isGdprApplies() {
    return gdprApplies;
}

void AppLovinMax::debugMediation() {
    [sdk showMediationDebugger];
}

void AppLovinMax::setGdprConsent(bool consent) {
    [ALPrivacySettings setHasUserConsent:consent];
}

void AppLovinMax::setAgeRestricted(bool age_restricted) {
    [ALPrivacySettings setIsAgeRestrictedUser:age_restricted];
}

void AppLovinMax::setCCPAApplied(bool ccpa_applied) {
    [ALPrivacySettings setDoNotSell:ccpa_applied];
}

void AppLovinMax::loadBanner(const String bannerId, bool isOnTop, Object *callbackOb) {
    NSString *idStr = [NSString stringWithCString:bannerId.utf8().get_data() encoding: NSUTF8StringEncoding];
    BannerWrapper *wrapper = [[BannerWrapper alloc] initWithAdUnitId:idStr andCallbackOb:callbackOb top:isOnTop];
    [banners setObject:wrapper forKey:idStr];
}

void AppLovinMax::showBanner(const String bannerId) {
    NSString *idStr = [NSString stringWithCString:bannerId.utf8().get_data() encoding: NSUTF8StringEncoding];
    BannerWrapper * wrapper = [banners objectForKey:idStr];
    UIViewController *root = UIApplication.sharedApplication.keyWindow.rootViewController;
    [root.view addSubview:wrapper.banner];
    wrapper.banner.hidden = NO;
    bannerShown = true;
}

void AppLovinMax::hideBanner(const String bannerId) {
    NSString *idStr = [NSString stringWithCString:bannerId.utf8().get_data() encoding: NSUTF8StringEncoding];
    BannerWrapper * wrapper = [banners objectForKey:idStr];
    wrapper.banner.hidden = YES;
    bannerShown = false;
}

void AppLovinMax::removeBanner(const String bannerId) {
    NSString *idStr = [NSString stringWithCString:bannerId.utf8().get_data() encoding: NSUTF8StringEncoding];
    BannerWrapper * wrapper = [banners objectForKey:idStr];
    [wrapper.banner removeFromSuperview];
    [banners removeObjectForKey:idStr];
}

void AppLovinMax::resize() {
    //[banner resize];
}

int AppLovinMax::getBannerWidth(const String bannerId) {
    NSString *idStr = [NSString stringWithCString:bannerId.utf8().get_data() encoding: NSUTF8StringEncoding];
    BannerWrapper * wrapper = [banners objectForKey:idStr];
    return wrapper.banner.frame.size.width;
}

int AppLovinMax::getBannerHeight(const String bannerId) {
    NSString *idStr = [NSString stringWithCString:bannerId.utf8().get_data() encoding: NSUTF8StringEncoding];
    BannerWrapper * wrapper = [banners objectForKey:idStr];
    return wrapper.banner.frame.size.height;
}

void AppLovinMax::loadInterstitial(const String interstitialId, Object *callbackOb) {
    NSString *idStr = [NSString stringWithCString:interstitialId.utf8().get_data() encoding: NSUTF8StringEncoding];
    InterstitialWrapper *wrapper = [[InterstitialWrapper alloc] initWithAdUnitId:idStr andCallbackOb:callbackOb];
    [interstitials setObject:wrapper forKey:idStr];
}

void AppLovinMax::showInterstitial(const String interstitialId) {    
    NSString *idStr = [NSString stringWithCString:interstitialId.utf8().get_data() encoding: NSUTF8StringEncoding];
    InterstitialWrapper * wrapper = [interstitials objectForKey:idStr];
    [wrapper.interstitial showAd];
}

void AppLovinMax::loadRewardedVideo(const String rewardedId, Object *callbackOb) {
    NSString *idStr = [NSString stringWithCString:rewardedId.utf8().get_data() encoding: NSUTF8StringEncoding];
    RewardedWrapper *wrapper = [[RewardedWrapper alloc] initWithAdUnitId:idStr andCallbackOb:callbackOb];
    [rewardeds setObject:wrapper forKey:idStr];
}

void AppLovinMax::showRewardedVideo(const String rewardedId) {
    NSString *idStr = [NSString stringWithCString:rewardedId.utf8().get_data() encoding: NSUTF8StringEncoding];
    RewardedWrapper * wrapper = [rewardeds objectForKey:idStr];
    [wrapper.rewarded showAd];
}

void AppLovinMax::_register_methods() {
    register_method("_init",&AppLovinMax::_init);
    register_method("init",&AppLovinMax::init);
    register_method("isInited",&AppLovinMax::isInited);
    register_method("setUserId",&AppLovinMax::setUserId);
    register_method("isGdprApplies",&AppLovinMax::isGdprApplies);
    register_method("debugMediation",&AppLovinMax::debugMediation);
    register_method("setGdprConsent",&AppLovinMax::setGdprConsent);
    register_method("setAgeRestricted",&AppLovinMax::setAgeRestricted);
    register_method("setCCPAApplied",&AppLovinMax::setCCPAApplied);
    register_method("loadBanner",&AppLovinMax::loadBanner);
    register_method("showBanner",&AppLovinMax::showBanner);
    register_method("hideBanner",&AppLovinMax::hideBanner);
    register_method("loadInterstitial",&AppLovinMax::loadInterstitial);
    register_method("showInterstitial",&AppLovinMax::showInterstitial);
    register_method("loadRewardedVideo",&AppLovinMax::loadRewardedVideo);
    register_method("showRewardedVideo",&AppLovinMax::showRewardedVideo);
    register_method("resize",&AppLovinMax::resize);
    register_method("getBannerWidth",&AppLovinMax::getBannerWidth);
    register_method("getBannerHeight",&AppLovinMax::getBannerHeight);
}
