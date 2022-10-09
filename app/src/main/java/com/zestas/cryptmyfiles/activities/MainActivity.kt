package com.zestas.cryptmyfiles.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.appbar.MaterialToolbar
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.fragments.EncryptedViewFragment
import com.zestas.cryptmyfiles.fragments.PasswordAnalyzerFragment
import com.zestas.cryptmyfiles.fragments.SettingsFragment
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import com.zestas.cryptmyfiles.fragments.DecryptedViewFragment
import com.zestas.cryptmyfiles.helpers.*
import com.zestas.cryptmyfiles.helpers.FragmentHelper.Companion.replaceFragmentWithDelay
import com.zestas.cryptmyfiles.helpers.ui.FabHelper
import com.zestas.cryptmyfiles.helpers.ui.SnackBarHelper
import com.zestas.cryptmyfiles.helpers.ui.ToolbarHelper
import dev.skomlach.biometric.compat.BiometricPromptCompat
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private val bottomNavBar by lazy { findViewById<ChipNavigationBar>(R.id.bottom_menu) }
    private val fabMenu by lazy { findViewById<FloatingActionMenu>(R.id.fab_menu) }
    private val toolbar by lazy { findViewById<MaterialToolbar>(R.id.toolbar) }
    private lateinit var searchMenuItem: MenuItem

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(if (ZenCryptSettingsModel.darkTheme.value) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        BiometricPromptCompat.init(null)

        bottomNavBar.setItemSelected(R.id.encrypted)

        init()

        if(savedInstanceState == null)
            replaceFragmentWithDelay(EncryptedViewFragment(), 0)
        else if(themeChanged) {
            fabMenu.hideMenuButton(false)
            searchMenuItem.isVisible = false
        }
    }


    private fun init() {
        ToolbarHelper.makeTitle(this, toolbar, getString(R.string.app_name))
        searchMenuItem = toolbar.menu.findItem(R.id.search_files)
        FabHelper.init(this)
        SnackBarHelper.init(this)
        FragmentHelper.init(this)
        FileActionsHelper.init(this)

        lifecycleScope.launch {
            whenStarted {
                PermissionHelper.checkPermissions(this@MainActivity)
            }
            ChangelogHelper.showChangelogOnUpdate(this@MainActivity)
        }

        bottomNavBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.encrypted -> {
                    replaceFragmentWithDelay(EncryptedViewFragment())
                    fabMenu.showMenuButton(true)
                    fabMenu.close(true)
                    searchMenuItem.isVisible = true
                }
                R.id.decrypted -> {
                    replaceFragmentWithDelay(DecryptedViewFragment())
                    fabMenu.showMenuButton(true)
                    fabMenu.close(true)
                    searchMenuItem.isVisible = true
                }
                R.id.pass_analysis -> {
                    replaceFragmentWithDelay(PasswordAnalyzerFragment())
                    fabMenu.hideMenuButton(true)
                    searchMenuItem.isVisible = false
                }
                R.id.settings -> {
                    replaceFragmentWithDelay(SettingsFragment())
                    fabMenu.hideMenuButton(true)
                    searchMenuItem.isVisible = false
                }
                //else -> R.color.white to ""
            }
        }

        //api 33 implementation of onBackPressed which is now deprecated!
        onBackPressedDispatcher.addCallback(this /* lifecycle owner */, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (searchMenuItem.isActionViewExpanded)
                    searchMenuItem.collapseActionView()
                else if (fabMenu.isOpened)
                    fabMenu.close(true)
                else if (bottomNavBar.getSelectedItemId() != R.id.encrypted) {
//                      replaceFragmentWithDelay(EncryptedViewFragment())
                    bottomNavBar.setItemSelected(R.id.encrypted)
                }
                else {
                    exit()
                }
            }
        })
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