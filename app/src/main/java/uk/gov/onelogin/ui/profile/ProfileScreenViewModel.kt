package uk.gov.onelogin.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.ui.LocaleUtils

@HiltViewModel
class ProfileScreenViewModel @Inject constructor(
    localeUtils: LocaleUtils,
    getEmail: GetEmail
) : ViewModel() {
    val email = getEmail() ?: ""
    val locale = localeUtils.getLocale()
}
