package uk.gov.onelogin.optin.ui

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import uk.gov.onelogin.optin.domain.repository.AnalyticsOptInRepositoryTest.Companion.createTestAnalyticsOptInRepository
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class OptInRequirementViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: OptInRepository
    private lateinit var viewModel: OptInRequirementViewModel

    @BeforeTest
    fun setUp() {
        repository = createTestAnalyticsOptInRepository(dispatcher)
        viewModel = OptInRequirementViewModel(repository)
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial isOptInRequired is true`() = runTest {
        // Given a OptInRequirementViewModel
        // Then the isOptInRequired is true
        val actual = viewModel.isOptInRequired.first()
        assertEquals(expected = true, actual)
    }

    @Test
    fun `isOptInRequired post choice value is false`() = runTest {
        // Given a OptInRequirementViewModel
        // When the repository has registered an opt in preference
        repository.optIn()
        // Then the isOptInRequired is false
        val actual = viewModel.isOptInRequired.first()
        assertEquals(expected = false, actual)
    }
}
