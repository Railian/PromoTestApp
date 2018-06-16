package ly.slide.promo.testapp.presentation.util

import android.os.Parcel
import android.os.Parcelable
import org.joda.time.DateTimeZone
import org.joda.time.LocalTime

interface KParcelable : Parcelable {
    override fun describeContents() = 0
    override fun writeToParcel(dest: Parcel, flags: Int)
}

inline fun <reified T> parcelableCreator(crossinline create: (Parcel) -> T): Parcelable.Creator<T> {
    return object : Parcelable.Creator<T> {
        override fun createFromParcel(source: Parcel): T = create(source)
        override fun newArray(size: Int): Array<T?> = arrayOfNulls(size)
    }
}

inline fun <reified T : Parcelable> Parcel.readParcelable(): T {
    return this.readParcelable(T::class.java.classLoader)
}

inline fun Parcel.writeLocalTime(time: LocalTime) {
    this.writeInt(time.millisOfDay)
}

inline fun Parcel.readLocalTime(): LocalTime {
    return LocalTime(this.readInt(), DateTimeZone.UTC)
}