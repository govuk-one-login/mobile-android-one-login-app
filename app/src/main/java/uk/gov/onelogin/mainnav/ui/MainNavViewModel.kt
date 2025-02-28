package uk.gov.onelogin.mainnav.ui

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainNavViewModel @Inject constructor() : ViewModel() {
    fun openOSSLMenu(context: Context) {
        OssLicensesMenuActivity.setActivityTitle("OSSL")
        val intent = Intent(context, OssLicensesMenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
