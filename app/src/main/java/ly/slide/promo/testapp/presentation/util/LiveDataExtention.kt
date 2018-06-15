package ly.slide.promo.testapp.presentation.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

fun <T> Fragment.observeLiveData(liveData: LiveData<T>, observer: (value: T?) -> Unit) {
    liveData.observe(this, Observer { observer(it) })
}