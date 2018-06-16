package ly.slide.promo.testapp.presentation.util

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

fun View.getColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this.context, colorRes)
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide(gone: Boolean = true) {
    this.visibility = when {
        gone -> View.GONE
        else -> View.INVISIBLE
    }
}

fun viewOutlineProvider(initOutline: (View, Outline) -> Unit): ViewOutlineProvider {
    return object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline): Unit = initOutline(view, outline)
    }
}