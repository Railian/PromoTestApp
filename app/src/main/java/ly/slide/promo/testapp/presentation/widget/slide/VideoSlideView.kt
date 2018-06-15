package ly.slide.promo.testapp.presentation.widget.slide

import android.content.Context
import android.net.Uri
import com.yqritc.scalablevideoview.ScalableType
import com.yqritc.scalablevideoview.ScalableVideoView

class VideoSlideView(context: Context) : ScalableVideoView(context), Slide {

    override var isStarted: Boolean = false

    override fun prepare(uri: Uri?, onPreparedListener: OnPreparedListener?) {
        mMediaPlayer?.apply {
            stop()
            release()
        }
        setDataSource(context, uri!!)
        prepareAsync {
            setScalableType(ScalableType.CENTER_CROP)
            if (isStarted) it.start()
            onPreparedListener?.invoke()
        }
    }

    override fun start() {
        isStarted = true
        mMediaPlayer?.start()
    }
}