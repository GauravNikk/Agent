package com.gaurav.fieldagent.utils
//package com.celestial.cab.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 * A utility object for handling Android runtime permissions for API levels 24 (Android 7) and above.
 * This provides a stable way to check, request, and handle permission results,
 * including scenarios where the user has permanently denied a permission.
 */
object PermissionUtils {

    /**
     * Returns the appropriate permissions for reading media from external storage,
     * adapting to platform changes in Android 13 (API 33).
     *
     * On Android 13 and higher, it requests granular media permissions (`READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO`).
     * On older versions, it requests `READ_EXTERNAL_STORAGE`.
     * For apps needing to write to storage on versions below Android 10 (API 29), you should manually add
     * `Manifest.permission.WRITE_EXTERNAL_STORAGE` to your list.
     *
     * @return A list of permission strings to be requested.
     */
    fun getReadMediaPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO)
        } else {
            listOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    /**
     * Checks if all the given permissions have been granted.
     *
     * @param context The context.
     * @param permissions A list of permissions to check.
     * @return `true` if all permissions are granted, `false` otherwise.
     */
    fun hasPermissions(context: Context, permissions: List<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Directly checks if the camera permission has been granted.
     * @return `true` if `Manifest.permission.CAMERA` is granted, `false` otherwise.
     */
    fun hasCameraPermission(context: Context): Boolean {
        return hasPermissions(context, listOf(Manifest.permission.CAMERA))
    }

    /**
     * Directly checks if the appropriate read media/storage permissions have been granted for the current OS version.
     * @return `true` if the necessary permissions are granted, `false` otherwise.
     */
    fun hasReadMediaPermission(context: Context): Boolean {
        return hasPermissions(context, getReadMediaPermissions())
    }

    /**
     * Determines if a rationale should be shown to the user for any of the requested permissions.
     * This is true if the user has denied the permission in the past without checking "Don't ask again".
     *
     * @param activity The activity.
     * @param permissions A list of permissions to check for rationale.
     * @return `true` if a rationale should be shown for at least one permission, `false` otherwise.
     */
    fun shouldShowRationale(activity: Activity, permissions: List<String>): Boolean {
        return permissions.any {
            ActivityCompat.shouldShowRequestPermissionRationale(activity, it)
        }
    }

    /**
     * Requests the specified permissions from the user.
     * The result is delivered to the activity's `onRequestPermissionsResult` callback.
     *
     * @param activity The activity that is requesting the permissions.
     * @param permissions A list of permissions to request.
     * @param requestCode The request code to identify this permission request.
     */
    fun requestPermissions(activity: Activity, permissions: List<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions.toTypedArray(), requestCode)
    }

    /**
     * Directly requests the camera permission.
     */
    fun requestCameraPermission(activity: Activity, requestCode: Int) {
        requestPermissions(activity, listOf(Manifest.permission.CAMERA), requestCode)
    }

    /**
     * Directly requests the appropriate read media/storage permissions for the current OS version.
     */
    fun requestReadMediaPermission(activity: Activity, requestCode: Int) {
        requestPermissions(activity, getReadMediaPermissions(), requestCode)
    }

    /**
     * A helper to be called from `onRequestPermissionsResult` to process the outcome.
     * It simplifies checking the request code and grant results.
     *
     * @param requestCode The request code passed to `onRequestPermissionsResult`.
     * @param permissions The permissions array passed to `onRequestPermissionsResult`.
     * @param grantResults The grant results passed to `onRequestPermissionsResult`.
     * @param expectedRequestCode The request code this handler is responsible for.
     * @param onGranted A lambda to be executed if all permissions were granted.
     * @param onDenied A lambda to be executed with a list of the permissions that were denied.
     */
    fun handlePermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        expectedRequestCode: Int,
        onGranted: () -> Unit,
        onDenied: (deniedPermissions: List<String>) -> Unit
    ) {
        if (requestCode == expectedRequestCode) {
            val deniedPermissions = permissions.filterIndexed { index, _ ->
                grantResults.getOrNull(index) != PackageManager.PERMISSION_GRANTED
            }

            if (deniedPermissions.isEmpty() && grantResults.isNotEmpty()) {
                onGranted()
            } else {
                onDenied(deniedPermissions)
            }
        }
    }
}