package uk.gov.onelogin.features.unit.developer.ui.logging

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage
import uk.gov.logging.api.v3.matchers.MemorisedLoggerMatchers.hasSize
import uk.gov.onelogin.features.developer.ui.logging.DeveloperException
import uk.gov.onelogin.features.developer.ui.logging.LoggingTabViewModel

class LoggingTabViewModelTest {
    private val logger = MemorisedLogger()
    private lateinit var viewModel: LoggingTabViewModel

    @BeforeEach
    fun setUp() {
        viewModel = LoggingTabViewModel(logger)
    }

    @Test
    fun `crash throws IllegalStateException`() {
        assertThrows(DeveloperException::class.java) {
            viewModel.crash()
        }
    }

    @Test
    fun `logError logs an error message`() {
        viewModel.logError()

        assertThat(logger, hasSize(1))
        assertThat(logger, hasItem(hasMessage("Test error from developer menu")))
    }

    @Test
    fun `logInfo logs an info message`() {
        viewModel.logInfo()

        assertThat(logger, hasSize(1))
        assertThat(logger, hasItem(hasMessage("Test info from developer menu")))
    }
}
