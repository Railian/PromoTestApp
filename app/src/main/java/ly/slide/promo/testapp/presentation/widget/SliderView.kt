package ly.slide.promo.testapp.presentation.widget

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import ly.slide.promo.testapp.domain.Media
import ly.slide.promo.testapp.presentation.widget.slide.ImageSlideView
import ly.slide.promo.testapp.presentation.widget.slide.Slide
import ly.slide.promo.testapp.presentation.widget.slide.VideoSlideView
import kotlin.properties.Delegates

class SliderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {

    var animationDuration: Long by Delegates.vetoable(
            initialValue = 1000L,
            onChange = { _, _, newValue -> newValue in 300..3000 }
    )

    private var previousSlide: View? = null
    private var currentSlide: View? = null
    private var nextSlide: View? = null

    fun showSlide(media: Media?) {
        previousSlide = currentSlide
        currentSlide = nextSlide
        nextSlide = null
        when {
            currentSlide?.tag == media -> {
                (currentSlide as? Slide)?.start()
                currentSlide?.animate()
                        ?.alpha(1f)
                        ?.setDuration(animationDuration)
                        ?.setListener(object : Animator.AnimatorListener {
                            override fun onAnimationStart(animator: Animator) = Unit
                            override fun onAnimationRepeat(animator: Animator) = Unit
                            override fun onAnimationEnd(animator: Animator) = removeSlide(previousSlide)
                            override fun onAnimationCancel(animator: Animator) = removeSlide(previousSlide)
                        })
                        ?.start()
            }
            media == null -> currentSlide = null
            currentSlide?.bindMedia(media) != true -> {
                currentSlide = createSlide(media.type).apply {
                    bindMedia(media)
                    (this as? VideoSlideView)?.start()
                    addSlide(this, visible = true)
                }
            }
        }
    }

    fun prepareSlide(media: Media?) {
        nextSlide = media?.let {
            createSlide(media.type).apply {
                bindMedia(media)
                addSlide(this, visible = false)
            }
        }
    }

    private fun createSlide(type: Media.Type): View {
        return when (type) {
            Media.Type.IMAGE -> ImageSlideView(context).apply {
                layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
            Media.Type.VIDEO -> VideoSlideView(context).apply {
                layoutParams = RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    }

    private fun View.bindMedia(media: Media?): Boolean {
        tag = media
        when {
            media == null -> return false
            media.type == Media.Type.IMAGE && this is ImageSlideView -> prepare(media.uri)
            media.type == Media.Type.VIDEO && this is ImageSlideView -> prepare(media.uri)
            else -> return false
        }
        return true
    }

    private fun removeSlide(view: View?) {
        view?.let(::removeView)
    }

    private fun addSlide(view: View?, visible: Boolean) {
        view?.apply { alpha = if (visible) 1f else 0f }?.let(::addView)
    }
}