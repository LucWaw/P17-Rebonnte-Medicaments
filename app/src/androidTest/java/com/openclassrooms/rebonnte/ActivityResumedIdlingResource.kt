package com.openclassrooms.rebonnte

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.IdlingResource
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage

class ActivityResumedIdlingResource(
    private val expectedActivityClassName: String
) : IdlingResource {

    @Volatile
    private var isIdle = false
    private var callback: IdlingResource.ResourceCallback? = null

    private val lifecycleCallback = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            if (activity.javaClass.name.contains(expectedActivityClassName)) {
                isIdle = true
                callback?.onTransitionToIdle()
            }
        }

        // autres callbacks non utilisés
        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStarted(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {}
        override fun onActivityDestroyed(activity: Activity) {}
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    }

    override fun getName() = "ActivityResumedIdlingResource: $expectedActivityClassName"

    override fun isIdleNow() = isIdle

    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback) {
        this.callback = callback

        val app = ApplicationProvider.getApplicationContext<Application>()
        app.registerActivityLifecycleCallbacks(lifecycleCallback)

        // Évite l'appel illégal si déjà sur le main thread
        if (Looper.myLooper() != Looper.getMainLooper()) {
            InstrumentationRegistry.getInstrumentation().runOnMainSync {
                val activity = ActivityLifecycleMonitorRegistry
                    .getInstance()
                    .getActivitiesInStage(Stage.RESUMED)
                    .firstOrNull()

                val currentActivityName = activity?.javaClass?.name
                Log.d("IdleDebug", "Immediately visible activity: $currentActivityName")

                if (currentActivityName?.contains(expectedActivityClassName) == true) {
                    isIdle = true
                    callback.onTransitionToIdle()
                }
            }
        } else {
            // Si déjà sur le main thread, fais-le directement (sans `runOnMainSync`)
            val activity = ActivityLifecycleMonitorRegistry
                .getInstance()
                .getActivitiesInStage(Stage.RESUMED)
                .firstOrNull()

            val currentActivityName = activity?.javaClass?.name
            Log.d("IdleDebug", "Immediately visible activity: $currentActivityName")

            if (currentActivityName?.contains(expectedActivityClassName) == true) {
                isIdle = true
                callback.onTransitionToIdle()
            }
        }
    }


}