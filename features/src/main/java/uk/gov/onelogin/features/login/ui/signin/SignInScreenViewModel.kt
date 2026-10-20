package uk.gov.onelogin.features.login.ui.signin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import uk.gov.onelogin.core.navigation.domain.Navigator

@HiltViewModel
@Suppress("LongParameterList", "TooManyFunctions")
class SignInScreenViewModel
@Inject
constructor(private val navigator: Navigator,) : ViewModel() {
    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    fun navigateToDevPanel() {
        navigator.openDeveloperPanel()
    }
}
