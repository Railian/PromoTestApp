package ly.slide.promo.testapp.platform

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import androidx.core.database.getInt
import androidx.core.database.getLong
import androidx.core.database.getString
import io.reactivex.Single
import ly.slide.promo.testapp.domain.Media

object MediaLoader {

    fun getImagesAndVideos(context: Context): Single<List<Media>> {
        return Single.fromCallable {
            with(context) {
                val internalImages = getMediaListFromUri(MediaStore.Images.Media.INTERNAL_CONTENT_URI, Media.Type.IMAGE)
                val externalImages = getMediaListFromUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Media.Type.IMAGE)
                val internalVideos = getMediaListFromUri(MediaStore.Video.Media.INTERNAL_CONTENT_URI, Media.Type.VIDEO)
                val externalVideos = getMediaListFromUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, Media.Type.VIDEO)
                internalImages + externalImages + internalVideos + externalVideos
            }
        }
    }

    private fun Context.getMediaListFromUri(uri: Uri, mediaType: Media.Type): List<Media> {

        val projection = arrayOf(
                MediaStore.MediaColumns._ID,
                MediaStore.MediaColumns.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED
        )
        val cursor = contentResolver.query(uri, projection, null, null, null)
        val mediaList = mutableListOf<Media>()

        cursor?.use {
            while (it.moveToNext()) {
                mediaList += Media(
                        type = mediaType,
                        uri = Uri.withAppendedPath(uri, it.getInt(MediaStore.MediaColumns._ID).toString()),
                        name = it.getString(MediaStore.MediaColumns.DISPLAY_NAME),
                        addedAt = it.getLong(MediaStore.MediaColumns.DATE_ADDED)
                )
            }
        }

        return mediaList
    }
}