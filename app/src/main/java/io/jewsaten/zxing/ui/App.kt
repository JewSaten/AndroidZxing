package io.jewsaten.zxing.ui

import android.app.Application
import io.jewsaten.zxing.extensions.DelegatesExt

/**
 * Created by Administrator on 2018/3/15.
 */
class App : Application() {
    companion object {
        var instance: App by DelegatesExt.notNullSingleValue()
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}