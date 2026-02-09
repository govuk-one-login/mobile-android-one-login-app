package uk.gov.onelogin.core.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset

object ModifierExtensions {
    fun Modifier.drawAfterMeasured(previousItemHeightPx: Int): Modifier =
        this.offset {
            IntOffset(x = 0, y = previousItemHeightPx)
        }

    fun errorBodyItemModifier(padding: Dp): Modifier = Modifier.fillMaxWidth().padding(horizontal = padding)
}
