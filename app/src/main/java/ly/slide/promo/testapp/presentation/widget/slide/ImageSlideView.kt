package ly.slide.promo.testapp.presentation.widget.slide

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class ImageSlideView(context: Context) : ImageView(context), Slide {

    init {
        scaleType = ScaleType.CENTER_CROP
    }

    override var isStarted: Boolean = false

    override fun prepare(uri: Uri?, onPreparedListener: OnPreparedListener?) {
        Picasso.get()
                .load(uri)
                .resize(1280, 0)
                .into(this, handleCallback(onPreparedListener))
    }

    override fun start() {
        isStarted = true
    }

    override fun pause() = Unit

    override fun onDetachedFromWindow() {
        Picasso.get().cancelRequest(this)
        super.onDetachedFromWindow()
    }

    private fun handleCallback(onPreparedListener: OnPreparedListener?): Callback {
        return object : Callback {

            override fun onSuccess() {
                onPreparedListener?.invoke()
            }

            override fun onError(e: Exception?) = Unit
        }
    }
}