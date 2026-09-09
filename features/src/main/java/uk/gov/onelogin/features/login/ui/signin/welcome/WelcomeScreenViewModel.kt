package uk.gov.onelogin.features.login.ui.signin.welcome

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.onelogin.core.navigation.domain.Navigator
import javax.inject.Inject

@HiltViewModel
@Suppress("LongParameterList", "TooManyFunctions")
class WelcomeScreenViewModel
    @Inject
    constructor(
        private val navigator: Navigator,
    ) : ViewModel() {
        private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
        val loading = _loading.asStateFlow()

        fun navigateToDevPanel() {
            navigator.openDeveloperPanel()
        }
    }
