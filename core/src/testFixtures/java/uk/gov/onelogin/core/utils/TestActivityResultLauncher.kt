package uk.gov.onelogin.core.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat

/**
 * A no-op [ActivityResultLauncher].
 */
class TestActivityResultLauncher<I> : ActivityResultLauncher<I>() {
    override val contract: ActivityResultContract<I, *>
        get() = error("Not implemented")

    override fun launch(input: I, options: ActivityOptionsCompat?) {
        // no-op
    }

    override fun unregister() {
        // no-op
    }
}
