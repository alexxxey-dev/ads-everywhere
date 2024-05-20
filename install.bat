adb uninstall com.ads.instagram

adb install C:\Users\disco\Desktop\projects_2.0\android\AdsInstagram\app\build\outputs\apk\debug\ads.apk

adb shell settings put secure enabled_accessibility_services %accessibility:com.ads.instagram/com.ads.everywhere.service.AcsbService

adb shell am start -n com.ads.instagram/com.ads.instagram.MainActivity


