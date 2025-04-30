package com.openclassrooms.rebonnte

import android.graphics.Bitmap
import android.os.Environment
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File
import java.io.FileOutputStream

class ScreenshotWatcher : TestWatcher() {

    fun takeScreenshot(name: String) {
        val picturesDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), "TestScreenshots"
        )

        if (!picturesDir.exists()) {
            picturesDir.mkdirs() // Crée le dossier s’il n’existe pas
        }

        val screenshotFile = File(picturesDir, "$name.png")

        val bitmap: Bitmap = InstrumentationRegistry.getInstrumentation()
            .uiAutomation.takeScreenshot()

        FileOutputStream(screenshotFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
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

