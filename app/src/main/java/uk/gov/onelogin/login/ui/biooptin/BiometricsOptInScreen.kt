package uk.gov.onelogin.login.ui.biooptin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.onelogin.R
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.GdsVectorImage
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.components.VectorImageParameters
import uk.gov.android.ui.components.buttons.ButtonParameters
import uk.gov.android.ui.components.buttons.ButtonType
import uk.gov.android.ui.components.buttons.GdsButton
import uk.gov.android.ui.components.content.ContentParameters
import uk.gov.android.ui.components.content.GdsContent
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.android.ui.theme.hintTextGrey
import uk.gov.android.ui.theme.m3.Typography
import uk.gov.android.ui.theme.smallPadding
import uk.gov.onelogin.login.biooptin.BiometricPreference

@Suppress("LongMethod")
@Composable
fun BiometricsOptInScreen(
    viewModel: BioOptInViewModel = hiltViewModel(),
    onPrimary: () -> Unit = {},
    onSecondary: () -> Unit = {}
) {
    GdsTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(smallPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1F),
                verticalArrangement = Arrangement.Center
            ) {
                GdsVectorImage(
                    VectorImageParameters(
                        image = R.drawable.bio_opt_in,
                        scale = ContentScale.Fit
                    )
                )
                GdsHeading(
                    HeadingParameters(
                        modifier = Modifier.padding(top = smallPadding),
                        text = R.string.app_enableBiometricsTitle,
                        size = HeadingSize.H1(),
                        textAlign = TextAlign.Center
                    )
                )
                GdsContent(
                    ContentParameters(
                        resource = listOf(
                            GdsContentText.GdsContentTextString(
                                intArrayOf(
                                    R.string.app_enableBiometricsBody1
                                )
                            )
                        )
                    )
                )
                GdsContent(
                    ContentParameters(
                        resource = listOf(
                            GdsContentText.GdsContentTextString(
                                intArrayOf(R.string.app_enableBiometricsBody2)
                            )
                        )
                    )
                )
            }
            Column(
                verticalArrangement = Arrangement.Bottom
            ) {
                GdsContent(
                    ContentParameters(
                        resource = listOf(
                            GdsContentText.GdsContentTextString(
                                intArrayOf(R.string.app_enableBiometricsFootnote)
                            )
                        ),
                        textStyle = Typography.bodyMedium,
                        color = hintTextGrey
                    )
                )
                GdsButton(
                    ButtonParameters(
                        modifier = Modifier.fillMaxWidth(),
                        buttonType = ButtonType.PRIMARY(),
                        text = R.string.app_enableBiometricsButton,
                        onClick = {
                            viewModel.setBioPreference(BiometricPreference.BIOMETRICS)
                            onPrimary()
                        }
                    )
                )
                GdsButton(
                    ButtonParameters(
                        modifier = Modifier.fillMaxWidth(),
                        buttonType = ButtonType.SECONDARY(),
                        text = R.string.app_enablePasscodeOrPatternButton,
                        onClick = {
                            viewModel.setBioPreference(BiometricPreference.PASSCODE)
                            onSecondary()
                        }
                    )
                )
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    BiometricsOptInScreen()
}
