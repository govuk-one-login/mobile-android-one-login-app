package uk.gov.onelogin.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.tokens.usecases.GetEmail
import javax.inject.Inject

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    getEmail: GetEmail
) : ViewModel() {
    val email = getEmail().orEmpty()
}
