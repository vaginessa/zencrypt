package com.zestas.cryptmyfiles.helpers

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.araujo.jordan.excuseme.ExcuseMe
import kotlin.system.exitProcess

class PermissionHelper {
    companion object {
        suspend fun checkPermissions(activity: AppCompatActivity) {
            if (!ExcuseMe.doWeHavePermissionFor(
                    activity,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                ExcuseMe.couldYouGive(activity).please { _, result ->
                    val dialog = AlertDialog.Builder(activity)
                    dialog.setTitle("Permission Request")
                    dialog.setMessage("ZenCrypt does not have any intrusive permissions. " +
                            "To function properly, it only needs permission to read/write your storage. " +
                            "Even with this permissions granted, ZenCrypt cannot mess with your files due to android's scoped storage.")
                    dialog.setNegativeButton("Not now") { _, _ ->
                        activity.moveTaskToBack(true)
                        activity.finishAffinity()
                        exitProcess(1)
                    }
                    dialog.setPositiveButton("Continue") { _, _ -> result(true) }
                    dialog.setOnCancelListener {
                        activity.moveTaskToBack(true)
                        activity.finishAffinity()
                        exitProcess(1)
                    } //important
                    dialog.show()
                }.permissionFor(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}