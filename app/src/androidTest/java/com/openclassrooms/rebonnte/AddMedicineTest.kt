package com.openclassrooms.rebonnte

import android.util.Log
import android.view.View
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddMedicineTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val screenshotWatcher = ScreenshotWatcher()


    fun waitUntilViewIsDisplayed(matcher: org.hamcrest.Matcher<View>, timeout: Long = 5000L) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + timeout
        var lastError: Throwable? = null

        do {
            try {
                onView(matcher).check(matches(isDisplayed()))
                return
            } catch (e: Throwable) {
                lastError = e
                Thread.sleep(100)
            }
        } while (System.currentTimeMillis() < endTime)

        throw lastError
    }


    @Test
    fun testAddMedicine() {
        val authActivityIdle =
            ActivityResumedIdlingResource("com.firebase.ui.auth.ui.email.EmailActivity") // ou "AuthMethodPickerActivity"
        IdlingRegistry.getInstance().register(authActivityIdle)


        ScreenshotWatcher().takeScreenshot("Starting TEST")



        onView(withHint("Email")).perform(replaceText("fakehhkgugugugufugubkdt@mail.com"))


        InstrumentationRegistry.getInstrumentation().runOnMainSync {
            val activity = ActivityLifecycleMonitorRegistry
                .getInstance()
                .getActivitiesInStage(Stage.RESUMED)
                .firstOrNull()

            println("🔥 Current activity name: ${activity?.javaClass?.name}")
            Log.d(
                "Current activity name",
                "Current activity name: ${activity?.javaClass?.name}"
            )
        }


        onView(withText("SIGN IN")).perform(click())

        onView(withHint("Password")).perform(replaceText("Gjgjgjvkfyfuvk"))

        onView(withText("SIGN IN")).check(matches(isCompletelyDisplayed())).perform(click())


        IdlingRegistry.getInstance().unregister(authActivityIdle)

        composeTestRule
            .onNodeWithText("Medicine")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag(
                    "addMedicineFabButton"
                )
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag("addMedicineFabButton")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag(
                    "MedicineNameField"
                )
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(
                "MedicineNameField"
            ).performTextInput("Paracétamol")


        composeTestRule
            .onNodeWithText("Select an Aisle")
            .performClick()


        composeTestRule
            .onNodeWithText("Main aisle")
            .performClick()


        composeTestRule.onNodeWithText("Stock Quantity")
            .performTextInput("10")


        //Click on the "save" button
        composeTestRule
            .onNodeWithText("Save Medicine")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule.onNodeWithTag("LazyMedicine")
                .isDisplayed()
        }


        composeTestRule.waitUntil(timeoutMillis = 50000) {
            try {
                composeTestRule.onNodeWithTag("LazyMedicine")
                    .performScrollToNode(hasText("Paracétamol"))
            } catch (_: Exception) {
                println("testAddEvent : LazyEvent not found")
            } catch (_: AssertionError) {
                println("testAddEvent : LazyEvent not found")
            }

            composeTestRule
                .onNodeWithText("Paracétamol")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("Paracétamol")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag("deleteMedicine")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("deleteMedicine")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithText("Yes")
                .isDisplayed()
        }

        composeTestRule.onNodeWithText("Yes").performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag("LazyMedicine")
                .isDisplayed()
        }

        composeTestRule.onNodeWithText("Account").performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithText("Sign out")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("Sign out")
            .performClick()

    }


}