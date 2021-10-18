package com.zestas.cryptmyfiles.helpers

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.fragments.DecryptedViewFragment
import com.zestas.cryptmyfiles.fragments.EncryptedViewFragment
import java.io.File

class DeleteFileHelper {
    companion object {
        private lateinit var mActivity: AppCompatActivity

        fun init(activity: AppCompatActivity) {
            mActivity = activity
        }

        fun showFileDeleteConfirmDialog(file: File, replaceWith: Int) {
            val name = file.name
            val dialog = AlertDialog.Builder(mActivity)
            dialog.setTitle("Confirm deletion")
            dialog.setMessage("Are you sure you want to delete:\n$name")
            dialog.setNegativeButton("No") { _, _ -> }
            dialog.setPositiveButton("Yes") {_, _ ->
                if (file.exists() && file.canWrite()) {
                    file.delete()
                    FragmentHelper.replaceFragmentWithDelay(
                        if (replaceWith == ZenCryptConstants.REPLACE_WITH_ENCRYPTED) EncryptedViewFragment()
                        else DecryptedViewFragment(),0)
                    SnackBarHelper.showSnackBarCheck("Successfully deleted $name")
                }
                else {
                    SnackBarHelper.showSnackBarError("Something went wrong. Could not delete $name")
                }
            }
            dialog.show()
        }
    }
}