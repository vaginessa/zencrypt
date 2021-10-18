package com.zestas.cryptmyfiles.constants

import android.app.Activity
import java.io.File

class ZenCryptConstants {
    companion object {
        private val ENCRYPTED = "encrypted"
        private val DECRYPTED = "decrypted"
        val ACTION_CODE = "ACTION_CODE"
        val REQUEST_CODE = "REQUEST_CODE"
        val FILE = "FILE"
        val FILE_PICK : Int = -2
        val FROM_CARD_VIEW : Int = -1
        val ACTION_ENCRYPT : Int = 0
        val ACTION_DECRYPT : Int = 1

        val REPLACE_WITH_ENCRYPTED: Int = 2
        val REPLACE_WITH_DECRYPTED: Int = 3
        val REPLACE_CODE = "REPLACE_CODE"


        fun encryptedFilesDir(activity: Activity): File {
            return activity.getExternalFilesDir(File.separator + ENCRYPTED)!!
        }

        fun decryptedFilesDir(activity: Activity): File {
            return activity.getExternalFilesDir(File.separator + DECRYPTED)!!
        }
    }
}