package uk.gov.android.authentication

import android.content.Context
import android.content.Intent

interface ILoginSession {
    fun present(
        configuration: LoginSessionConfiguration
    )

    fun init(context: Context): ILoginSession
    fun finalise(intent: Intent)
}
