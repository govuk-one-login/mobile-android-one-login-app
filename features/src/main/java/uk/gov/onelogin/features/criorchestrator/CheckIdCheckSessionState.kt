package uk.gov.onelogin.features.criorchestrator

import javax.inject.Inject
import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.isIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk

interface CheckIdCheckSessionState {
    fun isIdCheckActive(): Boolean
}

class CheckIdCheckSessionStateImpl @Inject constructor(
    private val criOrchestratorSdk: CriOrchestratorSdk
) : CheckIdCheckSessionState {
    override fun isIdCheckActive(): Boolean {
        return criOrchestratorSdk.isIdCheckSdkActive.invoke()
    }
}
