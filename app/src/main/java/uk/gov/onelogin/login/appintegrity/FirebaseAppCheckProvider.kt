package uk.gov.onelogin.login.appintegrity

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.appcheck.AppCheckProviderFactory
import com.google.firebase.appcheck.appCheck
import com.google.firebase.initialize
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface FirebaseAppCheckProvider {
    @Throws(Exception::class)
    fun init()

    @Throws(Exception::class)
    suspend fun getToken(): String
}

class FirebaseAppCheckProviderImpl
    @Inject
    constructor(
        private val appCheckFactory: AppCheckProviderFactory,
        private val context: Context,
    ) : FirebaseAppCheckProvider {
        private val appCheck = Firebase.appCheck

        override fun init() {
            Firebase.appCheck.installAppCheckProviderFactory(
                appCheckFactory,
            )
            Firebase.initialize(context)
        }

        override suspend fun getToken(): String = appCheck.limitedUseAppCheckToken.await().token
    }
