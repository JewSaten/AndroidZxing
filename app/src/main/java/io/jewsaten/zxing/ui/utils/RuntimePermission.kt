package io.jewsaten.zxing.ui.utils

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import java.util.*

/**
 * Created by Administrator on 2018/3/16.
 */
object RuntimePermission {

    val DEFAULT_SETTINGS_REQ_CODE = 16061

    interface PermissionsResultCallback : ActivityCompat.OnRequestPermissionsResultCallback {

        fun onPermissionsGranted(requestCode: Int, perms: List<String>)

        fun onPermissionsDenied(requestCode: Int, perms: List<String>)

        fun onPermissionsPossessed()

    }

    fun hasPermissions(context: Context,perms: Array<String>): Boolean {
        // Always return true for SDK < M, let the system deal with the permissions
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }

        for (perm in perms) {
            val hasPerm = ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
            if (!hasPerm) {
                return false
            }
        }

        return true
    }

    fun requestPermissions(`object`: Any, rationale: String,
                           requestCode: Int, perms: Array<String>) {
        requestPermissions(`object`, rationale,
                android.R.string.ok,
                android.R.string.cancel,
                requestCode, perms)
    }

    fun requestPermissions(`object`: Any, rationale: String,
                           positiveButton: Int,
                           negativeButton: Int,
                           requestCode: Int, perms: Array<String>) {

        checkCallingObjectSuitability(`object`)

        var shouldShowRationale = false
        for (perm in perms) {
            shouldShowRationale = shouldShowRationale || shouldShowRequestPermissionRationale(`object`, perm)
        }

        if (shouldShowRationale) {
            val activity = getActivity(`object`) ?: return

            val dialog = AlertDialog.Builder(activity)
                    .setMessage(rationale)
                    .setPositiveButton(positiveButton) { dialog, which -> executePermissionsRequest(`object`, perms, requestCode) }
                    .setNegativeButton(negativeButton) { dialog, which ->
                        // act as if the permissions were denied
                        (`object` as? PermissionsResultCallback)?.onPermissionsDenied(requestCode, Arrays.asList(*perms))
                    }.create()
            dialog.show()
        } else {
            executePermissionsRequest(`object`, perms, requestCode)
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                   grantResults: IntArray, `object`: Any) {

        checkCallingObjectSuitability(`object`)

        // Make a collection of granted and denied permissions from the request.
        val granted = ArrayList<String>()
        val denied = ArrayList<String>()
        for (i in permissions.indices) {
            val perm = permissions[i]
            if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                granted.add(perm)
            } else {
                denied.add(perm)
            }
        }

        // Report granted permissions, if any.
        if (!granted.isEmpty()) {
            // Notify callbacks
            (`object` as? PermissionsResultCallback)?.onPermissionsGranted(requestCode, granted)
        }

        // Report denied permissions, if any.
        if (!denied.isEmpty()) {
            (`object` as? PermissionsResultCallback)?.onPermissionsDenied(requestCode, denied)
        }

        // If 100% successful, call annotated methods
        if (!granted.isEmpty() && denied.isEmpty()) {
            (`object` as? PermissionsResultCallback)?.onPermissionsPossessed()
        }
    }


    fun somePermissionPermanentlyDenied(`object`: Any,
                                        rationale: String,
                                        positiveButton: Int,
                                        negativeButton: Int,
                                        deniedPerms: List<String>): Boolean {
        return somePermissionPermanentlyDenied(`object`, rationale,
                positiveButton, negativeButton, null, deniedPerms)
    }


    fun somePermissionPermanentlyDenied(`object`: Any,
                                        rationale: String,
                                        positiveButton: Int,
                                        negativeButton: Int,
                                        negativeButtonOnClickListener: DialogInterface.OnClickListener?,
                                        deniedPerms: List<String>): Boolean {
        for (perm in deniedPerms) {
            if (!shouldShowRequestPermissionRationale(`object`, perm)) {
                val activity = getActivity(`object`) ?: return true

                val dialog = AlertDialog.Builder(activity)
                        .setMessage(rationale)
                        .setPositiveButton(positiveButton) { dialog, which ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", activity.packageName, null)
                            intent.data = uri
                            startAppSettings(`object`, intent)
                        }
                        .setNegativeButton(negativeButton, negativeButtonOnClickListener)
                        .create()
                dialog.show()

                return true
            }
        }
        return false
    }

    @TargetApi(23)
    private fun shouldShowRequestPermissionRationale(`object`: Any, perm: String): Boolean {
        return if (`object` is Activity) {
            ActivityCompat.shouldShowRequestPermissionRationale(`object`, perm)
        } else (`object` as? Fragment)?.shouldShowRequestPermissionRationale(perm) ?: ((`object` as? android.app.Fragment)?.shouldShowRequestPermissionRationale(perm) ?: false)
    }

    @TargetApi(23)
    private fun executePermissionsRequest(`object`: Any, perms: Array<String>, requestCode: Int) {
        checkCallingObjectSuitability(`object`)

        if (`object` is Activity) {
            ActivityCompat.requestPermissions(`object`, perms, requestCode)
        } else (`object` as? Fragment)?.requestPermissions(perms, requestCode) ?: (`object` as? android.app.Fragment)?.requestPermissions(perms, requestCode)
    }

    @TargetApi(11)
    private fun getActivity(`object`: Any): Activity? {
        return `object` as? Activity ?: if (`object` is Fragment) {
            `object`.activity
        } else (`object` as? android.app.Fragment)?.activity
    }

    @TargetApi(11)
    private fun startAppSettings(`object`: Any,
                                 intent: Intent) {
        (`object` as? Activity)?.startActivityForResult(intent, DEFAULT_SETTINGS_REQ_CODE) ?: ((`object` as? Fragment)?.startActivityForResult(intent, DEFAULT_SETTINGS_REQ_CODE) ?: (`object` as? android.app.Fragment)?.startActivityForResult(intent, DEFAULT_SETTINGS_REQ_CODE))
    }


    private fun checkCallingObjectSuitability(`object`: Any) {
        // Make sure Object is an Activity or Fragment
        val isActivity = `object` is Activity
        val isSupportFragment = `object` is Fragment
        val isAppFragment = `object` is android.app.Fragment
        val isMinSdkM = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

        if (!(isSupportFragment || isActivity || isAppFragment && isMinSdkM)) {
            if (isAppFragment) {
                throw IllegalArgumentException(
                        "Target SDK needs to be greater than 23 if caller is android.app.Fragment")
            } else {
                throw IllegalArgumentException("Caller must be an Activity or a Fragment.")
            }
        }

        // Make sure Object implements callbacks
        if (`object` !is PermissionsResultCallback) {
            throw IllegalArgumentException("Caller must implement PermissionCallbacks.")
        }
    }
}