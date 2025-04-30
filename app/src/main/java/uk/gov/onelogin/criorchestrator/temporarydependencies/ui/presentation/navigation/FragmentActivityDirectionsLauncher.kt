package uk.gov.idcheck.ui.presentation.navigation

import android.app.Activity
import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import javax.inject.Inject
import uk.gov.idcheck.sdk.IdCheckSdkActivity
import uk.gov.onelogin.idcheck.sdk.R.id as sdkId

/**
 * Navigates via the controller stored within an Android Fragment.
 *
 * Note: Injection configuration via a Hilt `Provides` function is within the depending app
 * configuration.
 *
 * @param activity The Fragment activity obtained via the Hilt `ActivityScope`.
 * @param navHostFragmentId The [IdRes] of the view which handles navigation via a
 * nav graph. Primarily a `NavHostFragment` within production code, it may be different in the
 * testing space.
 */
class FragmentActivityDirectionsLauncher
@Inject
constructor(
    private val activity: Activity
) : DirectionsLauncher {
    override fun launch(directions: NavDirections) {
        val navHostFragmentId = getNavHostFragmentId()

        activity.findNavController(navHostFragmentId).navigate(directions)
    }

    private fun getNavHostFragmentId(): Int {
        val navHostFragmentId =
            when (activity) {
                is IdCheckSdkActivity -> sdkId.sdk_fragment_wrapper
                else -> android.R.id.content
            }
        return navHostFragmentId
    }
}
