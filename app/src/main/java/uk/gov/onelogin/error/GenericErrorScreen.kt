package uk.gov.onelogin.error

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import uk.gov.android.ui.components.ButtonParameters
import uk.gov.android.ui.components.ButtonType
import uk.gov.android.ui.components.HeadingSize
import uk.gov.android.ui.components.content.ContentParameters
import uk.gov.android.ui.components.content.GdsContentText
import uk.gov.android.ui.components.images.icon.IconParameters
import uk.gov.android.ui.components.information.InformationParameters
import uk.gov.android.ui.pages.errors.ErrorPage
import uk.gov.android.ui.pages.errors.ErrorPageParameters
import uk.gov.android.ui.theme.GdsTheme
import uk.gov.onelogin.R


@Composable
fun GenericErrorScreen(
) {
       ErrorPage(parameters = ErrorPageParameters(
           informationParameters = InformationParameters(
               contentParameters = ContentParameters(
                   resource = listOf(
                       GdsContentText.GdsContentTextString(
                           subTitle = R.string.errorScreenTitle,
                           text = arrayOf(R.string.errorScreenSubTitle).toIntArray()
                       )
                   ),
                   headingSize = HeadingSize.H1()
               ),
               iconParameters = IconParameters(
                   image = R.drawable.errors_alert,
                   description =R.string.errorScreenSubTitle,
                   size = 107
               )
           ),
           primaryButtonParameters = ButtonParameters(
               buttonType = ButtonType.PRIMARY(),
               onClick = {},
               text = R.string.errorScreenButton
           )
       )
       )
}

fun onBackPressed() = if (shouldAllowBack()) {
    super.onBackPressed()
} else {
    doSomething()
}

fun doSomething() {
    TODO("Not yet implemented")
}

fun shouldAllowBack(): Boolean {
    TODO("Not yet implemented")
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun Preview() {
    GdsTheme {
        GenericErrorScreen()
    }
}
