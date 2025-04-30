package com.openclassrooms.rebonnte


import android.graphics.Bitmap
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScreenshotWatcher : TestWatcher() {

    fun takeScreenshot(name: String) {
        val screenshotsDir = File("/sdcard/Download/TestScreenshots")

        if (!screenshotsDir.exists() && !screenshotsDir.mkdirs()) {
            Log.e(
                "ScreenshotWatcher",
                "Impossible de créer le dossier: ${screenshotsDir.absolutePath}"
            )
            return
        }

        val timeStamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date())
        val screenshotFile = File(screenshotsDir, "$name-$timeStamp.png")

        try {
            val bitmap: Bitmap = InstrumentationRegistry.getInstrumentation()
                .uiAutomation.takeScreenshot()

            FileOutputStream(screenshotFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                Log.i(
                    "ScreenshotWatcher",
                    "Screenshot sauvegardé dans: ${screenshotFile.absolutePath}"
                )
            }
        } catch (e: Exception) {
            Log.e("ScreenshotWatcher", "Erreur lors de la capture d'écran", e)
        }
    }

    override fun starting(description: Description) {
        takeScreenshot("START-${description.methodName}")
    }

    override fun failed(e: Throwable?, description: Description) {
        takeScreenshot("FAILED-${description.methodName}")
    }

    override fun succeeded(description: Description) {
        takeScreenshot("SUCCESS-${description.methodName}")
    }
}
