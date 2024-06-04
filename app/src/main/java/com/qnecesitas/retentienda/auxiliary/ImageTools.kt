package com.qnecesitas.retentienda.auxiliary

import android.content.Context
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ImageTools {

    companion object {
        const val WIDTH_OF_PHOTO_TO_UPLOAD = 900
        const val HEIGHT_OF_PHOTO_TO_UPLOAD = 900

        fun getActualHour(FormatSum: String): String {
            return SimpleDateFormat(
                FormatSum,
                Locale.getDefault()
            ).format(Date())
        }

        @Throws(IOException::class)
        fun createTempImageFile(context: Context , name: String): File {
            val storageDir = context.filesDir
            return File.createTempFile(name, ".png", storageDir)
        }


    }
}