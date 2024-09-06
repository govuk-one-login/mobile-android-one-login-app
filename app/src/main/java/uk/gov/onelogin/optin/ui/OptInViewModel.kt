package uk.gov.onelogin.optin.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.gov.onelogin.optin.domain.SingleChoice
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import javax.inject.Inject

@HiltViewModel
class OptInViewModel @Inject constructor(
    private val repository: OptInRepository
) : ViewModel() {
    private val choice = SingleChoice()
    val uiState: Flow<OptInUIState> = choice.state.map {
        when (it) {
            SingleChoice.State.PreChoice -> OptInUIState.PreChoice
            SingleChoice.State.PostChoice -> OptInUIState.PostChoice
        }
    }

    fun optIn() {
        choice.choose {
            viewModelScope.launch {
                repository.optIn()
            }
        }
    }

    fun optOut() {
        choice.choose {
            viewModelScope.launch {
                repository.optIn()
            }
        }
    }
}
