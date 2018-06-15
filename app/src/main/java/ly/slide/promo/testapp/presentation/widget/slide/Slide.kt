package ly.slide.promo.testapp.presentation.widget.slide

import android.net.Uri

typealias OnPreparedListener = () -> Unit

interface Slide {
    val isStarted: Boolean
    fun prepare(uri: Uri?, onPreparedListener: OnPreparedListener? = null)
    fun start()
    fun pause()
}