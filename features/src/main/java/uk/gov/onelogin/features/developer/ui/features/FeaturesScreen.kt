package uk.gov.onelogin.features.developer.ui.features

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import uk.gov.android.featureflags.FeatureFlag
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.theme.mediumPadding
import uk.gov.android.ui.theme.smallPadding

@Composable
fun FeaturesScreen(viewModel: FeaturesScreenViewModel = hiltViewModel()) {
    val availableFeatures by viewModel.featureList
    Column(
        modifier =
        Modifier
            .fillMaxSize()
            .padding(mediumPadding)
    ) {
        Text(
            text = stringResource(R.string.app_developer_features_title),
            style = TextStyle(fontWeight = FontWeight.Bold)
        )
        Column {
            availableFeatures.forEach { feature ->
                FeatureToggle(featureFlag = feature.key, checked = feature.value) {
                    viewModel.toggleFeature(feature.key)
                }
            }
        }
    }
}

@Composable
private fun FeatureToggle(
    featureFlag: FeatureFlag,
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier.padding(smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1F),
            text = featureFlag.id,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Switch(
            checked = checked,
            onCheckedChange = { onToggle() }
        )
    }
    HorizontalDivider()
}
