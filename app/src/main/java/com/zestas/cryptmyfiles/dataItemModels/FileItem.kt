package com.zestas.cryptmyfiles.dataItemModels

import android.text.format.DateFormat
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileItem(file: File) {
    private var mFile = file
    private var mDate: Date = Date(mFile.lastModified())

    companion object {
        fun create(file: File): FileItem {
            return FileItem(file)
        }
    }

    fun getFile(): File {
        return mFile
    }

    fun getName(): String {
        return mFile.name
    }

    fun getNameWithoutExtension(): String {
        return mFile.nameWithoutExtension
    }

    fun getPath(): String {
        return mFile.absolutePath
    }

    fun getDateTime(): String {
        val datePattern: String = DateFormat.getBestDateTimePattern(Locale.getDefault(), "ddMMyyyy hh:mm")
        val sdf = SimpleDateFormat(datePattern, Locale.getDefault())
        return sdf.format(mDate)
    }

    fun getSize(decimals: Int = 2): String {
        if (mFile.length() == 0L)
            return "0B"

        val kb: Double = mFile.length().toDouble() / 1024
        return if (kb < 1024)
            "%.${decimals}f KiB".format(kb)
        else {
            val mb: Double = kb / 1024
            if (mb < 1024) {
                "%.${decimals}f MiB".format(mb)
            }
            else {
                val gb: Double = mb / 1024
                "%.${decimals}f GiB".format(gb)
            }
        }
    }
}