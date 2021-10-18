package com.zestas.cryptmyfiles.dataItemModels

import com.michaelflisar.materialpreferences.core.SettingsModel
import com.michaelflisar.materialpreferences.datastore.DataStoreStorage

object ZenCryptSettingsModel : SettingsModel(DataStoreStorage(name = "zencrypt_settings")) {
    //-------
    val darkTheme by boolPref(false)
    val vibration by boolPref(true)
    val extension by stringPref(".zen")
    val fingerprint_auth by boolPref(false)
    val custom_pass_hash by stringPref("")
    val custom_pass_placeholder by stringPref("")
    val delete_original_unencrypted by boolPref(true)
    val isProUser by boolPref(false)
    val versionCode by intPref(-1)
    //-------
}