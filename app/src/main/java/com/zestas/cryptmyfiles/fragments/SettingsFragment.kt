package com.zestas.cryptmyfiles.fragments

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.text.InputType
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.michaelflisar.materialpreferences.core.interfaces.StorageSetting
import com.michaelflisar.materialpreferences.preferencescreen.*
import com.michaelflisar.materialpreferences.preferencescreen.classes.Badge
import com.michaelflisar.materialpreferences.preferencescreen.classes.asBatch
import com.michaelflisar.materialpreferences.preferencescreen.classes.asIcon
import com.michaelflisar.materialpreferences.preferencescreen.dependencies.Dependency
import com.michaelflisar.materialpreferences.preferencescreen.dependencies.asDependency
import com.michaelflisar.materialpreferences.preferencescreen.enums.NoIconVisibility
import com.michaelflisar.materialpreferences.preferencescreen.input.input
import com.michaelflisar.text.asText
import com.zestas.cryptmyfiles.activities.MainActivity
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.activities.AboutActivity
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.databinding.FragmentSettingsBinding
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import com.zestas.cryptmyfiles.helpers.IapHelper
import com.zestas.cryptmyfiles.helpers.SnackBarHelper
import dev.skomlach.biometric.compat.*
import games.moisoni.google_iab.BillingConnector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.security.MessageDigest


class SettingsFragment : Fragment(R.layout.fragment_settings) {
    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private lateinit var preferenceScreen: PreferenceScreen
    private lateinit var billingConnector: BillingConnector

