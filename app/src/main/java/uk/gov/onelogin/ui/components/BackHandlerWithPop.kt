package uk.gov.onelogin.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.navigation.Navigator

@Composable
fun BackHandlerWithPop(
    enabled: Boolean = true,
    onBack: () -> Unit
) {
    val viewModel: BackHandlerWithPopViewModel = hiltViewModel()
    BackHandler(enabled) {
        onBack()
        viewModel.goBack()
    }
}

@HiltViewModel
class BackHandlerWithPopViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel() {
    fun goBack() {
        navigator.goBack()
    }
}
