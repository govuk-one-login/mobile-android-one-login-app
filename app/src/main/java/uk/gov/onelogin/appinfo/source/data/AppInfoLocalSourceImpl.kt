package uk.gov.onelogin.appinfo.source.data

import android.util.Log
import kotlinx.serialization.json.Json
import uk.gov.android.securestore.RetrievalEvent
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.tokens.Keys
import javax.inject.Inject
import javax.inject.Named

class AppInfoLocalSourceImpl @Inject constructor(
    @Named("Open")
    private val secureStore: SecureStore
) : AppInfoLocalSource {
    override suspend fun get(): AppInfoLocalState {
        return when (val result = secureStore.retrieve(APP_INFO_KEY)) {
            is RetrievalEvent.Success -> {
                val decodedResult = Json.decodeFromString<AppInfoData>(result.value)
                AppInfoLocalState.Success(decodedResult)
            }
            is RetrievalEvent.Failed -> AppInfoLocalState.Failure(APP_INFO_LOCAL_SOURCE_ERROR)
        }
    }

    override suspend fun update(value: String) {
        try {
            secureStore.upsert(
                key = APP_INFO_KEY,
                value = value
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
        }
    }

    companion object {
        const val APP_INFO_LOCAL_SOURCE_ERROR = "Retrieving AppInfo from secure store error"
        const val APP_INFO_KEY = "app_info"
    }
}
