package io.jewsaten.zxing.ui.activities

import android.Manifest
import android.content.Context
import android.os.Bundle
import io.jewsaten.zxing.R
import io.jewsaten.zxing.extensions.navigate
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by Administrator on 2018/3/15.
 */
class MainActivity : BaseActivity() {

    companion object {
        private val RC_CAMERA_PERM = 123
        private val CAMERA = arrayOf(Manifest.permission.CAMERA)
    }

    private fun setupToolbar() {
        title = null
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Kotlin Sample"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        icon.setOnClickListener {
            checkPermission(RC_CAMERA_PERM, getString(R.string.rationale_camera), CAMERA, object : PermissionsCallback {
                override fun onSuccess(context: Context) {
                    navigate<ScannerActivity>(getString(R.string.title_scanner))
                }
            })
        }
    }
}