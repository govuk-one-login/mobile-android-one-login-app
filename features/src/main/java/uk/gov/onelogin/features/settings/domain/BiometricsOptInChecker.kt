package uk.gov.onelogin.features.settings.domain

import kotlinx.coroutines.flow.Flow

fun interface BiometricsOptInChecker {
    fun getBiometricsOptInState(): Flow<Boolean>
}
