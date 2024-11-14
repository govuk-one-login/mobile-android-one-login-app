package uk.gov.onelogin.tokens.usecases

import android.util.Log
import javax.inject.Inject
import javax.inject.Named
import uk.gov.android.securestore.SecureStore
import uk.gov.android.securestore.error.SecureStorageError

interface SaveToOpenSecureStore {
    /**
     * Use case for saving data into an open secure store instance
     *
     * @param key [String] value of key value pair to save
     * @param value [String] value of value in key value pair to save
     */
    suspend fun save(
        key: String,
        value: String
    )

    suspend fun save(
        key: String,
        value: Number
    )
}

class SaveToOpenSecureStoreImpl @Inject constructor(
    @Named("Open")
    private val secureStore: SecureStore
) : SaveToOpenSecureStore {
    override suspend fun save(key: String, value: String) {
        try {
            secureStore.upsert(
                key = key,
                value = value
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
        }
    }

    override suspend fun save(key: String, value: Number) {
        try {
            secureStore.upsert(
                key = key,
                value = value.toString()
            )
        } catch (e: SecureStorageError) {
            Log.e(this::class.simpleName, e.message, e)
        }
    }
}
