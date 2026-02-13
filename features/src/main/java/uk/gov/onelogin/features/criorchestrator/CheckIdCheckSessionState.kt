package uk.gov.onelogin.features.criorchestrator

import uk.gov.onelogin.criorchestrator.features.idcheckwrapper.publicapi.idchecksdkactivestate.isIdCheckSdkActive
import uk.gov.onelogin.criorchestrator.sdk.sharedapi.CriOrchestratorSdk
import javax.inject.Inject

fun interface CheckIdCheckSessionState {
    fun isIdCheckActive(): Boolean
}

class CheckIdCheckSessionStateImpl
    @Inject
    constructor(
        private val criOrchestratorSdk: CriOrchestratorSdk,
    ) : CheckIdCheckSessionState {
        override fun isIdCheckActive(): Boolean = criOrchestratorSdk.isIdCheckSdkActive.invoke()
    }
