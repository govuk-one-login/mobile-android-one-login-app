package uk.gov.onelogin.core.utils

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset

object ModifierExtensions {
    fun Modifier.drawAfterMeasured(previousItemHeightPx: Int): Modifier = this.offset {
        IntOffset(x = 0, y = previousItemHeightPx)
    }
}
