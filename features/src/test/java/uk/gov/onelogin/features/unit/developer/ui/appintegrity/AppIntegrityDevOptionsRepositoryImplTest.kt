package uk.gov.onelogin.features.unit.developer.ui.appintegrity

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import uk.gov.onelogin.features.developer.ui.appintegrity.AppIntegrityDevOptionsRepositoryImpl
import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException

class AppIntegrityDevOptionsRepositoryImplTest {
    private val repository = AppIntegrityDevOptionsRepositoryImpl()

    @Test
    fun `forceError defaults to null`() {
        val result = repository.forceError

        assertNull(result)
    }

    @Test
    fun `forceError returns the value that was set`() {
        repository.forceError = AppIntegrityException.AppIntegrityErrorType.INTERMITTENT

        val result = repository.forceError

        assertEquals(
            AppIntegrityException.AppIntegrityErrorType.INTERMITTENT,
            result,
        )
    }
}
