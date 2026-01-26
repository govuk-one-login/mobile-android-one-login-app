package uk.gov.onelogin.e2e.controller

import org.junit.rules.TestName
import org.junit.runner.Description

/**
 * [org.junit.Rule] that captures the name of the currently running test class.
 *
 * @param shouldLogToConsole Defaults to `false`. When true, the class makes additional print
 * statements during the proceeding state changes:
 *
 * - [failed]
 * - [finished]
 * - [starting]
 * - [succeeded]
 */
class TestCaseNameWatcher(
    private val shouldLogToConsole: Boolean = false,
) : TestName() {
    @Volatile
    private var _className: String? = null

    /**
     * @return the name of the currently running test class
     */
    val className get() = _className

    override fun starting(d: Description) =
        super.starting(d).also {
            _className = d.className

            logToConsole("Starting")
        }

    override fun finished(description: Description) =
        super.finished(description).also {
            logToConsole("Finished")
        }

    override fun succeeded(description: Description?) =
        super.succeeded(description).also {
            logToConsole("Succeeded")
        }

    override fun failed(
        e: Throwable?,
        description: Description?,
    ) = super.failed(e, description).also {
        logToConsole("Failed")
    }

    private fun logToConsole(middle: String) {
        if (shouldLogToConsole) {
            println(
                "EndtoEndTests: $middle $className.$methodName",
            )
        }
    }
}
