package ly.slide.promo.testapp.domain.entity

import android.net.Uri

data class Media(
        val type: Type,
        val uri: Uri,
        val name: String,
        val addedAt: Long
) {
    enum class Type { IMAGE, VIDEO }
}