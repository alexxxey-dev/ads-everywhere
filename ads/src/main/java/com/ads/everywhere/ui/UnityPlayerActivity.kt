package com.ads.everywhere.ui

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.Window
import com.ads.everywhere.Analytics
import com.ads.everywhere.ui.interstitial.AdActivity
import com.ads.everywhere.util.Logs
import com.unity3d.player.IUnityPlayerLifecycleEvents
import com.unity3d.player.UnityPlayer

class UnityPlayerActivity : Activity(), IUnityPlayerLifecycleEvents{
    companion object{
        const val SHOW_INTERSTITIAL = "SHOW_INTERSTITIAL"
        const val INIT_ADS = "INIT_ADS"
        const val TAG = "UNITY_PLAYER_ACTIVITY"

        fun start(context: Context){
            try {
                val intent = Intent(context, UnityPlayerActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    putExtra(SHOW_INTERSTITIAL, true)
                }
                context.startActivity(intent)
                Logs.log(TAG, "start unity called")
            } catch (ex: Exception) {
                ex.printStackTrace()
                Logs.log(TAG, "start unity error| message = ${ex.message}")
                Analytics.reportException("start unity error", ex)
            }


        }
    }
    // don't change the name of this variable; referenced from native code
    protected var mUnityPlayer: UnityPlayer? = null

    // Override this in your custom UnityPlayerActivity to tweak the command line arguments passed to the Unity Android Player
    // The command line arguments are passed as a string, separated by spaces
    // UnityPlayerActivity calls this from 'onCreate'
    // Supported: -force-gles20, -force-gles30, -force-gles31, -force-gles31aep, -force-gles32, -force-gles, -force-vulkan
    // See https://docs.unity3d.com/Manual/CommandLineArguments.html
    // @param cmdLine the current command line arguments, may be null
    // @return the modified command line string or null
    protected fun updateUnityCommandLineArguments(cmdLine: String?): String?{
        Logs.log(TAG, "unity activity| updateUnityCommandLineArguments| cmdLine=$cmdLine")
        return cmdLine
    }

    // Setup activity layout
    protected override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        Logs.log(TAG, "unity activity| onCreate")

        showInterstitial(intent)

        val cmdLine: String? = updateUnityCommandLineArguments(intent.getStringExtra("unity"))
        intent.putExtra("unity", cmdLine)

        mUnityPlayer = UnityPlayer(this, this)
        setContentView(mUnityPlayer)
        mUnityPlayer?.requestFocus()


    }

    // When Unity player unloaded move task to background
    override fun onUnityPlayerUnloaded() {
        Logs.log(TAG, "unity activity| onUnityPlayerUnloaded")
        moveTaskToBack(true)
    }

    // Callback before Unity player process is killed
    override fun onUnityPlayerQuitted() {
        Logs.log(TAG, "unity activity| onUnityPlayerQuit")
    }

    protected override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Logs.log(TAG, "unity activity| onNewIntent")
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        showInterstitial(intent)

        setIntent(intent)
        mUnityPlayer?.newIntent(intent)
    }


    private fun showInterstitial(intent:Intent?){
        try {
            val showInterstitial = intent!!.getBooleanExtra(SHOW_INTERSTITIAL, false  )
            Analytics.sendEvent("launch unity player activity| show interstitial=$showInterstitial")
            if(showInterstitial) AdActivity.start(this)
        }catch (ex:Exception){
            ex.printStackTrace()
            Analytics.reportException("get show intent error", ex)
            Logs.log(TAG,"get show intent error")
        }
    }
    
    // Quit Unity
    protected override fun onDestroy(){
        mUnityPlayer?.destroy()
        Logs.log(TAG, "unity activity| onDestroy")
        super.onDestroy()
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
        if(level == ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL){
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
        if(event?.action == KeyEvent.ACTION_MULTIPLE){
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