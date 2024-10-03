package uk.gov.onelogin.optin.domain.repository

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import uk.gov.onelogin.core.delete.domain.Cleaner
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource
import uk.gov.onelogin.optin.ui.IODispatcherQualifier

interface OptInRepository : Cleaner {
    fun isOptInPreferenceRequired(): Flow<Boolean>
    fun hasAnalyticsOptIn(): Flow<Boolean>
    suspend fun optIn()
    suspend fun optOut()
    suspend fun synchronise()
    suspend fun reset()
}

@Suppress("MemberVisibilityCanBePrivate")
class AnalyticsOptInRepository @Inject constructor(
    private val localSource: OptInLocalSource,
    private val remoteSource: OptInRemoteSource,
    @IODispatcherQualifier
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

    @SuppressWarnings("kotlin:S6311") // investigating the warning
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
            localSource.update(state)
            remoteSource.update(state)
        }
    }

    override suspend fun reset() {
        updateOptInState(AnalyticsOptInState.None)
    }

    override suspend fun synchronise() {
        withContext(dispatcher) {
            val state = fetchOptInState()
            remoteSource.update(state)
        }
    }

    override suspend fun clean(): Result<Unit> {
        reset()
        return Result.success(Unit)
    }
}
