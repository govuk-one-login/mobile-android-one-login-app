package uk.gov.onelogin.features.error.ui.appintegrity

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@HiltViewModel
class AppIntegrityErrorViewModel
    @Inject
    constructor(
        private val navigator: Navigator
    ) : ViewModel() {
        fun goBackToPreviousScreen() {
            navigator.goBack()
            navigator.goBack()
        }
    }
