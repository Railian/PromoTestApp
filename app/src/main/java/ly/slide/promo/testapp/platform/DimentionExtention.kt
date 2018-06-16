package ly.slide.promo.testapp.platform

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment

fun Context.dp2px(dp: Float): Float {
    return dp * resources.displayMetrics.density
}

fun Context.px2dp(px: Float): Float {
    return px / resources.displayMetrics.density
}

fun Context.sp2px(sp: Float): Float {
    return sp * resources.displayMetrics.scaledDensity
}

fun Context.px2sp(px: Float): Float {
    return px / resources.displayMetrics.scaledDensity
}

fun View.dp2px(dp: Float): Float = context.dp2px(dp)
fun View.px2dp(px: Float): Float = context.px2dp(px)
fun View.sp2px(sp: Float): Float = context.sp2px(sp)
fun View.px2sp(px: Float): Float = context.px2sp(px)

fun Fragment.dp2px(dp: Float): Float = requireContext().dp2px(dp)
fun Fragment.px2dp(px: Float): Float = requireContext().px2dp(px)
fun Fragment.sp2px(sp: Float): Float = requireContext().sp2px(sp)
fun Fragment.px2sp(px: Float): Float = requireContext().px2sp(px)