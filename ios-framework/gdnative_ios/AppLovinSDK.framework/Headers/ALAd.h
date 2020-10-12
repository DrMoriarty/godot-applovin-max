//
//  ALAd.h
//  AppLovinSDK
//
//  Copyright © 2020 AppLovin Corporation. All rights reserved.
//

#import "ALAdSize.h"
#import "ALAdType.h"

NS_ASSUME_NONNULL_BEGIN

/**
 * This class represents an ad that has been served from the AppLovin server.
 */
@interface ALAd : NSObject<NSCopying>

/**
 * The size of this ad.
 */
@property (nonatomic, strong, readonly) ALAdSize *size;

/**
 * The type of this ad.
 */
@property (nonatomic, strong, readonly) ALAdType *type;

/**
 * The zone id for the ad, if any.
 */
@property (nonatomic, copy, readonly, nullable) NSString *zoneIdentifier;

/**
 * Whether or not the current ad is a video advertisement.
 */
@property (nonatomic, assign, readonly, getter=isVideoAd) BOOL videoAd;

/**
 * Get an arbitrary ad value for a given key. The list of keys may be found in AppLovin documentation online.
 */
- (nullable NSString *)adValueForKey:(NSString *)key;

/**
 * A unique ID which identifies this advertisement.
 *
 * Should you need to report a broken ad to AppLovin support, please include this number's longValue.
 */
@property (nonatomic, strong, readonly) NSNumber *adIdNumber;


- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)new NS_UNAVAILABLE;

@end

@interface ALAd (ALDeprecated)
@property (strong, readonly, getter=size, nullable) ALAdSize *adSize __deprecated_msg("Use size property instead.");
@property (strong, readonly, getter=type, nullable) ALAdType *adType __deprecated_msg("Use type property instead.");
@end

NS_ASSUME_NONNULL_END
