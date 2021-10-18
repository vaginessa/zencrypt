package com.zestas.cryptmyfiles.helpers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.activities.ActionActivity
import com.zestas.cryptmyfiles.constants.ZenCryptConstants

class FabHelper {
    companion object {
        private lateinit var fabMenu: FloatingActionMenu
        private lateinit var fabActionEncrypt: FloatingActionButton
        private lateinit var fabActionDecrypt: FloatingActionButton

        fun init(activity: AppCompatActivity) {
            fabMenu = activity.findViewById(R.id.fab_menu)
            fabActionEncrypt = activity.findViewById(R.id.fab_action_encrypt)
            fabActionDecrypt = activity.findViewById(R.id.fab_action_decrypt)
            initOnClickListeners(activity)
        }

        private fun initOnClickListeners(activity: AppCompatActivity) {
            fabActionEncrypt.setOnClickListener {
                val bottomMenuSelectedId = activity.findViewById<ChipNavigationBar>(R.id.bottom_menu).getSelectedItemId()
                val intent = Intent(activity, ActionActivity::class.java)
                intent.putExtra(ZenCryptConstants.REQUEST_CODE, ZenCryptConstants.FILE_PICK)
                intent.putExtra(ZenCryptConstants.ACTION_CODE, ZenCryptConstants.ACTION_ENCRYPT)
                intent.putExtra(ZenCryptConstants.REPLACE_CODE, if (bottomMenuSelectedId == R.id.encrypted)
                    ZenCryptConstants.REPLACE_WITH_ENCRYPTED else ZenCryptConstants.REPLACE_WITH_DECRYPTED)
                activity.startActivity(intent)
            }

            fabActionDecrypt.setOnClickListener {
                val bottomMenuSelectedId = activity.findViewById<ChipNavigationBar>(R.id.bottom_menu).getSelectedItemId()
                val intent = Intent(activity, ActionActivity::class.java)
                intent.putExtra(ZenCryptConstants.REQUEST_CODE, ZenCryptConstants.FILE_PICK)
                intent.putExtra(ZenCryptConstants.ACTION_CODE, ZenCryptConstants.ACTION_DECRYPT)
                intent.putExtra(ZenCryptConstants.REPLACE_CODE, if (bottomMenuSelectedId == R.id.encrypted)
                    ZenCryptConstants.REPLACE_WITH_ENCRYPTED else ZenCryptConstants.REPLACE_WITH_DECRYPTED)
                activity.startActivity(intent)
            }
        }

    }
}