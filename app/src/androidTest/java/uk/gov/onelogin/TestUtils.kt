package uk.gov.onelogin

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData

object TestUtils {
    val data = AppInfoData(
        apps = AppInfoData.App(
            AppInfoData.AppInfo(
                minimumVersion = "1.0.0",
                releaseFlags = AppInfoData.ReleaseFlags(
                    true,
                    true,
                    true
                ),
                available = true,
                featureFlags = AppInfoData.FeatureFlags(true)
            )
        )
    )

    fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.setActivity(
        action: () -> Unit
    ) {
        this.activityRule.scenario.onActivity { action() }
    }

    fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.back() {
        this.activityRule.scenario.onActivity { activity ->
            activity.onBackPressedDispatcher.onBackPressed()
        }
    }
}
