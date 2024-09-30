package uk.gov.onelogin.optin.domain.repository

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.wheneverBlocking
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.source.FakeOptInLocalSource
import uk.gov.onelogin.optin.domain.source.FakeOptInRemoteSource
import uk.gov.onelogin.optin.domain.source.OptInLocalSource
import uk.gov.onelogin.optin.domain.source.OptInRemoteSource

@ExperimentalCoroutinesApi
class AnalyticsOptInRepositoryTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var remoteSource: OptInRemoteSource
    private lateinit var repository: AnalyticsOptInRepository

    @BeforeTest
    fun setUp() {
        remoteSource = mock()
        repository = createTestAnalyticsOptInRepository(dispatcher, remoteSource = remoteSource)
        wheneverBlocking { remoteSource.update(any()) }.thenAnswer {}
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is None`() = runTest {
        // Given an unset opt in preference
        // When calling fetchOptInState() to query the state
        val actual = repository.fetchOptInState()
        // Then the opt in state is None
        assertEquals(expected = AnalyticsOptInState.None, actual)
    }

    @Test
    fun `updateOptInState(None) changes state to None`() = runTest {
        // Given an AnalyticsOptInState.No opt in preference
        repository.updateOptInState(AnalyticsOptInState.No)
        // When calling updateOptInState() with None
        repository.updateOptInState(AnalyticsOptInState.None)
        // Then the opt in state is changed to None
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.None, actual)
    }

    @Test
    fun `updateOptInState(Yes) changes state to Yes`() = runTest {
        // Given an unknown opt in preference
        // When calling updateOptInState() with Yes
        repository.updateOptInState(AnalyticsOptInState.Yes)
        // Then the opt in state is changed to Yes
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.Yes, actual)
    }

    @Test
    fun `updateOptInState(No) changes state to No`() = runTest {
        // Given an unknown opt in preference
        // When calling updateOptInState() with No
        repository.updateOptInState(AnalyticsOptInState.No)
        // Then the opt in state is changed to No
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.No, actual)
    }

    @Test
    fun `optIn() changes state to Yes`() = runTest {
        // Given an unknown opt in preference
        // When calling optIn()
        repository.optIn()
        // Then the opt in state is changed to AnalyticsOptInState.Yes
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.Yes, actual)
    }

    @Test
    fun `optOut() changes state to No`() = runTest {
        // Given an unknown opt in preference
        // When calling optOut()
        repository.optOut()
        // Then the opt in state is changed to AnalyticsOptInState.No
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.No, actual)
    }

    @Test
    fun `reset() changes state to None`() = runTest {
        // Given an unknown opt in preference
        // When calling reset()
        repository.reset()
        // Then the opt in state is changed to AnalyticsOptInState.None
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.None, actual)
    }

    @Test
    fun `synchronise sets the saved preference to the remote`() = runTest {
        // Given any AnalyticsOptInState
        val expected = AnalyticsOptInState.No
        repository.updateOptInState(expected)
        // When refreshing the preference
        repository.synchronise()
        // Then set AnalyticsOptInState to the remote Source twice
        verify(remoteSource, times(2)).update(expected)
    }

    companion object {
        fun createTestAnalyticsOptInRepository(
            dispatcher: CoroutineDispatcher,
            localSource: OptInLocalSource = FakeOptInLocalSource(),
            remoteSource: OptInRemoteSource = FakeOptInRemoteSource()
        ) = AnalyticsOptInRepository(
            localSource = localSource,
            remoteSource = remoteSource,
            dispatcher = dispatcher
        )
    }
}
