package uk.gov.onelogin.optin.ui

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import uk.gov.onelogin.optin.domain.repository.AnalyticsOptInRepositoryTest.Companion.createTestAnalyticsOptInRepository

@ExperimentalCoroutinesApi
class OptInViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var viewModel: OptInViewModel

    @BeforeTest
    fun setUp() {
        viewModel = OptInViewModel(
            repository = createTestAnalyticsOptInRepository(dispatcher)
        )
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial UI state is PreChoice`() = runTest {
        // Given a new OptInViewModel
        // Then the `uiState` is OptInUIState.PreChoice
        val state = viewModel.uiState.first()
        assertEquals(expected = OptInUIState.PreChoice, state)
    }

    @Test
    fun `optIn changes UI state to PostChoice`() = runTest {
        // Given a new OptInViewModel
        // When calling optIn()
        viewModel.optIn()
        // Then the `uiState` is OptInUIState.PostChoice
        val state = viewModel.uiState.first()
        assertEquals(expected = OptInUIState.PostChoice, state)
    }

    @Test
    fun `optOut changes UI state to PostChoice`() = runTest {
        // Given a new OptInViewModel
        // When calling optOut()
        viewModel.optOut()
        // Then the `uiState` is OptInUIState.PostChoice
        val state = viewModel.uiState.first()
        assertEquals(expected = OptInUIState.PostChoice, state)
    }
}
