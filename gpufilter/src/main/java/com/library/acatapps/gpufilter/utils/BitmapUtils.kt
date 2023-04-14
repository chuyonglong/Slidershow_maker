package com.library.acatapps.gpufilter.utils

import android.content.Context
import android.graphics.*
import java.io.File
import kotlin.math.max
import kotlin.math.min

object BitmapUtils {

    fun resizeBitmap(bitmapInput: Bitmap, size:Float): Bitmap {
        if(bitmapInput.width < size || bitmapInput.height < size) return bitmapInput
        val scale = min((size/bitmapInput.width), (size/bitmapInput.height))
        return Bitmap.createScaledBitmap(bitmapInput, (bitmapInput.width*scale).toInt(), (bitmapInput.height*scale).toInt(), true)
    }

    fun getBitmapFromAsset(context: Context ,path: String): Bitmap {
        val inputStream = context.assets.open(path)
        return BitmapFactory.decodeStream(inputStream)
    }

    fun getStickerFromFilePath(filePath:String): Bitmap {
        val file = File(filePath)
        return BitmapFactory.decodeFile(file.absolutePath)

    }

    fun getMaxLineWidth(text: String, paint: Paint): Int {
        var maxWidth = 0
        var line = ""
        var lineCount = 0
        for (i in text) {
            if (i == '\n') {
                val w = getTextWidth(line, paint)
                maxWidth = max(maxWidth, w)
                line = ""
                lineCount += 1
                continue
            } else {
                line = "$line$i"
            }
        }
        if (lineCount == 0) {
            return getTextWidth(text, paint)
        }
        return maxWidth
    }

    private fun getTextWidth(text: String, paint: Paint): Int {
        val rect = Rect()
        paint.getTextBounds(text, 0, text.length, rect)
        return rect.width()
    }

}