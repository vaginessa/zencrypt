package com.zestas.cryptmyfiles.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.github.clans.fab.FloatingActionMenu
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.fragments.EncryptedViewFragment
import com.zestas.cryptmyfiles.fragments.PasswordAnalyzerFragment
import com.zestas.cryptmyfiles.fragments.SettingsFragment
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import com.zestas.cryptmyfiles.fragments.DecryptedViewFragment
import com.zestas.cryptmyfiles.helpers.*
import com.zestas.cryptmyfiles.helpers.FragmentHelper.Companion.replaceFragmentWithDelay
import dev.skomlach.biometric.compat.BiometricPromptCompat
import kotlinx.coroutines.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val menu by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }
    private val fabMenu by lazy { findViewById<FloatingActionMenu>(R.id.fab_menu) }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(if (ZenCryptSettingsModel.darkTheme.value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BiometricPromptCompat.init(null)

        menu.setItemSelected(R.id.encrypted)

        init()

        if(savedInstanceState == null)
            replaceFragmentWithDelay(EncryptedViewFragment(), 0)
        else if(themeChanged)
            fabMenu.hideMenuButton(false)
    }


    private fun init() {
        FabHelper.init(this)
        SnackBarHelper.init(this)
        FragmentHelper.init(this)
        DeleteFileHelper.init(this)

        lifecycleScope.launch {
            whenStarted {
                PermissionHelper.checkPermissions(this@MainActivity)
            }
            ChangelogHelper.showChangelogOnUpdate(this@MainActivity)
        }

        menu.setOnItemSelectedListener { id ->
            when (id) {
                R.id.encrypted -> {
                    replaceFragmentWithDelay(EncryptedViewFragment())
                    fabMenu.showMenuButton(true)
                    fabMenu.close(true)
                }
                R.id.decrypted -> {
                    replaceFragmentWithDelay(DecryptedViewFragment())
                    fabMenu.showMenuButton(true)
                    fabMenu.close(true)
                }
                R.id.pass_analysis -> {
                    replaceFragmentWithDelay(PasswordAnalyzerFragment())
                    fabMenu.hideMenuButton(true)
                }
                R.id.settings -> {
                    replaceFragmentWithDelay(SettingsFragment())
                    fabMenu.hideMenuButton(true)
                }
                //else -> R.color.white to ""
            }
        }
    }

    override fun onBackPressed() {
        if (menu.getSelectedItemId() != R.id.encrypted) {
            replaceFragmentWithDelay(EncryptedViewFragment())
            menu.setItemSelected(R.id.encrypted)
        }
        else {
            exit()
        }
    }

    private fun exit() {
        val _intentOBJ = Intent(Intent.ACTION_MAIN)
        _intentOBJ.addCategory(Intent.CATEGORY_HOME)
        _intentOBJ.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        _intentOBJ.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(_intentOBJ)
    }

    companion object {
        private var themeChanged: Boolean = false
        fun themeChanged() {
            themeChanged = true
        }
    }
}