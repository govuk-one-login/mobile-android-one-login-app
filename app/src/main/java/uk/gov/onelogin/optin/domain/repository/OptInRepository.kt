package uk.gov.onelogin.optin.domain.repository

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.model.DisallowedStateChange
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource

interface OptInRepository {
    fun isOptInPreferenceRequired(): Flow<Boolean>
    fun hasAnalyticsOptIn(): Flow<Boolean>
    suspend fun optIn()
    suspend fun optOut()
}

@Suppress("MemberVisibilityCanBePrivate")
class AnalyticsOptInRepository @Inject constructor(
    private val localSource: OptInLocalSource,
    private val remoteSource: OptInRemoteSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : OptInRepository {

    override fun isOptInPreferenceRequired(): Flow<Boolean> = flow {
        val state = fetchOptInState()
        emit(state.isUnset)
    }

    override fun hasAnalyticsOptIn(): Flow<Boolean> = flow {
        val state = fetchOptInState()
        emit(state == AnalyticsOptInState.Yes)
    }

    suspend fun fetchOptInState(): AnalyticsOptInState =
        withContext(dispatcher) {
            localSource.getState()
        }

    override suspend fun optIn() {
        updateOptInState(AnalyticsOptInState.Yes)
    }

    override suspend fun optOut() {
        updateOptInState(AnalyticsOptInState.No)
    }

    suspend fun updateOptInState(state: AnalyticsOptInState) {
        withContext(dispatcher) {
            if (state == AnalyticsOptInState.None) throw DisallowedStateChange()
            localSource.update(state)
            remoteSource.update(state)
        }
    }
}
