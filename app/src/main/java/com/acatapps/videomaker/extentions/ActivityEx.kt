package com.acatapps.videomaker.extentions

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.acatapps.videomaker.utils.Logger
import java.io.Serializable
fun Activity.openAppInStore() {
    try {
        val intent = packageManager.getLaunchIntentForPackage("com.android.vending")
        if(intent != null) {
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=$packageName");
            startActivity(intent)
        }
    }catch (e:java.lang.Exception) {
        Logger.e(e.toString())
    }
}

fun Activity.openDevPageInGooglePlay() {
    try {
        val intent = packageManager.getLaunchIntentForPackage("com.android.vending")
        if(intent != null) {
            intent.action = Intent.ACTION_VIEW;
            intent.data = Uri.parse("https://play.google.com/store/apps/developer?id=Ani+App+Studio");
            startActivity(intent);
        }
    }catch (e:java.lang.Exception) {
        Logger.e(e.toString())
    }
}