package uk.gov.onelogin.core.extensions

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.extension.AfterEachCallback
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

@ExperimentalCoroutinesApi
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
class CoroutinesTestExtension(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) : BeforeEachCallback,
    AfterEachCallback {
    /**
     * Set TestCoroutine dispatcher as main
     */
    override fun beforeEach(context: ExtensionContext?) {
        Dispatchers.setMain(dispatcher)
    }

    override fun afterEach(context: ExtensionContext?) {
        Dispatchers.resetMain()
    }
}
