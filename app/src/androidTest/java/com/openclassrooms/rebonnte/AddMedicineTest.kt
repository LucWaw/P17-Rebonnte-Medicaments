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
class AddMedicineTest {


    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun testAddMedicine() {

        composeTestRule.waitUntil(timeoutMillis = 50000) {
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