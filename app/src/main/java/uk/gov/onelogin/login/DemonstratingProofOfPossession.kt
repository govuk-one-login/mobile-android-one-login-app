package uk.gov.onelogin.login

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.integrity.keymanager.KeyStoreManager
import uk.gov.android.authentication.integrity.pop.ProofOfPossessionGenerator
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionConfig
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManagerImpl
import uk.gov.logging.api.Logger

@Module
@InstallIn(SingletonComponent::class)
object DemonstratingProofOfPossession {
    @Provides
    fun provideDemonstratingProofOfPossessionConfig(
        logger: Logger,
        keyStoreManager: KeyStoreManager,
    ) = DemonstratingProofOfPossessionConfig(
        logger,
        ProofOfPossessionGenerator,
        keyStoreManager,
    )

    @Provides
    fun provideDemonstratingProofOfPossessionManager(
        config: DemonstratingProofOfPossessionConfig
    ): DemonstratingProofOfPossessionManager = DemonstratingProofOfPossessionManagerImpl(config)
}
