package uk.gov.onelogin

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule

object TestUtils {
    fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.setNavInitialPoint(
        action: () -> Unit
    ) {
        this.activityRule.scenario.onActivity { action() }
    }
}
