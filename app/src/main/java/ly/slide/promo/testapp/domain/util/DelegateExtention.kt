package ly.slide.promo.testapp.domain.util

import kotlin.properties.Delegates
import kotlin.properties.ObservableProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

inline fun <T> Delegates.vetoable(
        initialValue: T,
        crossinline onChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Boolean,
        crossinline afterChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit
): ReadWriteProperty<Any?, T> =
        object : ObservableProperty<T>(initialValue) {
            override fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = onChange(property, oldValue, newValue)
            override fun afterChange(property: KProperty<*>, oldValue: T, newValue: T): Unit = afterChange(property, oldValue, newValue)
        }