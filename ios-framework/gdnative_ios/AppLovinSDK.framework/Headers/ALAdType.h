//
//  ALAdType.h
//  AppLovinSDK
//
//  Copyright © 2020 AppLovin Corporation. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN

/**
 * This class defines the possible types of an interstitial ad (i.e. regular or incentivized/rewarded).
 */
@interface ALAdType : NSObject

/**
 * Represents a standard advertisement that does not provide a reward to the user.
 */
@property (class, nonatomic, strong, readonly) ALAdType *regular;

/**
 * Represents a rewarded ad which will provide the user virtual currency upon completion.
 */
@property (class, nonatomic, strong, readonly) ALAdType *incentivized;

/**
 * Represents a rewarded interstitial ad which the user can skip and be granted a reward upon successful completion of the ad.
 */
@property (class, nonatomic, strong, readonly) ALAdType *autoIncentivized;

- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)new NS_UNAVAILABLE;

@end

@interface ALAdType(ALDeprecated)
+ (ALAdType *)typeNative __deprecated;
+ (NSArray *)allTypes __deprecated_msg("Retrieval of all types is deprecated and will be removed in a future SDK version.");
@property (copy, nonatomic, readonly) NSString *label __deprecated_msg("Retrieval of underlying string is deprecated and will be removed in a future SDK version.");
+ (ALAdType *)typeRegular __deprecated_msg("Class method `typeRegular` is deprecated and will be removed in a future SDK version. Please use ALAdType.regular instead.");
+ (ALAdType *)typeIncentivized __deprecated_msg("Class method `typeIncentivized` is deprecated and will be removed in a future SDK version. Please use ALAdType.incentivized instead.");
@end

NS_ASSUME_NONNULL_END
