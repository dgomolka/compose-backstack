@file:Suppress("RemoveEmptyParenthesesFromAnnotationEntry", "DEPRECATION")

package com.zachklipp.compose.backstack

import androidx.ui.core.*
import androidx.ui.unit.Density
import androidx.ui.unit.IntPxPosition
import androidx.ui.unit.IntPxSize
import androidx.ui.unit.ipx
import com.zachklipp.compose.backstack.BackstackTransition.Crossfade
import com.zachklipp.compose.backstack.BackstackTransition.Slide

/**
 * Defines transitions for a [Backstack]. Transitions control how screens are rendered by returning
 * [Modifier]s that will be used to wrap screen composables.
 *
 * @see Slide
 * @see Crossfade
 */
interface BackstackTransition {

    /**
     * Returns a [Modifier] to use to draw screen in a [Backstack].
     *
     * @param visibility A float in the range `[0, 1]` that indicates at what visibility this screen
     * should be drawn. For example, this value will increase when [isTop] is true and the transition
     * is in the forward direction.
     * @param isTop True only when being called for the top screen. E.g. if the screen is partially
     * visible, then the top screen is always transitioning _out_, and non-top screens are either
     * transitioning out or invisible.
     */
    fun modifierForScreen(
        visibility: Float,
        isTop: Boolean
    ): Modifier

    /**
     * A simple transition that slides screens horizontally.
     */
    object Slide : BackstackTransition {
        override fun modifierForScreen(
            visibility: Float,
            isTop: Boolean
        ): Modifier = PercentageLayoutOffset(
            offset = if (isTop) 1f - visibility else -1 + visibility
        )

        private class PercentageLayoutOffset(private val offset: Float) : LayoutModifier {
            override fun MeasureScope.measure(
                measurable: Measurable,
                constraints: Constraints,
                layoutDirection: LayoutDirection
            ): MeasureScope.MeasureResult {
                var realOffset = offset.coerceIn(-1f..1f)
                if (layoutDirection == LayoutDirection.Rtl) realOffset *= -1f

                val placeable = measurable.measure(constraints)
                return layout(placeable.width, placeable.height) {
                    val x = placeable.width * realOffset
                    val y = 0.ipx
                    placeable.placeAbsolute(
                        (if (layoutDirection == LayoutDirection.Ltr) x else -x),
                        y
                    )
                }
            }
        }
    }

    /**
     * A simple transition that crossfades between screens.
     */
    object Crossfade : BackstackTransition {
        override fun modifierForScreen(
            visibility: Float,
            isTop: Boolean
        ): Modifier = Modifier.drawOpacity(visibility)
    }
}
