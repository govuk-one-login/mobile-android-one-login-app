package uk.gov.onelogin.features.home.idcheck.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.urls.GovUkSignInUrl
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.navigation.domain.WebNavigator
import javax.inject.Inject

@HiltViewModel
class HowToProveYourIdentityModalViewModel @Inject constructor(
    private val navigator: Navigator,
    private val webNavigator: WebNavigator,
    @GovUkSignInUrl
    private val govUkSignInUrl: String,
) : ViewModel() {
    fun onDismissRequest() {
        navigator.goBack()
    }

    fun onGoToGovUkWebsiteClick() {
        webNavigator.openWebBrowser(govUkSignInUrl)
    }
}
