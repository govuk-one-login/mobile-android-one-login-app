package uk.gov.onelogin.utils

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.SemanticsNodeInteractionCollection
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.unit.Density
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * An implementation of [ComposeTestRule] that will correctly reset itself in-between test retries.
 *
 * NOTE: In order to function properly this rule should be wrapped by your retry rule.
 *
 * This is necessary because there is currently no ability to reset the
 * [AndroidComposeUiTestEnvironment] in the standard [AndroidComposeTestRule].
 *
 * @param ruleProvider Function to create the underlying ComposeTestRule rule, defaults to [createEmptyComposeRule]
 */
class RetryableComposeTestRule constructor(
    private val ruleProvider: () -> ComposeTestRule = ::createEmptyComposeRule
) :
    ComposeTestRule {
    private var rule: ComposeTestRule = ruleProvider.invoke()

    override val density: Density
        get() = rule.density
    override val mainClock: MainTestClock
        get() = rule.mainClock

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    rule.apply(base, description).evaluate()
                } finally {
                    rule = ruleProvider.invoke()
                }
            }
        }
    }

    override suspend fun awaitIdle() = rule.awaitIdle()

    override fun onAllNodes(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean
    ): SemanticsNodeInteractionCollection = rule.onAllNodes(matcher, useUnmergedTree)

    override fun onNode(matcher: SemanticsMatcher, useUnmergedTree: Boolean): SemanticsNodeInteraction =
        rule.onNode(matcher, useUnmergedTree)

    override fun registerIdlingResource(idlingResource: IdlingResource) =
        rule.registerIdlingResource(idlingResource)

    override fun <T> runOnIdle(action: () -> T): T =
        rule.runOnIdle(action)

    override fun <T> runOnUiThread(action: () -> T): T =
        rule.runOnUiThread(action)

    override fun unregisterIdlingResource(idlingResource: IdlingResource) =
        rule.unregisterIdlingResource(idlingResource)

    override fun waitForIdle() = rule.waitForIdle()

    override fun waitUntil(timeoutMillis: Long, condition: () -> Boolean) =
        rule.waitUntil(timeoutMillis, condition)

    @ExperimentalTestApi
    override fun waitUntilNodeCount(
        matcher: SemanticsMatcher,
        count: Int,
        timeoutMillis: Long
    ) {
        rule.waitUntilNodeCount(matcher, count, timeoutMillis)
    }

    @ExperimentalTestApi
    override fun waitUntilAtLeastOneExists(
        matcher: SemanticsMatcher,
        timeoutMillis: Long
    ) {
        rule.waitUntilAtLeastOneExists(matcher, timeoutMillis)
    }

    @ExperimentalTestApi
    override fun waitUntilExactlyOneExists(
        matcher: SemanticsMatcher,
        timeoutMillis: Long
    ) {
        rule.waitUntilExactlyOneExists(matcher, timeoutMillis)
    }

    @ExperimentalTestApi
    override fun waitUntilDoesNotExist(
        matcher: SemanticsMatcher,
        timeoutMillis: Long
    ) {
        rule.waitUntilDoesNotExist(matcher, timeoutMillis)
    }
}
