//
//  ALSdkSettings.h
//  AppLovinSDK
//
//  Copyright © 2020 AppLovin Corporation. All rights reserved.
//

NS_ASSUME_NONNULL_BEGIN

/**
 * This class contains settings for the AppLovin SDK.
 */
@interface ALSdkSettings : NSObject

/**
 * Toggle verbose logging for the SDK. This is set to NO by default. Set to NO if SDK should be silent (recommended for App Store submissions).
 *
 * If enabled AppLovin messages will appear in standard application log accessible via console.
 * All log messages will be prefixed by the "AppLovinSdk" tag.
 *
 * Verbose logging is <i>disabled</i> by default.
 */
@property (nonatomic, assign) BOOL isVerboseLogging;

/**
 * Determines whether to begin video ads in a muted state or not. Defaults to NO unless changed in the dashboard.
 */
@property (nonatomic, assign) BOOL muted;

/**
 * Determines whether the ad info button will be displayed on fullscreen ads (debug and TestFlight builds only).
 */
@property (nonatomic, assign) BOOL adInfoButtonEnabled;

/**
 * Enable devices to receive test ads, by passing in the advertising identifier (IDFA) of each test device.
 * Refer to AppLovin logs for the IDFA of your current device.
 */
@property (nonatomic, copy) NSArray<NSString *> *testDeviceAdvertisingIdentifiers;

/**
 * The MAX ad unit ids that will be used for this instance of the SDK. 3rd-party SDKs will be initialized with the credentials configured for these ad unit ids.
 */
@property (nonatomic, copy) NSArray<NSString *> *initializationAdUnitIdentifiers;

+ (instancetype)new NS_UNAVAILABLE;

@end

NS_ASSUME_NONNULL_END
