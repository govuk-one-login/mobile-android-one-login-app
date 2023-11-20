package uk.gov.onelogin

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.test.espresso.accessibility.AccessibilityChecks
import dagger.hilt.android.testing.HiltTestApplication
import io.qameta.allure.android.runners.AllureAndroidJUnitRunner

open class InstrumentationTestRunner : AllureAndroidJUnitRunner() {

    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(
            cl,
            HiltTestApplication::class.java.name,
            context
        )
    }

    override fun onCreate(arguments: Bundle) {
        super.onCreate(arguments)
        AccessibilityChecks.enable().setRunChecksFromRootView(true)
    }

    override fun finish(resultCode: Int, results: Bundle?) {
        AccessibilityChecks.disable()
        super.finish(resultCode, results)
    }
}
