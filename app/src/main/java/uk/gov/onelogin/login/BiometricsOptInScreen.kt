package uk.gov.onelogin.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uk.gov.android.ui.components.GdsHeading
import uk.gov.android.ui.components.GdsVectorImage
import uk.gov.android.ui.components.HeadingParameters
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.components.VectorImageParameters
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.R

@Composable
fun BiometricsOptInScreen() {
    GdsTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Column {
                GdsVectorImage(
                    VectorImageParameters(
                        image = R.drawable.bio_opt_in,
                        scale = ContentScale.Fit
                    )
                )
                GdsHeading(
                    HeadingParameters(
                        text = R.string.app_enableBiometricsTitle,
                        size = HeadingSize.H1(),
                        textAlign = TextAlign.Center
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
