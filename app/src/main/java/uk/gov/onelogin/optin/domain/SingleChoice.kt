package uk.gov.onelogin.optin.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.concurrent.atomic.AtomicBoolean

/*
 * Needs to be moved to a common area
 * Not sure the name captures the intent
 */
class SingleChoice {
    private val hasChosen = AtomicBoolean(false)
    private val _state: MutableStateFlow<State> = MutableStateFlow(State.PreChoice)
    val state: Flow<State> = _state

    fun choose(option: () -> Unit) {
        if (!hasChosen.getAndSet(true)) {
            _state.tryEmit(State.PostChoice)
            option()
        }
    }

    enum class State {
        PreChoice, PostChoice
    }
}