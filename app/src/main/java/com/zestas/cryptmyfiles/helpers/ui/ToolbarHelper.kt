package com.zestas.cryptmyfiles.helpers.ui

import android.content.Context
import androidx.core.content.ContextCompat.getColor
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.google.android.material.appbar.MaterialToolbar
import com.zestas.cryptmyfiles.R

class ToolbarHelper {
    companion object {
        fun makeTitle(context: Context, toolbar: MaterialToolbar, text: String) {
            val start = getColor(context, R.color.ZenCryptDark)
            val end = getColor(context, R.color.ZenCryptSecondaryLight)
            val spannable = text.toSpannable()
            spannable[0..text.length] = LinearGradientSpan(text, text, start, end)
            toolbar.title = spannable
        }
    }
}