package uk.gov.onelogin.features.developer.ui.logging

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger
import javax.inject.Inject

@HiltViewModel
class LoggingTabViewModel
    @Inject
    constructor(
        private val logger: Logger,
    ) : ViewModel(),
        LogTagProvider {
        fun crash(): Unit = throw DeveloperException("Forced crash from developer menu")

        fun logError() {
            logger.error("Test error from developer menu", DeveloperException("Test error"))
        }

        fun logInfo() {
            logger.info("Test info from developer menu")
        }
    }
