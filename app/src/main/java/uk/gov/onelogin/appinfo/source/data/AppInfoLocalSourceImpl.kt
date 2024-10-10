package uk.gov.onelogin.appinfo.source.data

import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.optin.ui.IODispatcherQualifier
import javax.inject.Inject

class AppInfoLocalSourceImpl @Inject constructor(
    private val sharedPrefs: SharedPreferences,
    @IODispatcherQualifier
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AppInfoLocalSource {
    override suspend fun get(): AppInfoLocalState {
        return try {
            val result = sharedPrefs.getString(APP_INFO_KEY, null)
            if (!result.isNullOrEmpty()) {
                Log.e("GetAppInfoLocal", "$result")
                decodeAppInfoData(result)
            } else {
                AppInfoLocalState.Failure(APP_INFO_LOCAL_SOURCE_ERROR)
            }
        } catch (e: ClassCastException) {
            AppInfoLocalState.Failure(APP_INFO_CLASS_CAST_ERROR)
        }
    }

    override suspend fun update(value: String) {
        withContext(dispatcher) {
            sharedPrefs.edit(true) {
                putString(APP_INFO_KEY, value)
            }
        }
    }

    private fun decodeAppInfoData(
        result: String
    ): AppInfoLocalState {
        return try {
            val decodedResult = Json.decodeFromString<AppInfoData>(result)
            AppInfoLocalState.Success(decodedResult)
        } catch (e: SerializationException) {
            AppInfoLocalState.Failure(reason = APP_INFO_ILLEGAL_ARG_ERROR, exp = e)
        }
    }

    companion object {
        const val APP_INFO_KEY = "AppInfo.Key"
        const val APP_INFO_LOCAL_SOURCE_ERROR = "Retrieving AppInfo - does not exist"
        const val APP_INFO_ILLEGAL_ARG_ERROR = "Deserialization Error - invalid JSON"
        const val APP_INFO_CLASS_CAST_ERROR = "Retrieving AppInfo - Class Cast Exception"
    }
}
