package uk.gov.onelogin

import android.app.Application
import android.content.Context
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
}
