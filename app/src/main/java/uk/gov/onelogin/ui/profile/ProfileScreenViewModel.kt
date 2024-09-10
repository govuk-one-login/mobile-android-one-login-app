package uk.gov.onelogin.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.tokens.usecases.GetEmail

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    getEmail: GetEmail
) : ViewModel() {
    val email = getEmail().orEmpty()
}
