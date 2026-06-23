package uk.gov.onelogin.features.network.provider

import android.content.Context
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.SignedDPoP
import uk.gov.android.network.dpop.DPoPProvider
import uk.gov.android.network.dpop.DPoPResponse
import uk.gov.android.onelogin.core.R
import javax.inject.Inject

/**
 * [DPoPProvider] implementation for the networking service.
 */
class DPoPProviderImpl
    @Inject
    constructor(
        private val context: Context,
        private val dPoPManager: DemonstratingProofOfPossessionManager,
    ) : DPoPProvider {
        override suspend fun getRefreshDPoP(): DPoPResponse {
            // htu (HTTP URI) is the token endpoint URL where this DPoP will be used
            val htu = context.getString(R.string.stsUrl, context.getString(R.string.tokenExchangeEndpoint))

            return when (val result = dPoPManager.generateDPoP(htu)) {
                is SignedDPoP.Success -> DPoPResponse.Success(result.popJwt)
                is SignedDPoP.Failure -> result.toFailure()
            }
        }

        private fun SignedDPoP.Failure.toFailure(): DPoPResponse.Failure =
            DPoPResponse.Failure(
                IllegalStateException("Failed to get DPoP: $reason", error)
            )
    }
