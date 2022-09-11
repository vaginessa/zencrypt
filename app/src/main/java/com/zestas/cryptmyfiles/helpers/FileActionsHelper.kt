package com.zestas.cryptmyfiles.helpers

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.fragments.DecryptedViewFragment
import com.zestas.cryptmyfiles.fragments.EncryptedViewFragment
import java.io.File


class FileActionsHelper {
    companion object {
        private lateinit var mActivity: AppCompatActivity

        fun init(activity: AppCompatActivity) {
            mActivity = activity
        }

        fun showFileDeleteConfirmDialog(file: File, replaceWith: Int) {
            val name = file.name
            val title = mActivity.getString(R.string.confirm_deletion)
            val dialog = AlertDialog.Builder(mActivity)
            dialog.setTitle(title)
            dialog.setMessage("Are you sure you want to delete:\n$name")
            dialog.setNegativeButton("No") { dia, _ -> dia.cancel() }
            dialog.setPositiveButton("Yes") { dia, _ ->
                if (file.exists() && file.canWrite()) {
                    file.delete()
                    FragmentHelper.replaceFragmentWithDelay(
                        if (replaceWith == ZenCryptConstants.REPLACE_WITH_ENCRYPTED) EncryptedViewFragment()
                        else DecryptedViewFragment(), 0
                    )
                    SnackBarHelper.showSnackBarCheck("Successfully deleted $name")
                } else {
                    SnackBarHelper.showSnackBarError("Something went wrong. Could not delete $name")
                }
                dia.cancel()
            }
            dialog.show()
        }

        fun showFileRenameDialog(file: File, replaceWith: Int) {
            val oldName = file.name
            val textInputLayout =
                TextInputLayout(
                    ContextThemeWrapper(
                        mActivity,
                        R.style.Widget_Material3_TextInputLayout_OutlinedBox
                    )
                )
            textInputLayout.setPadding(
                mActivity.resources.getDimensionPixelOffset(R.dimen.dp_19),
                mActivity.resources.getDimensionPixelOffset(R.dimen.dp_19),
                mActivity.resources.getDimensionPixelOffset(R.dimen.dp_19),
                0
            )
            textInputLayout.startIconDrawable =
                ContextCompat.getDrawable(mActivity, R.drawable.pencil)
            textInputLayout.setStartIconTintList(
                ContextCompat.getColorStateList(
                    textInputLayout.context,
                    R.color.text_input_color_selector
                )
            )
            textInputLayout.boxBackgroundColor =
                MaterialColors.getColor(textInputLayout, R.attr.navMenuColor)
            textInputLayout.boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            textInputLayout.setBoxCornerRadii(
                14F,
                14F,
                14F,
                14F
            )
            val input = TextInputEditText(textInputLayout.context)
            input.textSize = 16F
            textInputLayout.hint = oldName
            textInputLayout.addView(input)
            val renameString = mActivity.getString(R.string.rename)
            val message = mActivity.getString(R.string.please_enter_file_name_without_extension)

            val alert = AlertDialog.Builder(mActivity)
                .setTitle(renameString)
                .setView(textInputLayout)
                .setMessage(message)
                .setPositiveButton(renameString) { dialog, _ ->
                    val newFile: File
                    val path =
                        if (replaceWith == ZenCryptConstants.REPLACE_WITH_ENCRYPTED)
                            ZenCryptConstants.encryptedFilesDir(mActivity).absolutePath
                        else ZenCryptConstants.decryptedFilesDir(mActivity).absolutePath
                    if (oldName.matches(".*\\..*\\..*".toRegex())) {
                        val ix1 = oldName.lastIndexOf('.')
                        val ix2 = oldName.lastIndexOf('.', ix1 - 1)
                        val dest = path +
                                File.separator +
                                input.text +
                                oldName.substring(ix2, ix1) +
                                oldName.substring(ix1, oldName.length)
                        newFile = File(dest)
                    } else {
                        val ix1 = oldName.lastIndexOf('.')
                        val dest = path +
                                File.separator +
                                input.text +
                                oldName.substring(ix1, oldName.length)
                        newFile = File(dest)
                    }
                    if (newFile.exists())
                        SnackBarHelper.showSnackBarError("File already exists!")
                    else {
                        val success = file.renameTo(newFile)
                        if (success)
                            SnackBarHelper.showSnackBarCheck("Successfully renamed file!")
                    }
                    FragmentHelper.replaceFragmentWithDelay(
                        if (replaceWith == ZenCryptConstants.REPLACE_WITH_ENCRYPTED) EncryptedViewFragment()
                        else DecryptedViewFragment(), 0
                    )
                    dialog.cancel()
                }
                .setNeutralButton(mActivity.getString(android.R.string.cancel)) { dialog, _ ->
                    dialog.cancel()
                }.create()

            alert.show()
        }
    }
}