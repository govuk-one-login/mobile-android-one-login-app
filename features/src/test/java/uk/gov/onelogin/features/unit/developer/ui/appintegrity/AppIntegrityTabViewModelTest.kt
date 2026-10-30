package uk.gov.onelogin.features.unit.developer.ui.appintegrity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.mockito.kotlin.mock
import uk.gov.android.authentication.integrity.AppIntegrityManager
import uk.gov.android.authentication.integrity.appcheck.usecase.AppChecker
import uk.gov.onelogin.core.tokens.domain.retrieve.GetFromOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.features.developer.ui.appintegrity.AppIntegrityDevOptionsRepositoryImpl
import uk.gov.onelogin.features.developer.ui.appintegrity.AppIntegrityTabViewModel
import uk.gov.onelogin.features.extensions.CoroutinesTestExtension
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrity
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException

@ExtendWith(CoroutinesTestExtension::class)
class AppIntegrityTabViewModelTest {
    @JvmField
    @RegisterExtension
    val coroutinesTestExtension = CoroutinesTestExtension()

    private val firebaseAppCheck: AppChecker = mock()
    private val appCheck: AppIntegrityManager = mock()
    private val appIntegrity: AppIntegrity = mock()
    private val getFromOpenSecureStore: GetFromOpenSecureStore = mock()
    private val saveToOpenSecureStore: SaveToOpenSecureStore = mock()
    private val devOptionsRepository = AppIntegrityDevOptionsRepositoryImpl()

    private fun createViewModel() = AppIntegrityTabViewModel(
        firebaseAppCheck,
        appCheck,
        appIntegrity,
        getFromOpenSecureStore,
        saveToOpenSecureStore,
        devOptionsRepository,
    )

    @Test
    fun `setting a forced error updates the dev options repository`() {
        val viewModel = createViewModel()

        viewModel.setForceError(AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED)

        assertEquals(
            AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED,
            devOptionsRepository.forceError,
        )
        assertEquals(
            AppIntegrityException.AppIntegrityErrorType.APP_CHECK_FAILED,
            viewModel.forceError.value,
        )
    }

    @Test
    fun `clearing the forced error updates the dev options repository`() {
        devOptionsRepository.forceError =
            AppIntegrityException.AppIntegrityErrorType.INTERMITTENT
        val viewModel = createViewModel()

        viewModel.setForceError(null)

        assertNull(devOptionsRepository.forceError)
        assertNull(viewModel.forceError.value)
    }

    @Test
    fun `initial forced error reflects the repository`() {
        devOptionsRepository.forceError =
            AppIntegrityException.AppIntegrityErrorType.GENERIC

        val viewModel = createViewModel()

        assertEquals(
            AppIntegrityException.AppIntegrityErrorType.GENERIC,
            viewModel.forceError.value,
        )
    }
}
