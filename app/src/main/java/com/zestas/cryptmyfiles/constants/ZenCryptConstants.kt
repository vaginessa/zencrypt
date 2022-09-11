package com.zestas.cryptmyfiles.constants

import android.app.Activity
import java.io.File

class ZenCryptConstants {
    companion object {
        private const val ENCRYPTED = "encrypted"
        private const val DECRYPTED = "decrypted"
        const val ACTION_CODE = "ACTION_CODE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val FILE = "FILE"
        const val FILE_PICK_MULTIPLE : Int = -3
        const val FILE_PICK : Int = -2
        const val FROM_CARD_VIEW : Int = -1
        const val ACTION_ENCRYPT : Int = 0
        const val ACTION_ENCRYPT_MULTIPLE = 1
        const val ACTION_DECRYPT : Int = 2

        const val REPLACE_WITH_ENCRYPTED: Int = 3
        const val REPLACE_WITH_DECRYPTED: Int = 4
        const val REPLACE_CODE = "REPLACE_CODE"


        fun encryptedFilesDir(activity: Activity): File {
            return activity.getExternalFilesDir(File.separator + ENCRYPTED)!!
        }

        fun decryptedFilesDir(activity: Activity): File {
            return activity.getExternalFilesDir(File.separator + DECRYPTED)!!
        }
    }
}