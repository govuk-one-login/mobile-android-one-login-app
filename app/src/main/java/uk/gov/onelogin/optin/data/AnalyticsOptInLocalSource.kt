package uk.gov.onelogin.optin.data

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.ui.IODispatcherQualifier

class AnalyticsOptInLocalSource @Inject constructor(
    private val preferences: SharedPreferences,
    @IODispatcherQualifier
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OptInLocalSource {

    override suspend fun getState(): AnalyticsOptInState = withContext(dispatcher) {
        preferences.getInt(OPT_IN_KEY, DEFAULT_ORDINAL).let { ordinal ->
            AnalyticsOptInState.entries[ordinal]
        }
    }

    override suspend fun update(state: AnalyticsOptInState) {
        withContext(dispatcher) {
            preferences.edit(IS_COMMIT) {
                putInt(OPT_IN_KEY, state.ordinal)
            }
        }
    }

    companion object {
        internal const val IS_COMMIT = true // in Dispatchers.IO, so already async
        internal const val OPT_IN_KEY = "OptIn.Key"
        internal val DEFAULT_ORDINAL = AnalyticsOptInState.None.ordinal
    }
}
