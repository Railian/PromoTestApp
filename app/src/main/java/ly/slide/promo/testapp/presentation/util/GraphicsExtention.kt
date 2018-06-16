package ly.slide.promo.testapp.presentation.util

import android.graphics.PointF
import android.graphics.RectF

fun pointOfCircle(center: PointF, radius: Float, angle: Double, destination: PointF) {
    destination.x = center.x + radius * Math.cos(angle).toFloat()
    destination.y = center.y + radius * Math.sin(angle).toFloat()
}

fun pointOfCircle(center: PointF, radius: Float, angle: Double): PointF {
    return PointF(
            center.x + radius * Math.cos(angle).toFloat(),
            center.y + radius * Math.sin(angle).toFloat()
    )
}

fun rectWithCenter(center: PointF, size: Float): RectF {
    return RectF(
            center.x - size / 2f, center.y - size / 2f,
            center.x + size / 2f, center.y + size / 2f
    )
}
