package ly.slide.promo.testapp.presentation.util

import android.graphics.Outline
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.ViewOutlineProvider
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf

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

class ViewSavedState(
        val viewState: Bundle,
        val superState: Parcelable?
) : KParcelable {

    private constructor(source: Parcel) : this(
            viewState = source.readBundle(Bundle::class.java.classLoader),
            superState = source.readParcelable()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeBundle(viewState)
        dest.writeParcelable(superState, flags)
    }

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = parcelableCreator(::ViewSavedState)
    }
}

fun Parcelable?.alsoSaveState(vararg state: Pair<String, Any?>): ViewSavedState {
    return ViewSavedState(viewState = bundleOf(*state), superState = this)
}

fun Parcelable?.restoreSavedState(function: Bundle.() -> Unit): Parcelable? {
    val savedState = this as? ViewSavedState
    savedState?.viewState?.let { function(it) }
    return savedState?.superState
}