    private val regexWithDot: Regex = "^\\.(([a-z]|[A-Z])+)\$".toRegex()
    private val regexWithoutDot: Regex = "^(([a-z]|[A-Z])+)\$".toRegex()

/*    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = FragmentSettingsBinding.inflate(layoutInflater)
        preferenceScreen = initSettings(savedInstanceState)
    }*/

/*    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        *//*val v = inflater.inflate(R.layout.fragment_settings, container, false)*//*
*//*        val v = inflater.inflate(R.layout.activity_main, container, false)
        test = v.findViewById(R.id.fab_menu)*//*

        return binding.root
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingConnector = IapHelper.initBillingConnector(activity as AppCompatActivity)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceScreen = initSettings(savedInstanceState)
    }

    private fun initSettings(savedInstanceState: Bundle?): PreferenceScreen {

        // global settings to avoid
        // INFO:
        // some global settings can be overwritten per preference (e.g. bottomSheet yes/no)
        // other global settings can only be changed globally

        // following is optional!
        PreferenceScreenConfig.apply {
            bottomSheet = false                             // default: false
            maxLinesTitle = 1                               // default: 1
            maxLinesSummary = 3                             // default: 3
            noIconVisibility = NoIconVisibility.Invisible   // default: Invisible
            alignIconsWithBackArrow = false                 // default: false
        }

        // -----------------
        // 1) create screen(s)
        // -----------------

        val screen = screen {

            // set up screen
            state = savedInstanceState
            onScreenChanged = { subScreenStack, _ ->
                val breadcrumbs = subScreenStack.joinToString(" > ") { it.title.get(requireActivity()) }
            }

            // set up settings (and sub settings)

            // -----------------
            // 1) test app settings (root level)
            // -----------------

            category {
                title = getString(R.string.support_development).asText()
            }

            if ( ZenCryptSettingsModel.isProUser.value ) {
                button {
                    title = "ZenCrypt Pro".asText()
                    summary = getString(R.string.zencrypt_pro_is_already_purchased).asText()
                    icon = R.drawable.cart_check.asIcon()
                    badge = Badge.Text(getString(R.string.pro_unlocked).asText(), ContextCompat.getColor(requireActivity(), R.color.green_active))
                    onClick = {
                        SnackBarHelper.showSnackBarLove(getString(R.string.you_have_already_purchased))
                    }
                }
            }
            else {
                button {
                    title = getString(R.string.go_pro).asText()
                    summary = getString(R.string.i_am_solo_developing).asText()
                    icon = R.drawable.ic_baseline_attach_money_24.asIcon()
                    badge = Badge.Text("Pro not Unlocked".asText(), Color.RED)
                    onClick = {
                        when {
                            billingConnector.isReady -> billingConnector.purchase(requireActivity(),"zencrypt_pro")
                            else -> SnackBarHelper.showSnackBarError(getString(R.string.google_play_billing_error))
                        }
                    }
                }
            }



            category {
                title = "App Style".asText()
            }

            switch(ZenCryptSettingsModel.darkTheme) {
                title = getString(R.string.dark_theme).asText()
                icon = R.drawable.ic_baseline_style_24.asIcon()
                summary = getString(R.string.choose_between_light_and_dark).asText()
                badge = "PRO".asBatch()
                canChange = {
                    if ( !ZenCryptSettingsModel.isProUser.value )
                        SnackBarHelper.showSnackBarError(getString(R.string.zencrypt_pro_is_required))
                    ZenCryptSettingsModel.isProUser.value
                }
                onChanged = {
                    println("Dark Theme Settings Listener called: $it")
                    //recreate()
                    MainActivity.themeChanged()
                    AppCompatDelegate.setDefaultNightMode(if (it) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            category {
                title = getString(R.string.configuration).asText()
            }

            switch(ZenCryptSettingsModel.vibration) {
                title = getString(R.string.enable_vibration).asText()
                icon = R.drawable.ic_baseline_vibrate_24.asIcon()
                summary = getString(R.string.vibrate_after_every_action).asText()
            }

            switch(ZenCryptSettingsModel.delete_original_unencrypted) {
                title = getString(R.string.delete_original_unencrypted).asText()
                icon = R.drawable.trash_can_outline.asIcon()
                summary = getString(R.string.delete_original_file_after_encryption).asText()
            }

            input(ZenCryptSettingsModel.extension) {
                title = getString(R.string.encrypted_file_extension).asText()
                icon = R.drawable.ic_baseline_text_fields_24.asIcon()
                summary = "Insert the preferable output file extension (Example: file.zen).\nCurrent: \"%s\"".asText()
                hint = getString(R.string.file_extension).asText()
                onChanged = {
                    when {
                        it.matches(regexWithDot) -> SnackBarHelper.showSnackBarCheck("File extension changed to \"$it\"")
                        it.matches(regexWithoutDot) -> {
                            lifecycleScope.launch(Dispatchers.IO)  {
                                ZenCryptSettingsModel.extension.update(".$it")
                            }
                            SnackBarHelper.showSnackBarCheck("File extension changed to \".$it\"")
                        }
                        else -> {
                            SnackBarHelper.showSnackBarInfo(getString(R.string.are_you_sure_you_typed_the_correct_file_extension))
                        }
                    }
                }
            }

            category {
                title = getString(R.string.fingerprint_auth).asText()
            }

            switch(ZenCryptSettingsModel.fingerprint_auth) {
                title = getString(R.string.enable_fingerprint_auth).asText()
                icon = R.drawable.ic_baseline_fingerprint_24.asIcon()
                summary = getString(R.string.enabling_fingerprint_auth).asText()
                badge = "PRO".asBatch()
                canChange = {
                    if ( !ZenCryptSettingsModel.isProUser.value )
                        SnackBarHelper.showSnackBarError(getString(R.string.zencrypt_pro_is_required))
                    ZenCryptSettingsModel.isProUser.value
                }
                onChanged = {
                    if (it) SnackBarHelper.showSnackBarInfo(getString(R.string.set_a_custom_password_below))
                }
                dependsOn = object : Dependency<Boolean> {
                    override suspend fun isEnabled(): Boolean {
                        return BiometricManagerCompat.isHardwareDetected(BiometricAuthRequest(
                            BiometricApi.AUTO, BiometricType.BIOMETRIC_FINGERPRINT))
                    }

                    override val setting: StorageSetting<Boolean>
                        get() = ZenCryptSettingsModel.fingerprint_auth
                }
            }

            input(ZenCryptSettingsModel.custom_pass_placeholder) {
                title = getString(R.string.set_custom_password).asText()
                icon = R.drawable.ic_baseline_textbox_password.asIcon()
                summary = getString(R.string.any_password_is_valid).asText()
                hint = getString(R.string.enter_a_password).asText()
                dependsOn = ZenCryptSettingsModel.fingerprint_auth.asDependency()
                onChanged = {
                    lifecycleScope.launch(Dispatchers.IO)  {
                        //check if password is empty
                        if (it.trim() == "") {
                            ZenCryptSettingsModel.fingerprint_auth.update(false)
                            SnackBarHelper.showSnackBarError(getString(R.string.empty_password))
                        }
                        else
                            SnackBarHelper.showSnackBarCheck(getString(R.string.password_set))
                        //update the real password with user's input hash
                        ZenCryptSettingsModel.custom_pass_hash.update(it.sha256())
                        //then set the placeholder text view to empty again
                        ZenCryptSettingsModel.custom_pass_placeholder.update("")
                    }
                }
                textInputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            }

            category {
                title = getString(R.string.about_zencrypt).asText()
            }

            button {
                title = getString(R.string.rate_zencrypt).asText()
                summary = getString(R.string.if_you_like_the_app).asText()
                icon = R.drawable.star_outline.asIcon()
                onClick = {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.zestas.cryptmyfiles")))
                    } catch (e1: ActivityNotFoundException) {
                        try {
                            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.zestas.cryptmyfiles")))
                        } catch (e2: ActivityNotFoundException) {
                            SnackBarHelper.showSnackBarError("Cannot open link.")
                        }
                    }
                }
            }

            button {
                title = getString(R.string.contact_me).asText()
                summary = getString(R.string.send_me_an_email).asText()
                icon = R.drawable.mail_at.asIcon()
                onClick = {
                    val selectorIntent = Intent(Intent.ACTION_SENDTO)
                    selectorIntent.data = Uri.parse("mailto:")

                    val emailIntent = Intent(Intent.ACTION_SEND)
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("zestas.zencrypt@mail.com"))
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "ZenCrypt Feedback")
                    emailIntent.selector = selectorIntent

                    startActivity(Intent.createChooser(emailIntent, "ZenCrypt Feedback"))
                }
            }

            button {
                title = getString(R.string.source_code).asText()
                summary = getString(R.string.zencrypt_is_now_open_source).asText()
                icon = R.drawable.gitlab.asIcon()
                onClick = {
                    try {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://gitlab.com/Kelsios/zencrypt")))
                    } catch (e: ActivityNotFoundException) {
                        SnackBarHelper.showSnackBarError("Cannot open link.")
                    }
                }
            }

            button {
                title = getString(R.string.about).asText()
                summary = getString(R.string.zencrypt_changelog_and_libraries).asText()
                icon = R.drawable.info_outline.asIcon()
                onClick = {
                    val intent = Intent(requireActivity(), AboutActivity::class.java)
                    startActivity(intent)
                }
            }

        }
        screen.bind(binding.rvSettings)
        return screen
    }

    private fun String.sha256(): String {
        return MessageDigest
            .getInstance("SHA-256")
            .digest(this.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }
}