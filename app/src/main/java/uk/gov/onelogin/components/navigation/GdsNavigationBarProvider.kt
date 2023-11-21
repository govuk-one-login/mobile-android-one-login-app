package uk.gov.onelogin.components.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import uk.gov.onelogin.R

class GdsNavigationBarProvider : PreviewParameterProvider<GdsNavigationBar> {
    private val baselineNavItem = GdsNavigationItem(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Selected icon"
            )
        },
        onClick = { },
        selected = true,
        label = { Text(text = "Label") }
    )

    override val values: Sequence<GdsNavigationBar> = sequenceOf(
        GdsNavigationBar(
            items = listOf(
                baselineNavItem,
                baselineNavItem.copy(
                    selected = false,
                    label = null
                ),
                baselineNavItem.copy(
                    selected = false,
                    label = { Text(text = "Home") }
                )
            )
        )
    )
}
