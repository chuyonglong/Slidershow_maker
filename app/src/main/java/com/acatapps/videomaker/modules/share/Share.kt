package com.acatapps.videomaker.modules.share

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import com.acatapps.videomaker.BuildConfig

import com.acatapps.videomaker.utils.Logger
import java.io.File

class Share {
    companion object {
        const val YOUTUBE_PACKAGE = "com.google.android.youtube"
        const val FACEBOOK_PACKAGE = "com.facebook.katana"
        const val INSTAGRAM_PACKAGE = "com.instagram.android"
        const val MESSENGER_PACKAGE = "com.facebook.orca"
        const val GMAIL_PACKAGE = "com.google.android.gm"
        const val WHATSAPP_PACKAGE = "com.whatsapp"
        const val LINE_PACKAGE = "jp.naver.line.android"
        const val TYPE_VIDEO = "video/*"
        const val BASE_URI = "content://"
        const val APP_ID = "com.app.videoedittor"
    }

    var shareType: String


    constructor() {
        this.shareType = TYPE_VIDEO
    }

    fun shareTo(context: Context, filePath: String, packageId: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageId)
        if (intent != null) {
            try {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    setPackage(packageId)
                    type = shareType

                }
                if(Build.VERSION.SDK_INT < 24) {
                    shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(filePath)))
                } else {
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider", File(filePath)))
                }
                context.startActivity(shareIntent)
            } catch (e: java.lang.Exception) {
                Logger.e(e.toString())
                Toast.makeText(context, "App not support", Toast.LENGTH_LONG).show()
            }

        } else {
            openStore(context, packageId)
        }
    }

    fun openMoreShare(context: Context, filePath: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = shareType

        }
        if(Build.VERSION.SDK_INT < 24) {
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(File(filePath)))
        } else {
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID+".fileprovider", File(filePath)))
        }
        context.startActivity(shareIntent)
    }

    fun shareApp(context: Context, appId: String) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
        }

        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "https://play.google.com/store/apps/details?id=$appId"
        )

        context.startActivity(shareIntent)
    }

    fun openStore(context: Context, packageId: String) {
        try {
            val intent = Intent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                data = Uri.parse("market://details?id=$packageId")
            }
            context.startActivity(intent)
        }catch (e:java.lang.Exception) {
            Logger.e(e.toString())
        }
    }
}