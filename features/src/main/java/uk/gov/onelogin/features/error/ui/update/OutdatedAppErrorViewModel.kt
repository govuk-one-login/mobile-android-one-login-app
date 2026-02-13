package uk.gov.onelogin.features.error.ui.update

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import uk.gov.onelogin.features.appinfo.AppInfoUtils
import javax.inject.Inject

@HiltViewModel
class OutdatedAppErrorViewModel
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : ViewModel() {
        fun updateApp() {
            val intent =
                Intent(Intent.ACTION_VIEW).apply {
                    data = AppInfoUtils.GOOGLE_PLAY_URL.toUri()
                    // To open the app once in prod uncomment line 18 once app available on PlayStore and update Manifest and the toUri() above with the correct/ full URL
                    // see more for implementation: https://developer.android.com/distribute/marketing-tools/linking-to-google-play#android-app
                    // Remove onelogin from accepted style issues in config/styles/config/vocabularies/Base/accept.txt
                    // setPackage("com.android.vending")
                }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
