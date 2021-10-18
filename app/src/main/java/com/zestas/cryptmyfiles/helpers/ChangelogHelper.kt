package com.zestas.cryptmyfiles.helpers

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.zestas.cryptmyfiles.BuildConfig
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import kotlinx.coroutines.launch

class ChangelogHelper {
    companion object {
        fun showChangelogOnUpdate(activity: AppCompatActivity) {
            if (ZenCryptSettingsModel.versionCode.value < BuildConfig.VERSION_CODE) {
                val builder = AlertDialog.Builder(activity)
                builder.setTitle("Changelog")
                builder.setCancelable(false)
                builder.setMessage(
                    HtmlCompat.fromHtml(
                        activity.getString(R.string.zencrypt_changelog),
                        HtmlCompat.FROM_HTML_MODE_LEGACY
                    )
                )
                builder.setPositiveButton("Got it") { _, _ ->
                    activity.lifecycleScope.launch {
                        ZenCryptSettingsModel.versionCode.update(BuildConfig.VERSION_CODE)
                    }
                }

                val dialog = builder.create()
                dialog.show()
                dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}