//
//  ALAdSize.h
//  AppLovinSDK
//
//  Created by Basil on 2/27/12.
//  Copyright © 2020 AppLovin Corporation. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * This class defines the possible sizes of an ad.
 */
@interface ALAdSize : NSObject

/**
 * Represents the size of a 320x50 banner advertisement.
 */
@property (class, nonatomic, strong, readonly) ALAdSize *banner;

/**
 * Represents the size of a 728x90 leaderboard advertisement (for tablets).
 */
@property (class, nonatomic, strong, readonly) ALAdSize *leader;

/**
 * Represents the size of a full-screen advertisement.
 */
@property (class, nonatomic, strong, readonly) ALAdSize *interstitial;


- (instancetype)init NS_UNAVAILABLE;
+ (instancetype)new NS_UNAVAILABLE;

@end

@interface ALAdSize(ALDeprecated)
@property (assign, nonatomic, readonly) CGFloat width __deprecated;
@property (assign, nonatomic, readonly) CGFloat height __deprecated;
+ (NSArray *)allSizes __deprecated_msg("Retrieval of all sizes is deprecated and will be removed in a future SDK version.");
+ (ALAdSize *)sizeWithLabel:(NSString *)label orDefault:(ALAdSize *)defaultSize __deprecated_msg("Custom ad sizes are no longer supported; use an existing singleton size like ALAdSize.banner");
@property (nonatomic, copy, readonly) NSString *label __deprecated_msg("Retrieval of underlying string is deprecated and will be removed in a future SDK version.");
+ (ALAdSize *)sizeBanner __deprecated_msg("Class method `sizeBanner` is deprecated and will be removed in a future SDK version. Please use ALAdSize.banner instead.");
+ (ALAdSize *)sizeMRec __deprecated_msg("Class method `sizeMRec` is deprecated and will be removed in a future SDK version. Please use ALAdSize.mrec instead.");
+ (ALAdSize *)sizeLeader __deprecated_msg("Class method `sizeLeader` is deprecated and will be removed in a future SDK version. Please use ALAdSize.leader instead.");
+ (ALAdSize *)sizeInterstitial __deprecated_msg("Class method `sizeInterstitial` is deprecated and will be removed in a future SDK version. Please use ALAdSize.interstitial instead.");
@property (class, nonatomic, strong, readonly) ALAdSize *mrec __deprecated_msg("MRECs have been deprecated and will be removed in a future SDK version.");
@property (nonatomic, strong, readonly, class) ALAdSize *sizeNative __deprecated_msg("Native ads have been deprecated and will be removed in a future SDK version.");
@end

NS_ASSUME_NONNULL_END
