package uk.gov.onelogin.developer.tabs.tokens

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import uk.gov.onelogin.tokens.Keys
import uk.gov.onelogin.tokens.usecases.GetPersistentId
import uk.gov.onelogin.tokens.usecases.SaveToOpenSecureStore
import uk.gov.onelogin.tokens.usecases.SaveTokenExpiry

@HiltViewModel
class TokenTabScreenViewModel @Inject constructor(
    private val saveTokenExpiry: SaveTokenExpiry,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val getPersistentId: GetPersistentId
) : ViewModel() {
    private val _persistentId = mutableStateOf("")
    val persistentId: State<String>
        get() = _persistentId

    init {
        viewModelScope.launch {
            _persistentId.value = getPersistentId() ?: ""
        }
    }

    fun resetAccessToken() {
        saveTokenExpiry(System.currentTimeMillis() - 1)
    }

    fun resetPersistentID() {
        viewModelScope.launch {
            saveToOpenSecureStore(
                Keys.PERSISTENT_ID_KEY,
                ""
            )
            _persistentId.value = getPersistentId() ?: ""
        }
    }
}
