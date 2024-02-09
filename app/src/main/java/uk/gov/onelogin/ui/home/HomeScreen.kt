package uk.gov.onelogin.ui.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.StandardIntegrityManager
import com.google.android.play.core.integrity.StandardIntegrityManager.StandardIntegrityTokenProvider
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.Url
import io.ktor.http.contentType
import uk.gov.android.authentication.TokenResponse
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.R
import io.ktor.utils.io.charsets.Charset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uk.gov.onelogin.network.http.IHttpClient
import uk.gov.onelogin.ui.components.appbar.GdsTopAppBar
import uk.gov.onelogin.ui.components.navigation.GdsNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    tokens: TokenResponse? = null,
    httpClient: IHttpClient? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    GdsTheme {
        Column {
            /* DCMAW-7031: Configure top app bar: */
            GdsTopAppBar(
                title = {
                    GdsHeading(
                        headingParameters = HeadingParameters(
                            size = HeadingSize.H1(),
                            text = R.string.homeScreenTitle
                        )
                    )
                }
            ).generate()

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Access Token",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    tokens?.accessToken ?: "No access token set!",
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag("homeScreen-accessToken")
                )
                Text(
                    text = "ID Token",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tokens?.idToken ?: "No id token set!",
                    modifier = Modifier
                        .padding(16.dp)
                        .testTag("homeScreen-idToken")
                )
                Text(
                    text = "Refresh Token",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tokens?.refreshToken ?: "No refresh token set!",
                    modifier = Modifier
                        .padding(
                            all = 16.dp
                        )
                        .testTag("homeScreen-refreshToken")
                )
                Text(
                    text = "Play integrity Spike",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "https://sts-be-jamie-7564-4.token.dev.account.gov.uk/hello-world",
                    modifier = Modifier
                        .padding(16.dp)
                )
                TextButton(
                    onClick = {
                        httpClient?.let {
                            initialisePlayIntegrityApi(context) { integrityTokenProvider ->
                                Log.d("HomeScreen", "initialisePlayIntegrityApi: Success")
                                retrieveIntegrityToken(integrityTokenProvider) { integrityToken ->

                                    Log.d(
                                        "HomeScreen",
                                        "retrieveIntegrityToken: Success: token: ${integrityToken.token()}"
                                    )
                                    makeSecureNetworkRequest(
                                        httpClient,
                                        coroutineScope,
                                        integrityToken.token()
                                    )
                                }
                            }
                        }
                        Toast.makeText(context, "Clicked on", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text("Make Request")
                }
            }
            /* DCMAW-7045: Configure bottom navigation bar: */
            GdsNavigationBar(items = listOf()).generate()
        }
    }
}

fun makeSecureNetworkRequest(
    httpClient: IHttpClient,
    coroutineScope: CoroutineScope,
    integrityToken: String
) {
    val url = Url("https://sts-be-jamie-7564-4.token.dev.account.gov.uk/hello-world")
    coroutineScope.launch(Dispatchers.IO) {
        val response = httpClient.client().post(url) {
            setBody(HelloWorldPostBody(name = "Flavius", deviceToken = integrityToken))
            contentType(ContentType.Application.Json)
        }
        val stringBody: String = response.body()
        Log.d("HomeScreen", "makeSecureNetworkRequest: response.status: ${response.status}")
        Log.d("HomeScreen", "makeSecureNetworkRequest: response.body: ${stringBody}")
    }
}

private fun initialisePlayIntegrityApi(
    context: Context,
    successListener: OnSuccessListener<in StandardIntegrityTokenProvider>
) {
    val standardIntegrityManager =
        IntegrityManagerFactory.createStandard(context.applicationContext)

    val cloudProjectNumber =
        241385178545 // project number of the Google Cloud Service linked to the app
    standardIntegrityManager.prepareIntegrityToken(
        StandardIntegrityManager.PrepareIntegrityTokenRequest.builder()
            .setCloudProjectNumber(cloudProjectNumber)
            .build()
    )
        .addOnSuccessListener(successListener)
        .addOnFailureListener {
            Log.e("HomeScreen", "initialisePlayIntegrityApi: Error: $it")
        }
}

private fun retrieveIntegrityToken(
    integrityTokenProvider: StandardIntegrityTokenProvider,
    onSuccessListener: OnSuccessListener<in StandardIntegrityManager.StandardIntegrityToken>
) {
    val requestHash = "Jamie is awesome - this is an optional hash of the request"
    val integrityTokenResponse = integrityTokenProvider.request(
        StandardIntegrityManager.StandardIntegrityTokenRequest.builder()
            .setRequestHash(requestHash)
            .build()
    )
    integrityTokenResponse
        .addOnSuccessListener(onSuccessListener)
        .addOnFailureListener { exception: Exception? ->
            Log.e(
                "HomeScreen",
                "retrieveIntegrityToken: error: $exception",
            )
        }

}

@Composable
@Preview
private fun Preview() {
    HomeScreen()
}
