package com.zestas.cryptmyfiles.activities

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.mikepenz.aboutlibraries.ui.LibsActivity
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.fragments.DecryptedViewFragment
import com.zestas.cryptmyfiles.fragments.EncryptedViewFragment
import com.zestas.cryptmyfiles.helpers.DeleteFileHelper
import com.zestas.cryptmyfiles.helpers.FragmentHelper
import com.zestas.cryptmyfiles.helpers.SnackBarHelper

class AboutActivity : LibsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        intent = LibsBuilder().withFields(R.string::class.java.fields)
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
            .withLibraryModification(
                "androidx_activity__activity",
                Libs.LibraryFields.LIBRARY_NAME,
                "Activity Support"
            )
            .intent(this)
        super.onCreate(savedInstanceState)
    }
}