package uk.gov.onelogin.optin.domain.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.assertThrows
import uk.gov.onelogin.optin.domain.model.AnalyticsOptInState
import uk.gov.onelogin.optin.domain.model.DisallowedStateChange
import uk.gov.onelogin.optin.domain.source.FakeOptInLocalSource
import uk.gov.onelogin.optin.domain.source.FakeOptInRemoteSource
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/*
* I am deliberately testing the internal functionality of AnalyticsOptInRepository independently
* from the public API
*  */
@ExperimentalCoroutinesApi
class AnalyticsOptInRepositoryTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: AnalyticsOptInRepository

    @BeforeTest
    fun setUp() {
        repository = createTestAnalyticsOptInRepository(dispatcher)
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
    fun `updateOptInState(None) throws DisallowedStateChange`() = runTest {
        // Given an AnalyticsOptInState.No opt in preference
        repository.updateOptInState(AnalyticsOptInState.No)
        // When calling updateOptInState() with None
        assertThrows<DisallowedStateChange> {
            repository.updateOptInState(AnalyticsOptInState.None)
        }
        // Then the opt in state is unchanged (still No)
        val actual = repository.fetchOptInState()
        assertEquals(expected = AnalyticsOptInState.No, actual)
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

    companion object {
        fun createTestAnalyticsOptInRepository(
            dispatcher: CoroutineDispatcher
        ) = AnalyticsOptInRepository(
            localSource = FakeOptInLocalSource(),
            remoteSource = FakeOptInRemoteSource(),
            dispatcher = dispatcher
        )
    }
}
