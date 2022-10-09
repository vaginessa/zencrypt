package com.zestas.cryptmyfiles.helpers.ui

import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance
import androidx.annotation.ColorInt

/**
 * Applies a linear gradient on the text to which the span is attached.
 *
 * @param containingText The text that encloses the text that needs the gradient.
 * @param textToStyle The text that the gradient will be applied on. Can be a substring of `containingText` or equal to `containingText`.
 * @param startColorInt Resolved color to use as the gradient's start color.
 * @param endColorInt Resolved color to use as the gradient's end color.
 */
class LinearGradientSpan(
    private val containingText: String,
    private val textToStyle: String,
    @ColorInt private val startColorInt: Int,
    @ColorInt private val endColorInt: Int
) : CharacterStyle(), UpdateAppearance {


    override fun updateDrawState(tp: TextPaint?) {
        tp ?: return
        var leadingWidth = 0f
        val indexOfTextToStyle = containingText.indexOf(textToStyle)
        if (!containingText.startsWith(textToStyle) && containingText != textToStyle) {
            leadingWidth = tp.measureText(containingText, 0, indexOfTextToStyle)
        }
        val gradientWidth = tp.measureText(containingText, indexOfTextToStyle, indexOfTextToStyle + textToStyle.length)

        tp.shader = LinearGradient(
            leadingWidth,
            0f,
            leadingWidth + gradientWidth,
            0f,
            startColorInt,
            endColorInt,
            Shader.TileMode.REPEAT
        )
    }
}