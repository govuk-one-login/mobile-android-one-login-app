package uk.gov.onelogin.developer.tabs.tokens

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltViewModel
class TokenTabScreenViewModel @Inject constructor(
    private val saveTokenExpiry: SaveTokenExpiry
) : ViewModel() {
    fun resetAccessToken() {
        saveTokenExpiry(System.currentTimeMillis() - 1)
    }
}
