package uk.gov.onelogin.developer.tabs.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import uk.gov.onelogin.appinfo.apicall.domain.model.AppInfoData
import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState
import uk.gov.onelogin.appinfo.source.domain.source.AppInfoLocalSource

@HiltViewModel
class AppTabScreenViewModel @Inject constructor(
    private val appInfoLocalSource: AppInfoLocalSource
) : ViewModel() {
    val appInfo: MutableStateFlow<AppInfoData?> = MutableStateFlow(null)

    fun getAppInfo() = when (val appInfoStatus = appInfoLocalSource.get()) {
        is AppInfoLocalState.Failure -> appInfo.value = null
        is AppInfoLocalState.Success -> appInfo.value = appInfoStatus.value
    }

    fun updateAppInfoData(data: AppInfoData) {
        appInfoLocalSource.update(data)
    }
}
