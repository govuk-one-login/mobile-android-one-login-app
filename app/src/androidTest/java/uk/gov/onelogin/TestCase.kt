package uk.gov.onelogin

import android.content.Context
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider

abstract class TestCase {
    protected val context: Context = ApplicationProvider.getApplicationContext()

    protected val resources: Resources = context.resources
}
