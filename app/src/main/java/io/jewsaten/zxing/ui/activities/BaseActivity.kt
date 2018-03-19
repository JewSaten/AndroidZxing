package io.jewsaten.zxing.ui.activities

import android.content.Context
import android.support.v7.app.AppCompatActivity
import io.jewsaten.zxing.R
import io.jewsaten.zxing.ui.utils.RuntimePermission

/**
 * Created by Administrator on 2018/3/16.
 */
open class BaseActivity : AppCompatActivity(), RuntimePermission.PermissionsResultCallback {

    private var callback: PermissionsCallback? = null

    fun checkPermission(requestCode: Int, rationale: String, perms: Array<String>, callback: PermissionsCallback) {
        this.callback = callback
        if (RuntimePermission.hasPermissions(this, perms)) {
            this.callback!!.onSuccess(this)
        } else {
            RuntimePermission.requestPermissions(this, rationale,
                    requestCode, perms)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {

    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        RuntimePermission.somePermissionPermanentlyDenied(this, getString(R.string.rationale_ask_again),
                R.string.setting, R.string.cancel, perms)
    }

    override fun onPermissionsPossessed() {
        this.callback!!.onSuccess(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        RuntimePermission.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    interface PermissionsCallback {
        fun onSuccess(context: Context)
    }
}