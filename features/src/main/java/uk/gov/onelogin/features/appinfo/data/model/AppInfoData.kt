package uk.gov.onelogin.features.appinfo.data.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AppInfoData(
    val apps: App,
) {
    @Serializable
    data class App(
        val android: AppInfo,
    )

    @Serializable
    @JsonIgnoreUnknownKeys
    data class AppInfo(
        val minimumVersion: String,
        val available: Boolean,
        val featureFlags: FeatureFlags,
    )

    @Serializable
    data class FeatureFlags(
        val appCheckEnabled: Boolean,
    )
}
