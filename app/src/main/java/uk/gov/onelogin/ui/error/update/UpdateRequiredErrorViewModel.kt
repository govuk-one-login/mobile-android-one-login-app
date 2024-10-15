package uk.gov.onelogin.ui.error.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.appinfo.AppInfoUtils

@HiltViewModel
class UpdateRequiredErrorViewModel @Inject constructor() : ViewModel() {
    fun updateApp(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(AppInfoUtils.GOOGLE_PLAY_URL)
            // To open the app once in prod uncomment line 18 once app available on PlayStore and update Manifest and the Uri.parse above with the correct/ full URL
            // see more for implementation: https://developer.android.com/distribute/marketing-tools/linking-to-google-play#android-app
            // Remove onelogin from accepted style issues in config/styles/config/vocabularies/Base/accept.txt
            // setPackage("uk.gov.onelogin")
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
