package uk.gov.onelogin.features.unit.optin.ui

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import uk.gov.onelogin.features.optin.data.OptInRepository
import uk.gov.onelogin.features.optin.ui.OptInRequirementViewModel
import uk.gov.onelogin.features.unit.optin.data.AnalyticsOptInRepositoryTest.Companion.createTestAnalyticsOptInRepository
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalCoroutinesApi
class AnalyticsOptInRequirementViewModelTest {
    private lateinit var repository: OptInRepository
    private lateinit var viewModel: OptInRequirementViewModel

    @BeforeTest
    fun setUp() {
        repository = createTestAnalyticsOptInRepository()
        viewModel = OptInRequirementViewModel(repository)
    }

    @Test
    fun `initial isOptInRequired is true`() =
        runTest {
            // Given a OptInRequirementViewModel
            // Then the isOptInRequired is true
            val actual = viewModel.isOptInRequired.first()
            assertEquals(expected = true, actual)
        }

    @Test
    fun `isOptInRequired post choice value is false`() =
        runTest {
            // Given a OptInRequirementViewModel
            // When the repository has registered an opt in preference
            repository.optIn()
            // Then the isOptInRequired is false
            val actual = viewModel.isOptInRequired.first()
            assertEquals(expected = false, actual)
        }
}
