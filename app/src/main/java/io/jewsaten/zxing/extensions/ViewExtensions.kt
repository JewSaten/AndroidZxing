package io.jewsaten.zxing.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.view.View

/**
 * Created by Administrator on 2018/3/16.
 */
val View.ctx: Context
    get() = context

inline fun <reified T : Activity> Activity.navigate(extraData: String, sharedView: View? = null,
                                                    transitionName: String? = null) {
    val intent = Intent(this, T::class.java)
    intent.putExtra("extraData", extraData)

    var options: ActivityOptionsCompat? = null

    if (sharedView != null && transitionName != null) {
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, sharedView, transitionName)
    }

    ActivityCompat.startActivity(this, intent, options?.toBundle())
}

fun Activity.getExtraData(): String {
    val intent = intent
    return intent.getStringExtra("extraData")
}
