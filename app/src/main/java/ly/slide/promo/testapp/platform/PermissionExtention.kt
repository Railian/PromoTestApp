package ly.slide.promo.testapp.platform

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

inline fun Activity.withPermissions(
        vararg permissions: String,
        request: Boolean = true,
        crossinline block: (allPermissionsGranted: Boolean) -> Unit
) {
    val permissionsResult = permissions.map { ContextCompat.checkSelfPermission(this, it) }
    if (permissionsResult.all { it == PackageManager.PERMISSION_GRANTED }) {
        block(true)
    } else {
        if (request) {
            val requiredPermissions = permissionsResult.mapIndexedNotNull { index, result ->
                permissions[index].takeIf { result != PackageManager.PERMISSION_GRANTED }
            }
            requiredPermissions
                    .filter { !ActivityCompat.shouldShowRequestPermissionRationale(this, it) }
                    .takeIf { it.isNotEmpty() }
                    ?.let { ActivityCompat.requestPermissions(this, it.toTypedArray(), 0) }
        }
        block(false)
    }
}

inline fun Fragment.withPermissions(
        vararg permissions: String,
        request: Boolean = true,
        crossinline block: (hasPermissions: Boolean) -> Unit
) {
    activity?.withPermissions(*permissions, request = request, block = block)
}