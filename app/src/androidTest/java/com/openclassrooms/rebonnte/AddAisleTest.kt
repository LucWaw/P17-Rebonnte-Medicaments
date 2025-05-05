package com.openclassrooms.rebonnte

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class AddAisleTest {


    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun testAddAisle() {

        // pour verifier qu'on est bien connécté
        composeTestRule.waitUntil(timeoutMillis = 50000) { //on test sur medicine car il sera enabled alors que aisle sera juste affiché apres la connexion car on est déjà dessus
            try {
                composeTestRule
                    .onNodeWithText("Medicine")
                    .assertIsDisplayed()            // First, check if it's visible
                    .assertIsEnabled()              // THEN, check if it's enabled
                true // If both assertions pass, the condition is met
            } catch (_: AssertionError) {
                // If either assertion fails, catch the error and return false
                // This tells waitUntil to keep trying
                false
            }
        }

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag(
                    "addAisleFabButton"
                )
                .isDisplayed()
        }
        composeTestRule
            .onNodeWithTag("addAisleFabButton")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag(
                    "AisleNameTextField"
                )
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag(
                "AisleNameTextField"
            ).performTextInput("MedicineGeneric")

        composeTestRule
            .onNodeWithTag("addAisleButton")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule.onNodeWithTag("LazyAisle")
                .isDisplayed()
        }

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            try {
                composeTestRule.onNodeWithTag("LazyAisle")
                    .performScrollToNode(hasText("MedicineGeneric"))
            } catch (_: Exception) {
                println("testAddAisle : LazyEvent not found")
            } catch (_: AssertionError) {
                println("testAddAisle : LazyEvent not found")
            }

            composeTestRule
                .onNodeWithText("MedicineGeneric")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("MedicineGeneric")
            .performClick()


        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag("deleteAisle")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("deleteAisle")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithText("Yes")
                .isDisplayed()
        }

        composeTestRule.onNodeWithText("Yes").performClick()

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag("LazyAisle")
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