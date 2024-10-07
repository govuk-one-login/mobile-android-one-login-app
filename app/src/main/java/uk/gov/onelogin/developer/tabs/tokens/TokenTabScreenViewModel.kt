package uk.gov.onelogin.developer.tabs.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val _persistentId = MutableStateFlow("")
    val persistentId: StateFlow<String>
        get() = _persistentId.asStateFlow()

    init {
        viewModelScope.launch {
            setPersistentId()
        }
    }

    fun resetAccessToken() {
        saveTokenExpiry(System.currentTimeMillis() - 1)
    }

    fun resetPersistentId() {
        viewModelScope.launch {
            saveToOpenSecureStore(
                Keys.PERSISTENT_ID_KEY,
                ""
            )
            setPersistentId()
        }
    }

    private suspend fun setPersistentId() {
        _persistentId.value = getPersistentId() ?: ""
    }
}
