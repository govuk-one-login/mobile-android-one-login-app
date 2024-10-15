package uk.gov.onelogin.developer.tabs.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource

@HiltViewModel
class AppTabScreenViewModel @Inject constructor(
    private val appInfoLocalSource: AppInfoLocalSource
) : ViewModel() {
    @OptIn(ExperimentalSerializationApi::class)
    fun getAppInfoAsString(): String {
        return when (val appInfoStatus = appInfoLocalSource.get()) {
            is AppInfoLocalState.Failure ->
                "Failed to retrieve app info"

            is AppInfoLocalState.Success -> {
                val json = Json {
                    prettyPrint = true
                    prettyPrintIndent = " "
                }
                json.encodeToString<AppInfoData>(
                    AppInfoData.serializer(),
                    appInfoStatus.value
                )
            }
        }
    }
}
