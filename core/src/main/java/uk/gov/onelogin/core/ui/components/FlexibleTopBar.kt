package uk.gov.onelogin.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.roundToInt

/**
 * This top bar uses the same scroll behaviors as Material3 top bars,
 * but it doesn't have a layout of its own. It is simply a container in
 * which you can put whatever you want.
 */
@ExperimentalMaterial3Api
@Composable
fun FlexibleTopBar(
    modifier: Modifier = Modifier,
    colors: FlexibleTopBarColors = FlexibleTopBarDefaults.topAppBarColors(),
    scrollBehavior: TopAppBarScrollBehavior? = null,
    content: @Composable () -> Unit
) {
    var heightOffsetLimit by remember {
        mutableFloatStateOf(0f)
    }
    LaunchedEffect(heightOffsetLimit) {
        if (scrollBehavior?.state?.heightOffsetLimit != heightOffsetLimit) {
            scrollBehavior?.state?.heightOffsetLimit = heightOffsetLimit
        }
    }

    val fraction by remember(scrollBehavior) {
        derivedStateOf {
            val colorTransitionFraction = scrollBehavior?.state?.overlappedFraction ?: 0f
            if (colorTransitionFraction > FRACTION_THRESHOLD) 1f else 0f
        }
    }
    val appBarContainerColor by animateColorAsState(
        targetValue = colors.containerColor(fraction),
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
    )

    val appBarDragModifier = if (scrollBehavior != null && !scrollBehavior.isPinned) {
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scrollBehavior.state.heightOffset += delta
            },
            onDragStopped = { velocity ->
                settleAppBar(
                    scrollBehavior.state,
                    velocity,
                    scrollBehavior.flingAnimationSpec,
                    scrollBehavior.snapAnimationSpec
                )
            }
        )
    } else {
        Modifier
    }

    Surface(modifier = modifier.then(appBarDragModifier), color = appBarContainerColor) {
        Layout(
            content = content,
            modifier = modifier,
            measurePolicy = { measurables, constraints ->
                val placeable = measurables.firstOrNull()?.measure(constraints.copy(minWidth = 0))
                heightOffsetLimit = (placeable?.height?.toFloat() ?: 0f) * -1
                val scrollOffset = scrollBehavior?.state?.heightOffset ?: 0f
                val height = (placeable?.height?.toFloat() ?: 0f) + scrollOffset
                val layoutHeight = height.roundToInt().coerceAtLeast(0)
                layout(constraints.maxWidth, layoutHeight) {
                    placeable?.place(0, scrollOffset.toInt())
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    if (state.collapsedFraction < FRACTION_THRESHOLD || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > HALF) this.cancelAnimation()
            }
    }
    if (snapAnimationSpec != null && state.heightOffset < 0 &&
        state.heightOffset > state.heightOffsetLimit
    ) {
        AnimationState(initialValue = state.heightOffset).animateTo(
            if (state.collapsedFraction < HALF) {
                0f
            } else {
                state.heightOffsetLimit
            },
            animationSpec = snapAnimationSpec
        ) { state.heightOffset = value }
    }

    return Velocity(0f, remainingVelocity)
}

@Stable
class FlexibleTopBarColors internal constructor(
    val containerColor: Color,
    val scrolledContainerColor: Color
) {
    /**
     * Represents the container color used for the top app bar.
     *
     * A [colorTransitionFraction] provides a percentage value that can be used to generate a color.
     * Usually, an app bar implementation will pass in a [colorTransitionFraction] read from
     * the [TopAppBarState.collapsedFraction] or the [TopAppBarState.overlappedFraction].
     *
     * @param colorTransitionFraction a `0.0` to `1.0` value that represents a color transition
     * percentage
     */
    @Composable
    fun containerColor(colorTransitionFraction: Float): Color {
        return lerp(
            containerColor,
            scrolledContainerColor,
            FastOutLinearInEasing.transform(colorTransitionFraction)
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is FlexibleTopBarColors) return false

        if (containerColor != other.containerColor) return false
        if (scrolledContainerColor != other.scrolledContainerColor) return false

        return true
    }

    override fun hashCode(): Int {
        var result = containerColor.hashCode()
        result = 31 * result + scrolledContainerColor.hashCode()

        return result
    }
}

object FlexibleTopBarDefaults {
    @Composable
    fun topAppBarColors(
        containerColor: Color = MaterialTheme.colorScheme.primary,
        scrolledContainerColor: Color = MaterialTheme.colorScheme.applyTonalElevation(
            backgroundColor = containerColor,
            elevation = 4.dp
        )
    ): FlexibleTopBarColors =
        FlexibleTopBarColors(
            containerColor,
            scrolledContainerColor
        )
}

internal fun ColorScheme.applyTonalElevation(backgroundColor: Color, elevation: Dp): Color {
    return if (backgroundColor == surface) {
        surfaceColorAtElevation(elevation)
    } else {
        backgroundColor
    }
}

/**
 * Computes the surface tonal color at different elevation levels e.g. surface1 through surface5.
 *
 * @param elevation Elevation value used to compute alpha of the color overlay layer.
 *
 * @return the [ColorScheme.surface] color with an alpha of the [ColorScheme.surfaceTint] color
 * overlaid on top of it.

 */
fun ColorScheme.surfaceColorAtElevation(
    elevation: Dp
): Color {
    if (elevation == 0.dp) return surface
    val alpha = (
        (ALPHA_MODIFIER * ln(elevation.value + ELEVATION_OFFSET)) + ALPHA_OFFSET
        ) / PERCENTAGE_DIVIDER
    return surfaceTint.copy(alpha = alpha).compositeOver(surface)
}

private const val FRACTION_THRESHOLD = 0.01f
private const val HALF = 0.5f
private const val ALPHA_MODIFIER = 4.5f
private const val ALPHA_OFFSET = 2f
private const val ELEVATION_OFFSET = 1
private const val PERCENTAGE_DIVIDER = 100f
