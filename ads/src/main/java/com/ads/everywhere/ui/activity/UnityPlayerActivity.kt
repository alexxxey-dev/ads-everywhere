package com.ads.everywhere.ui.activity

import com.ads.everywhere.data.IronSourceController

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import androidx.annotation.Keep
import com.ads.everywhere.ui.activity.AdActivity.Companion.SHOW_INTERSTITIAL
import com.ads.everywhere.ui.activity.AdActivity.Companion.STOP
import com.ads.everywhere.util.Logs
import com.ads.everywhere.util.ext.createReceiver
import com.ads.everywhere.util.ext.destroyReceiver
import com.ironsource.mediationsdk.IronSource
import com.unity3d.player.IUnityPlayerLifecycleEvents
import com.unity3d.player.UnityPlayer

@Keep
class UnityPlayerActivity : Activity(), IUnityPlayerLifecycleEvents {
    companion object {

        const val TAG = "IRON_SOURCE_SERVICE"
    }

    // don't change the name of this variable; referenced from native code
    protected var mUnityPlayer: UnityPlayer? = null


    private fun checkInterstitial() {
        val showInterstitial = intent?.getBooleanExtra(SHOW_INTERSTITIAL, false) ?: false
        if (showInterstitial) {
            Logs.log(IronSourceController.TAG, "unityActivity| showInterstitial")
            IronSource.showInterstitial(this)
        }
    }

    private fun checkStop() {
        val stop = intent?.getBooleanExtra(STOP, false) ?: false
        if (stop) {
            Logs.log(IronSourceController.TAG, "unityActivity| hideInterstitial")
            finishAffinity()
        }
    }

    // Setup activity layout
    protected override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        Logs.log(IronSourceController.TAG, "unityActivity| onCreate")

        checkStop()
        checkInterstitial()

        val cmdLine: String? = updateUnityCommandLineArguments(intent.getStringExtra("unity"))
        intent.putExtra("unity", cmdLine)

        mUnityPlayer = UnityPlayer(this, this)
        setContentView(mUnityPlayer)
        mUnityPlayer?.requestFocus()


    }

    protected override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Logs.log(TAG, "unityActivity| onNewIntent")
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        checkStop()
        checkInterstitial()
        setIntent(intent)
        mUnityPlayer?.newIntent(intent)
    }

    // Quit Unity
    protected override fun onDestroy() {
        mUnityPlayer?.destroy()
        Logs.log(TAG, "unityActivity| onDestroy")
        super.onDestroy()
    }

    // When Unity player unloaded move task to background
    override fun onUnityPlayerUnloaded() {
        Logs.log(TAG, "unityActivity| onUnityPlayerUnloaded")
        moveTaskToBack(true)
    }

    // Callback before Unity player process is killed
    override fun onUnityPlayerQuitted() {
        Logs.log(TAG, "unityActivity| onUnityPlayerQuit")
    }

    // Override this in your custom UnityPlayerActivity to tweak the command line arguments passed to the Unity Android Player
    // The command line arguments are passed as a string, separated by spaces
    // UnityPlayerActivity calls this from 'onCreate'
    // Supported: -force-gles20, -force-gles30, -force-gles31, -force-gles31aep, -force-gles32, -force-gles, -force-vulkan
    // See https://docs.unity3d.com/Manual/CommandLineArguments.html
    // @param cmdLine the current command line arguments, may be null
    // @return the modified command line string or null
    protected fun updateUnityCommandLineArguments(cmdLine: String?): String? {
        Logs.log(
            IronSourceController.TAG,
            "unity activity| updateUnityCommandLineArguments| cmdLine=$cmdLine"
        )
        return cmdLine
    }


    // Pause Unity
    protected override fun onPause() {
        super.onPause()
        Logs.log(TAG, "unity activity| onPause")
        mUnityPlayer?.pause()
    }

    // Resume Unity
    protected override fun onResume() {
        super.onResume()

        Logs.log(TAG, "unity activity| onResume")

        mUnityPlayer?.resume()
    }


    // Low Memory Unity
    override fun onLowMemory() {
        super.onLowMemory()
        Logs.log(TAG, "unity activity| onLowMemory")
        mUnityPlayer?.lowMemory()
    }

    // Trim Memory Unity
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Logs.log(TAG, "unity activity| onTrimMemory")
        if (level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer?.lowMemory()
        }
    }

    // This ensures the layout will be correct.
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Logs.log(TAG, "unity activity| onConfigurationChanged")
        mUnityPlayer?.configurationChanged(newConfig)
    }

    // Notify Unity of the focus change.
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        Logs.log(TAG, "unity activity| onWindowFocusChanged")
        mUnityPlayer?.windowFocusChanged(hasFocus)
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        Logs.log(TAG, "unity activity| dispatchKeyEvent| event=$event")
        if (event?.action == KeyEvent.ACTION_MULTIPLE) {
            return mUnityPlayer?.injectEvent(event) ?: super.dispatchKeyEvent(event)
        }
        return super.dispatchKeyEvent(event)
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Logs.log(TAG, "unity activity| onKeyUp| keyCode=$keyCode| event=$event")
        return mUnityPlayer?.injectEvent(event) ?: super.onKeyUp(keyCode, event)
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        Logs.log(TAG, "unity activity| onKeyDown| keyCode=$keyCode| event=$event")
        return mUnityPlayer?.injectEvent(event) ?: super.onKeyDown(keyCode, event)
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Logs.log(TAG, "unity activity| onTouchEvent| event=$event")
        return mUnityPlayer?.injectEvent(event) ?: super.onTouchEvent(event)
    }

    override fun onGenericMotionEvent(event: MotionEvent?): Boolean {
        Logs.log(TAG, "unity activity| onGenericMotionEvent| event=$event")
        return mUnityPlayer?.injectEvent(event) ?: super.onGenericMotionEvent(event)
    }
}