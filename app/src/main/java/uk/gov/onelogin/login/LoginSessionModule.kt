package uk.gov.onelogin.login

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.authentication.integrity.keymanager.KeyStoreManager
import uk.gov.android.authentication.integrity.pop.ProofOfPossessionGenerator
import uk.gov.android.authentication.login.AppAuthSession
import uk.gov.android.authentication.login.LoginSession
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionConfig
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManager
import uk.gov.android.authentication.login.refresh.DemonstratingProofOfPossessionManagerImpl
import uk.gov.logging.api.Logger

@Module
@InstallIn(SingletonComponent::class)
object LoginSessionModule {
    @Provides
    fun provideDemonstratingProofOfPossessionConfig(
        logger: Logger,
        keyStoreManager: KeyStoreManager
    ) = DemonstratingProofOfPossessionConfig(
        logger,
        ProofOfPossessionGenerator,
        keyStoreManager
    )

    @Provides
    fun provideDemonstratingProofOfPossessionManager(
        config: DemonstratingProofOfPossessionConfig
    ): DemonstratingProofOfPossessionManager {
        return DemonstratingProofOfPossessionManagerImpl(config)
    }

    @Provides
    fun providesLoginSession(
        @ApplicationContext
        context: Context,
        demonstratingProofOfPossessionManager: DemonstratingProofOfPossessionManager
    ): LoginSession = AppAuthSession(context, demonstratingProofOfPossessionManager)
}
