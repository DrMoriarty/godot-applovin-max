//
//  MARewardedInterstitialAd.h
//  AppLovinSDK
//
//  Created by Thomas So on 6/3/20.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * This class represents a fullscreen ad which the user can skip and be granted a reward upon successful completion of the ad.
 */
@interface MARewardedInterstitialAd : NSObject

/**
 * Create a new MAX rewarded interstitial.
 *
 * @param adUnitIdentifier Ad unit id to load ads for.
 */
- (instancetype)initWithAdUnitIdentifier:(NSString *)adUnitIdentifier;

/**
 * Create a new MAX rewarded interstitial.
 *
 * @param adUnitIdentifier Ad unit id to load ads for.
 * @param sdk SDK to use. An instance of the SDK may be obtained by calling -[ALSdk shared].
 */
- (instancetype)initWithAdUnitIdentifier:(NSString *)adUnitIdentifier sdk:(ALSdk *)sdk;
- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)new NS_UNAVAILABLE;

/**
 * Set a delegate that will be notified about ad events.
 */
@property (nonatomic, weak, nullable) id<MARewardedAdDelegate> delegate;

/**
 * Set an extra parameter for the ad.
 *
 * @param key   Parameter key.
 * @param value Parameter value.
 */
- (void)setExtraParameterForKey:(NSString *)key value:(nullable NSString *)value;

/**
 * Load ad for the current rewarded interstitial. Use -[MARewardedInterstitialAd delegate] to assign a delegate that should be notified about ad load state.
 */
- (void)loadAd;

/**
 * Show the loaded interstitial.
 *
 * Use -[MARewardedInterstitialAd setDelegate:] to assign a delegate that should be notified about display events.
 * Use -[MARewardedInterstitialAd isReady] to check if an ad was successfully loaded.
 */
- (void)showAd;

/**
 * Show the loaded interstitial for a given placement to tie ad events to.
 *
 * Use -[MARewardedInterstitialAd setDelegate:] to assign a delegate that should be notified about display events.
 * Use -[MARewardedInterstitialAd isReady] to check if an ad was successfully loaded.
 *
 * @param placement The placement to tie the showing ad's events to.
 */
- (void)showAdForPlacement:(nullable NSString *)placement;

/**
 * Check if this ad is ready to be shown.
 */
@property (nonatomic, assign, readonly, getter=isReady) BOOL ready;

@end

NS_ASSUME_NONNULL_END
