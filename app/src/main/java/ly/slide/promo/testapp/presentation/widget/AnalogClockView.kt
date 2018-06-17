package ly.slide.promo.testapp.presentation.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Size
import android.view.View
import androidx.core.graphics.toRect
import ly.slide.promo.testapp.R
import ly.slide.promo.testapp.platform.dp2px
import ly.slide.promo.testapp.presentation.UiConstants
import ly.slide.promo.testapp.presentation.util.*
import org.joda.time.LocalTime
import kotlin.properties.Delegates

@Suppress("JoinDeclarationAndAssignment")
class AnalogClockView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    companion object {
        private const val STATE_TIME = "state:time"
        private const val STATE_ELEVATION = "state:elevation"
        private const val STATE_ALPHA = "state:alpha"
        private const val STATE_VISIBILITY = "state:visibility"
    }

    private var viewport: Size by Delegates.notNull()
    private var center: PointF by Delegates.notNull()
    private var clockRadius: Float by Delegates.notNull()
    private var handPoint: PointF

    private val clockFacePaint: Paint
    private val clockHandsPaint: Paint

    var time: LocalTime by Delegates.observable(
            initialValue = LocalTime.MIDNIGHT,
            onChange = { _, _, _ -> invalidate() }
    )

    init {
        handPoint = PointF()

        clockFacePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clockFacePaint.style = Paint.Style.FILL

        clockHandsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clockHandsPaint.style = Paint.Style.STROKE
        clockHandsPaint.strokeCap = Paint.Cap.ROUND

        elevation = dp2px(UiConstants.CLOCK_ELEVATION_DEFAULT)
        alpha = UiConstants.CLOCK_OPACITY_DEFAULT / 100f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewport = Size(w, h)
        center = PointF(w * 0.5f, h * 0.5f)
        clockRadius = Math.min(w, h) * 0.5f

        outlineProvider = viewOutlineProvider { _, outline ->
            outline.setOval(rectWithCenter(center, clockRadius * 2 * 0.9f).toRect())
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockFace(canvas)
        drawClockHands(canvas, time)
    }

    private fun drawClockFace(canvas: Canvas) {
        with(clockFacePaint) {

            color = getColor(R.color.colorClockFaceSecondary)
            canvas.drawCircle(center.x, center.y, clockRadius * 0.9f, this)

            color = getColor(R.color.colorClockFacePrimary)
            canvas.drawCircle(center.x, center.y, clockRadius * 0.7f, this)
        }
    }

    private fun drawClockHands(canvas: Canvas, time: LocalTime) {
        with(clockHandsPaint) {

            color = getColor(R.color.colorClockHandHours)
            strokeWidth = clockRadius * 0.08f
            pointOfCircle(center, clockRadius * 0.45f, time.angleOfHoursHand, handPoint)
            canvas.drawLine(center.x, center.y, handPoint.x, handPoint.y, this)

            color = getColor(R.color.colorClockHandMinutes)
            strokeWidth = clockRadius * 0.06f
            pointOfCircle(center, clockRadius * 0.6f, time.angleOfMinutesHand, handPoint)
            canvas.drawLine(center.x, center.y, handPoint.x, handPoint.y, this)

            color = getColor(R.color.colorClockHandSeconds)
            strokeWidth = clockRadius * 0.04f
            pointOfCircle(center, clockRadius * 0.65f, time.angleOfSecondsHand, handPoint)
            canvas.drawLine(center.x, center.y, handPoint.x, handPoint.y, this)
        }
    }

    private val LocalTime.angleOfHoursHand: Double
        get() = (hourOfDay + minuteOfHour / 60f) / 12f * (2 * Math.PI) - Math.PI / 2

    private val LocalTime.angleOfMinutesHand: Double
        get() = minuteOfHour / 60f * (2 * Math.PI) - Math.PI / 2

    private val LocalTime.angleOfSecondsHand: Double
        get() = secondOfMinute / 60f * (2 * Math.PI) - Math.PI / 2

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        return superState.alsoSaveState(
                STATE_TIME to time,
                STATE_ELEVATION to elevation,
                STATE_ALPHA to alpha,
                STATE_VISIBILITY to visibility
        )
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        val superState = state.restoreSavedState {
            time = get(STATE_TIME) as LocalTime
            elevation = getFloat(STATE_ELEVATION)
            alpha = getFloat(STATE_ALPHA)
            visibility = getInt(STATE_VISIBILITY)
        }
        super.onRestoreInstanceState(superState)
    }
}