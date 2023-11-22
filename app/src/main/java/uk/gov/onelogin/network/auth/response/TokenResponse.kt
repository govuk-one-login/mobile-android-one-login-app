package uk.gov.onelogin.network.auth.response

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TokenResponse(
    @SerializedName("access_token")
    val access: String,
    @SerializedName("expires_in")
    val expires: Int,
    @SerializedName("id_token")
    val id: String,
    @SerializedName("refresh_token")
    val refresh: String?,
    val scope: String,
    @SerializedName("token_type")
    val type: String
)
