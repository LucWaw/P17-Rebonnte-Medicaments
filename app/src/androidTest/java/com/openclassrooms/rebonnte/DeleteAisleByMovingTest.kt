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
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class DeleteAisleByMovingTest {


    @get:Rule(order = 1)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun testDeleteAisleByMoving() {

        //CREATE AISLE


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
            ).performTextInput("MedicineGeneric2")

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
                    .performScrollToNode(hasText("MedicineGeneric2"))
            } catch (_: Exception) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            } catch (_: AssertionError) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            }

            composeTestRule
                .onNodeWithText("MedicineGeneric2")
                .isDisplayed()
        }


        //CREATE MEDICINE IN THE NEW AISLE

        composeTestRule.onNodeWithText("Medicine")
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
            ).performTextInput("NewGenericToMove")


        composeTestRule
            .onNodeWithText("Select an Aisle")
            .performClick()


        composeTestRule
            .onNodeWithText("MedicineGeneric2")
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
                    .performScrollToNode(hasText("NewGenericToMove"))
            } catch (_: Exception) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            } catch (_: AssertionError) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            }

            composeTestRule
                .onNodeWithText("NewGenericToMove")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithText("Aisle")
            .performClick()


        composeTestRule
            .onNodeWithText("MedicineGeneric2")
            .performClick()


        //DELETE AISLE

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag("deleteAisle")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("deleteAisle")
            .performClick()

        //WAIT UNTIL THE DIALOG IS DISPLAYED COMPLETELY

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule.onNodeWithTag("movingAllRadio").isDisplayed()
        }

        composeTestRule.onNodeWithTag("movingAllRadio").performClick()



        composeTestRule.onNodeWithText("Yes")
            .performClick()


        //Test if is moved

        composeTestRule.waitUntil(timeoutMillis = 50000) {
            try {
                composeTestRule.onNodeWithTag("LazyAisle")
                    .performScrollToNode(hasText("Main aisle"))
            } catch (_: Exception) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            } catch (_: AssertionError) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            }

            composeTestRule
                .onNodeWithText("Main aisle")
                .isDisplayed()
        }


        composeTestRule.onNodeWithText(
            "Main aisle"
        ).performClick()


        composeTestRule.waitUntil(timeoutMillis = 50000) {
            try {
                composeTestRule.onNodeWithTag("AisleDetailLazyMedicine")
                    .performScrollToNode(hasText("NewGenericToMove"))
            } catch (_: Exception) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            } catch (_: AssertionError) {
                println("testDeleteAisleByMoving : LazyEvent not found")
            }

            composeTestRule
                .onNodeWithText("NewGenericToMove")
                .isDisplayed()
        }

        //Delete the medicine

        composeTestRule
            .onNodeWithText("NewGenericToMove")
            .performClick()


        composeTestRule.waitUntil(timeoutMillis = 50000) {
            composeTestRule
                .onNodeWithTag("deleteMedicine")
                .isDisplayed()
        }

        composeTestRule
            .onNodeWithTag("deleteMedicine")
            .performClick()

        composeTestRule.onNodeWithText("Yes")
            .performClick()
    }
}