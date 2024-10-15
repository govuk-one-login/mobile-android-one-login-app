package uk.gov.onelogin.ui.error.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateRequiredErrorViewModel @Inject constructor(
) : ViewModel() {
    fun updateApp(context: Context) {
        val intent =  Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://play.google.com/store/apps")
            // To open the app once in prod uncomment line 18 once app available on PlayStore and update Manifest and the Uri.parse above with the correct/ full URL
            // see more for implementation: https://developer.android.com/distribute/marketing-tools/linking-to-google-play#android-app
            // setPackage("uk.gov.onelogin")
        }
        context.startActivity(intent)
    }
}
