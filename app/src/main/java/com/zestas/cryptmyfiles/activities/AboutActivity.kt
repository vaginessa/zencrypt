package com.zestas.cryptmyfiles.activities

import android.os.Bundle
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsActivity
import com.zestas.cryptmyfiles.R

class AboutActivity : LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent = LibsBuilder()
            .withVersionShown(true)
            .withLicenseShown(true)
            .withAboutIconShown(true)
            .withAboutVersionShown(true)
            .withAboutVersionShownCode(true)
            .withAboutAppName("ZenCrypt - Securely Encrypt")
            .withAboutDescription(getString(R.string.zencrypt_description))
            .withAboutSpecial1("changelog")
            .withAboutSpecial1Description(getString(R.string.zencrypt_changelog))
            .withSearchEnabled(true)
            .withActivityTitle("About")
            // find ids via './gradlew findLibraries'
            .intent(this)
        super.onCreate(savedInstanceState)
    }
}