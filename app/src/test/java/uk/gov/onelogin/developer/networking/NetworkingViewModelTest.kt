package uk.gov.onelogin.developer.networking

import com.google.firebase.FirebaseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.integrity.ClientAttestationManager
import uk.gov.android.authentication.integrity.appcheck.AppChecker
import uk.gov.android.authentication.integrity.model.AppCheckToken
import uk.gov.android.authentication.integrity.model.AttestationResponse
import uk.gov.onelogin.appcheck.AppIntegrity
import uk.gov.onelogin.developer.tabs.networking.NetworkingViewModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkingViewModelTest {
    private lateinit var firebaseAppCheck: AppChecker
    private lateinit var appCheck: ClientAttestationManager
    private lateinit var appIntegrity: AppIntegrity
    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var sut: NetworkingViewModel

    @BeforeTest
    fun setup() {
        firebaseAppCheck = mock()
        appCheck = mock()
        appIntegrity = mock()
        sut = NetworkingViewModel(firebaseAppCheck, appCheck, appIntegrity)
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getToken() - Success`(): Unit = runTest {
        val expected = Result.success(AppCheckToken("Success"))
        // Given
        whenever(firebaseAppCheck.getAppCheckToken())
            .thenReturn(expected)
        // When
        sut.getToken()
        //Then
        assertEquals(expected.toString(), sut.tokenResponse.value)
    }

    @Test
    fun `getToken() - Failure`(): Unit = runTest {
        val exp = FirebaseException("error")
        // Given
        whenever(firebaseAppCheck.getAppCheckToken())
            .thenReturn(Result.failure(exp))
        // When
        sut.getToken()
        //Then
        assertEquals(Result.failure<Exception>(exp).toString(), sut.tokenResponse.value)
    }

    @Test
    fun `makeNetworkCall - Success`(): Unit = runTest {
        val expectedResponse = AttestationResponse.Success("Success", 0)
        // Given
        whenever(appCheck.getAttestation())
            .thenReturn(expectedResponse)
        // When
        sut.makeNetworkCall()
        //Then
        assertEquals(expectedResponse.toString(), sut.networkResponse.value)
    }

    @Test
    fun `makeNetworkCall - Failure`(): Unit = runTest {
        val exp = Exception("Error")
        val expectedResponse = AttestationResponse.Failure(exp.message!!, exp)
        // Given
        whenever(appCheck.getAttestation())
            .thenReturn(expectedResponse)
        // When
        sut.makeNetworkCall()
        //Then
        assertEquals(expectedResponse.toString(), sut.networkResponse.value)
    }
}
