package io.jewsaten.zxing.ui.activities

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.google.zxing.Result
import com.google.zxing.client.result.ParsedResult
import com.mylhyl.zxing.scanner.OnScannerCompletionListener
import io.jewsaten.zxing.R
import io.jewsaten.zxing.extensions.getExtraData
import io.jewsaten.zxing.ui.App
import kotlinx.android.synthetic.main.activity_scanner.*
import org.jetbrains.anko.find
import org.jetbrains.anko.toast

/**
 * Created by Administrator on 2018/3/19.
 */
class ScannerActivity : BaseActivity(), ToolbarManager, OnScannerCompletionListener {

    override val toolbar by lazy { find<Toolbar>(R.id.toolbar) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        initViews()
    }

    override fun onResume() {
        super.onResume()
        scannerView.onResume()
    }

    override fun onPause() {
        scannerView.onPause()
        super.onPause()
    }

    private fun initViews() {
        toolbarTitle = getExtraData()
        enableHomeAsUp { onBackPressed() }
        scannerView.setLaserLineResId(R.mipmap.ic_scan_line)
        scannerView.setMediaResId(R.raw.beep)
        scannerView.setOnScannerCompletionListener(this)
        btnTorch.setOnCheckedChangeListener({ buttonView, isChecked -> scannerView.toggleLight(isChecked) })
    }

    override fun OnScannerCompletion(rawResult: Result?, parsedResult: ParsedResult?, barcode: Bitmap?) {
        rawResult?.let { App.instance.toast(it.text) }
        finish()
    }

}