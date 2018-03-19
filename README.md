# AndroidZxing
Kotlin 二维码/条形码

![截图](/img/image.gif "截图")

## 代码片段

```
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
        App.instance.toast(rawResult!!.text)
        finish()
    }
    
 ```

## 联系

- **Gmail：** satenjew@gmail.com
- **个人博客：** [https://jewsaten.github.io/](https://jewsaten.github.io/)
