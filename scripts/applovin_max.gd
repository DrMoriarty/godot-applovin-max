extends Node2D

var _ads = null
onready var Production = not OS.is_debug_build()
var isTop = true
var _inited = false
onready var _gdpr_provider := $'/root/ogury' if has_node('/root/ogury') else null

func _ready():
    pause_mode = Node.PAUSE_MODE_PROCESS
    if(Engine.has_singleton("AppLovinMax")):
        _ads = Engine.get_singleton("AppLovinMax")
    elif OS.get_name() == 'iOS':
        _ads = load("res://addons/applovinmax-ios/applovinmax.gdns").new()
    if _ads:
        if ProjectSettings.has_setting('AppLovin/SdkKey'):
            var sdk_key = ProjectSettings.get_setting('AppLovin/SdkKey')
            _ads.init(sdk_key, Production)
        else:
            push_error('You should set AppLovin/SdkKey to SDK initialization')
        if _gdpr_provider != null:
            _gdpr_provider.connect('gdpr_answer', self, '_on_gdpr_answer')

func _on_gdpr_answer(applies: bool, approval: bool, consent: String) -> void:
    setGdprConsent(approval)

func _process(delta: float) -> void:
    if _ads != null and not _inited:
        var i = _ads.isInited()
        if i:
            _inited = true
            var gdpr = _ads.isGdprApplies()
            _ads.setAgeRestricted(false)
            _ads.setCCPAApplied(false)

func debugMediation() -> bool:
    if _ads != null:
        _ads.debugMediation()
        return true
    else:
        return false

func isInited() -> bool:
    if _ads != null:
        return _ads.isInited()
    else:
        return false

func setUserId(uid: String) -> void:
    if _ads != null:
        _ads.setUserId(uid)

func isGdprApplies() -> bool:
    if _ads != null:
        return _ads.isGdprApplies()
    else:
        return false

func setGdprConsent(consent: bool) -> void:
    if _ads != null:
        _ads.setGdprConsent(consent)
        print('Set GDPR consent for ApplovinMAX: %s'%[var2str(consent)])

func setAgeRestricted(restricted: bool) -> void:
    if _ads != null:
        _ads.setAgeRestricted(restricted)

func setCCPAApplied(applied: bool) -> void:
    if _ads != null:
        _ads.setCCPAApplied(applied)

# Loaders

func loadBanner(id: String, isTop: bool, callback_id: int) -> bool:
    if _ads != null:
        if OS.get_name() == 'iOS':
            _ads.loadBanner(id, isTop, instance_from_id(callback_id))
        else:
            _ads.loadBanner(id, isTop, callback_id)
        return true
    else:
        return false

func loadInterstitial(id: String, callback_id: int) -> bool:
    if _ads != null:
        if OS.get_name() == 'iOS':
            _ads.loadInterstitial(id, instance_from_id(callback_id))
        else:
            _ads.loadInterstitial(id, callback_id)
        return true
    else:
        return false

func loadRewardedVideo(id: String, callback_id: int) -> bool:
    if _ads != null:
        if OS.get_name() == 'iOS':
            _ads.loadRewardedVideo(id, instance_from_id(callback_id))
        else:
            _ads.loadRewardedVideo(id, callback_id)
        return true
    else:
        return false

func loadMREC(id: String, gravity: int, callback_id: int) -> bool:
    if _ads != null:
        if OS.get_name() == 'iOS':
            _ads.loadMREC(id, gravity, instance_from_id(callback_id))
        else:
            _ads.loadMREC(id, gravity, callback_id)
        return true
    else:
        return false

# Check state

func bannerWidth(id: String) -> int:
    if _ads != null:
        var width = _ads.getBannerWidth(id)
        return width
    else:
        return 0

func bannerHeight(id: String) -> int:
    if _ads != null:
        var height = _ads.getBannerHeight(id)
        return height
    else:
        return 0

# Control

func showBanner(id: String) -> bool:
    if _ads != null:
        _ads.showBanner(id)
        return true
    else:
        return false

func hideBanner(id: String) -> bool:
    if _ads != null:
        _ads.hideBanner(id)
        return true
    else:
        return false

func removeBanner(id: String) -> bool:
    if _ads != null:
        _ads.removeBanner(id)
        return true
    else:
        return false

func showInterstitial(id: String) -> bool:
    if _ads != null:
        _ads.showInterstitial(id)
        return true
    else:
        return false

func showRewardedVideo(id: String) -> bool:
    if _ads != null:
        _ads.showRewardedVideo(id)
        return true
    else:
        return false

func showMREC(id: String) -> bool:
    if _ads != null:
        _ads.showMREC(id)
        return true
    else:
        return false

func removeMREC(id: String) -> bool:
    if _ads != null:
        _ads.removeMREC(id)
        return true
    else:
        return false
