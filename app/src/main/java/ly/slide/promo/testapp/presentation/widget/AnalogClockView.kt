package ly.slide.promo.testapp.presentation.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Size
import android.view.View
import ly.slide.promo.testapp.R
import ly.slide.promo.testapp.presentation.util.getColor
import org.joda.time.LocalTime
import kotlin.properties.Delegates

@Suppress("JoinDeclarationAndAssignment")
class AnalogClockView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var viewport: Size by Delegates.notNull()
    private var center: PointF by Delegates.notNull()
    private var clockRadius: Float by Delegates.notNull()
    private var handPoint: PointF

    private val clockFacePaint: Paint
    private val clockHandsPaint: Paint

    var time: LocalTime by Delegates.observable(
            initialValue = LocalTime(0, 0),
            onChange = { _, _, _ -> invalidate() }
    )

    init {
        handPoint = PointF()

        clockFacePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clockFacePaint.style = Paint.Style.FILL

        clockHandsPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        clockHandsPaint.style = Paint.Style.STROKE
        clockHandsPaint.strokeCap = Paint.Cap.ROUND
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        viewport = Size(w, h)
        center = PointF(w * 0.5f, h * 0.5f)
        clockRadius = Math.min(w, h) * 0.5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockFace(canvas)
        drawClockHands(canvas, LocalTime())
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
            strokeWidth = 10f
            pointOfCircle(handPoint, center, clockRadius * 0.5f, time.angleOfHoursHand)
            canvas.drawLine(center.x, center.y, handPoint.x, handPoint.y, this)

            color = getColor(R.color.colorClockHandMinutes)
            strokeWidth = 6f
            pointOfCircle(handPoint, center, clockRadius * 0.6f, time.angleOfMinutesHand)
            canvas.drawLine(center.x, center.y, handPoint.x, handPoint.y, this)

            color = getColor(R.color.colorClockHandSeconds)
            strokeWidth = 4f
            pointOfCircle(handPoint, center, clockRadius * 0.65f, time.angleOfSecondsHand)
            canvas.drawLine(center.x, center.y, handPoint.x, handPoint.y, this)
        }
    }

    private fun pointOfCircle(destination: PointF, center: PointF, radius: Float, angle: Double) {
        destination.x = center.x + radius * Math.cos(angle).toFloat()
        destination.y = center.y + radius * Math.sin(angle).toFloat()
    }

    private val LocalTime.angleOfHoursHand: Double
        get() = (hourOfDay + minuteOfHour / 60.0) / 12.0 * (2 * Math.PI) - Math.PI / 2

    private val LocalTime.angleOfMinutesHand: Double
        get() = minuteOfHour / 60.0 * (2 * Math.PI) - Math.PI / 2

    private val LocalTime.angleOfSecondsHand: Double
        get() = secondOfMinute / 60.0 * (2 * Math.PI) - Math.PI / 2
}