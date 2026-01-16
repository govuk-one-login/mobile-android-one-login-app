package uk.gov.onelogin.login.localauth

import android.app.KeyguardManager
import android.content.Context
import androidx.biometric.BiometricManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManager
import uk.gov.android.localauth.devicesecurity.DeviceBiometricsManagerImpl
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepo
import uk.gov.onelogin.core.localauth.domain.LocalAuthPreferenceRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BiometricsModule {
    @Provides
    @Singleton
    fun bindDeviceBiometricsManager(
        @ApplicationContext
        context: Context,
    ): DeviceBiometricsManager {
        val biometricManager = BiometricManager.from(context)
        val kgm = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return DeviceBiometricsManagerImpl(biometricManager, kgm)
    }

    @Provides
    @Singleton
    fun bindBiometricPreferenceHandler(
        @ApplicationContext
        context: Context,
    ): LocalAuthPreferenceRepo = LocalAuthPreferenceRepositoryImpl(context)
}
