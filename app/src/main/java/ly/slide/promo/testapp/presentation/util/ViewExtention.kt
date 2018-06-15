package ly.slide.promo.testapp.presentation.util

import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun View.getColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this.context, colorRes)
}