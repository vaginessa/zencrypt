package com.zestas.cryptmyfiles.activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.OpenableColumns
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.whenStarted
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.michaelflisar.materialpreferences.core.initialisation.SettingSetup.context
import com.pvryan.easycrypt.ECResultListener
import com.pvryan.easycrypt.symmetric.ECSymmetric
import com.zestas.cryptmyfiles.R
import com.zestas.cryptmyfiles.constants.ZenCryptConstants
import com.zestas.cryptmyfiles.dataItemModels.ZenCryptSettingsModel
import com.zestas.cryptmyfiles.fragments.DecryptedViewFragment
import com.zestas.cryptmyfiles.fragments.EncryptedViewFragment
import com.zestas.cryptmyfiles.helpers.FragmentHelper
import com.zestas.cryptmyfiles.helpers.SnackBarHelper
import dev.skomlach.biometric.compat.BiometricApi
import dev.skomlach.biometric.compat.BiometricAuthRequest
import dev.skomlach.biometric.compat.BiometricPromptCompat
import dev.skomlach.biometric.compat.BiometricType
import dev.skomlach.biometric.compat.AuthenticationFailureReason
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException

class ActionActivity: AppCompatActivity(), ECResultListener {
    private val layoutFileName by lazy { findViewById<LinearLayout>(R.id.file_name_layout) }
    private val layoutFileSize by lazy { findViewById<LinearLayout>(R.id.file_size_layout) }
    private val tvFileName by lazy { findViewById<TextView>(R.id.tv_file_name) }
    private val tvFileSize by lazy { findViewById<TextView>(R.id.tv_file_size) }
    private val tvTitle by lazy { findViewById<TextView>(R.id.tvTitle) }
    private val indeterminateProgressLayout by lazy { findViewById<LinearLayout>(R.id.indeterminate_progress_layout) }
    private val determinateProgressLayout by lazy { findViewById<LinearLayout>(R.id.determinate_progress_layout) }
    private val determineTextView by lazy { findViewById<TextView>(R.id.determine_textView) }
    private val buttonCancel by lazy { findViewById<Button>(R.id.button_cancel) }
    private val buttonUseFingerprint by lazy { findViewById<Button>(R.id.button_ok) }
    private val buttonUsePassword by lazy { findViewById<Button>(R.id.button_use_password) }
    private val textInputLayout by lazy { findViewById<TextInputLayout>(R.id.textInputLayout) }
    private val tvPassword by lazy { findViewById<TextInputEditText>(R.id.tvPassword) }
    private val progressBar by lazy { findViewById<LinearProgressIndicator>(R.id.determinateBar) }

    private var dataUri: Uri? = null
    private var fileToDelete: File? = null
    private var resultName = ""
    private var resultSize = ""

    private var intentAction: Int = -1
    private var intentRequestCode: Int = -1
    private var intentReplaceCode: Int = -1

    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            disableButtons()
            lifecycleScope.launch {
                whenStarted {
                    dataUri = result.data?.data
                    withContext(Dispatchers.IO) {
                        resultName += dataUri?.getOriginalFileName(context)
                        val length = dataUri?.length(contentResolver)!!
                        if (length == -1L) {
                            SnackBarHelper.showSnackBarError(getString(R.string.this_file_manager_is_not_supported))
                            finish()
                        }
                        resultSize += formatFileSize(length)
                    }
                }

                setFileTextViewNameAndSize(resultName, resultSize)
                hideIndeterminateProgress()
                showFileTextViews()
                enableButtons()
            }
        }
        else
            finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_action)
        setFinishOnTouchOutside(false)

        intentAction = intent.extras?.getInt(ZenCryptConstants.ACTION_CODE)!!
        intentRequestCode = intent.extras?.getInt(ZenCryptConstants.REQUEST_CODE)!!
        intentReplaceCode = intent.extras?.getInt(ZenCryptConstants.REPLACE_CODE)!!

        initWindowTitle()

        if ( !ZenCryptSettingsModel.isProUser.value ) {
            buttonUseFingerprint.isEnabled = false
            buttonUseFingerprint.alpha = 0.5f
            buttonUseFingerprint.text = getString(R.string.fingerprint_pro)
        }

        initButtonListeners()

        when (intentRequestCode) {
            ZenCryptConstants.FILE_PICK -> selectFile()
            else -> {
                val file: File = intent.extras?.get(ZenCryptConstants.FILE) as File

                if (intentAction == ZenCryptConstants.ACTION_ENCRYPT &&
                        ZenCryptSettingsModel.delete_original_unencrypted.value)
                    fileToDelete = file

                dataUri = Uri.fromFile(file)
                resultName = file.name
                resultSize = formatFileSize(file.length())

                setFileTextViewNameAndSize(resultName, resultSize)
                hideIndeterminateProgress()
                showFileTextViews()
            }
        }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startForResult.launch(intent)
    }

    private fun initButtonListeners() {
        buttonCancel.setOnClickListener {
            finish()
        }

        buttonUseFingerprint.setOnClickListener {
            if ( ZenCryptSettingsModel.fingerprint_auth.value )
                startFingerprintAuth()
            else
                SnackBarHelper.showSnackBarInfo(getString(R.string.fingerprint_is_not_enabled), this)
        }

        buttonUsePassword.setOnClickListener {
            startZenCryptAction()
        }
    }

    private fun initWindowTitle() {
        if ( intentAction == ZenCryptConstants.ACTION_ENCRYPT ) {
            tvTitle.text = getString(R.string.encrypt)
            tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.lock_plus_outline,
                0,
                0,
                0
            )
        }
        else {
            tvTitle.text = getString(R.string.decrypt)
            tvTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.lock_minus_outline,
                0,
                0,
                0
            )
        }
    }

    private fun startZenCryptAction(fingerprint: Boolean = false) {
        val pass: String = if ( fingerprint )
            ZenCryptSettingsModel.custom_pass_hash.value
        else
            tvPassword.text.toString().trim()

        if (dataUri != null && pass != "") {
            hidePasswordLayout()
            showDeterminateProgress()
            disableButtons()

            when (intentAction) {
                ZenCryptConstants.ACTION_ENCRYPT -> startEncrypting(pass)
                ZenCryptConstants.ACTION_DECRYPT -> startDecrypting(pass)
            }
        }
        else {
            SnackBarHelper.showSnackBarError(getString(R.string.something_went_wrong_empty_password),this)
        }
    }

    private fun startEncrypting(pass: String) {
        val eCryptSymmetric = ECSymmetric()

        determineTextView.text = getString(R.string.encrypting)
        val fis = contentResolver.openInputStream(dataUri!!)
        val filePath = ZenCryptConstants.encryptedFilesDir(this).absolutePath +
                File.separator +
                resultName +
                ZenCryptSettingsModel.extension.value

        val outputFile = File(filePath)
        if (outputFile.exists())
            outputFile.delete()

        try {
            eCryptSymmetric.encrypt(fis, pass, this, outputFile)
        } catch (e: FileNotFoundException) {
            enableButtons()
            SnackBarHelper.showSnackBarError("FileNotFoundException!",this)
        }
    }

    private fun startDecrypting(pass: String) {
        val eCryptSymmetric = ECSymmetric()

        determineTextView.text = getString(R.string.decrypting)
        val fis = contentResolver.openInputStream(dataUri!!)
        var filePath = ZenCryptConstants.decryptedFilesDir(this).absolutePath +
                File.separator +
                resultName

        //IMPORTANT: two or more dots. (file.jpg.zen -> file.jpg, but file.jpg -> file.jpg)
        if (filePath.matches(".*\\..*\\..*".toRegex()))
            filePath = filePath.substring(0, filePath.lastIndexOf('.'))

        val outputFile = File(filePath)
        if (outputFile.exists())
            outputFile.delete()

        try {
            eCryptSymmetric.decrypt(fis, pass, this, outputFile)
        } catch (e: FileNotFoundException) {
            enableButtons()
            SnackBarHelper.showSnackBarError("FileNotFoundException!",this)
        }
    }

    override fun <T> onSuccess(result: T) {
        SnackBarHelper.showSnackBarCheck(getString(R.string.operation_completed_successfully))

        if (fileToDelete != null && fileToDelete!!.canWrite())
            fileToDelete!!.delete()

        dataUri = null
        fileToDelete = null

        if (ZenCryptSettingsModel.vibration.value) vibrate()

        finish()
        if (intentRequestCode == ZenCryptConstants.FROM_CARD_VIEW) {
            if (intentAction == ZenCryptConstants.ACTION_ENCRYPT)
                FragmentHelper.replaceFragmentWithDelay(DecryptedViewFragment(), 0)
            else
                FragmentHelper.replaceFragmentWithDelay(EncryptedViewFragment(), 0)
        }
        else {
            if (intentReplaceCode == ZenCryptConstants.REPLACE_WITH_ENCRYPTED)
                FragmentHelper.replaceFragmentWithDelay(EncryptedViewFragment(), 0)
            else
                FragmentHelper.replaceFragmentWithDelay(DecryptedViewFragment(), 0)
        }
    }

    private var maxSet = false
    override fun onProgress(newBytes: Int, bytesProcessed: Long, totalBytes: Long) {
        if (totalBytes > -1) {
            lifecycleScope.launch {
                if (!maxSet) {
                    progressBar.max = (totalBytes / 1024).toInt()
                    maxSet = true
                }
                progressBar.progress = (bytesProcessed / 1024).toInt()
            }
        }
    }

    override fun onFailure(message: String, e: Exception) {
        val errorMessage = getString(R.string.maybe_wrong_password)
        println("ERROR: $message")
        SnackBarHelper.showSnackBarError(errorMessage)
        if (ZenCryptSettingsModel.vibration.value) vibrate()
        finish()
    }

    private fun startFingerprintAuth() {
        val biometricPromptCompat = BiometricPromptCompat.Builder(
            BiometricAuthRequest(BiometricApi.AUTO, BiometricType.BIOMETRIC_FINGERPRINT),
            this
        )
            .setTitle(if (intentAction == ZenCryptConstants.ACTION_ENCRYPT) getString(R.string.encrypt) else getString(R.string.decrypt))
            .setSubtitle(resultName)
            .setEnabledNotification(false)
            .setNegativeButton(getString(android.R.string.cancel), null)
            .build()

        biometricPromptCompat.authenticate(object : BiometricPromptCompat.AuthenticationCallback() {
            override fun onSucceeded(confirmed : Set<BiometricType>) {
                startZenCryptAction(fingerprint = true)
            }

            override fun onCanceled() {
                //do nothing
            }

            override fun onFailed(reason: AuthenticationFailureReason?) {
                SnackBarHelper.showSnackBarError("Error : $reason", this@ActionActivity)
                if (ZenCryptSettingsModel.vibration.value) vibrate()
            }

            override fun onUIClosed() {
                //do nothing
            }

            override fun onUIOpened() {
                //do nothing
            }
        })
    }

    private fun disableButtons() {
        buttonCancel.isEnabled = false
        buttonCancel.alpha = 0.5f
        buttonUseFingerprint.isEnabled = false
        buttonUseFingerprint.alpha = 0.5f
        buttonUsePassword.isEnabled = false
        buttonUsePassword.alpha = 0.5f
    }

    private fun enableButtons() {
        buttonCancel.isEnabled = true
        buttonCancel.alpha = 1.0f
        if ( ZenCryptSettingsModel.isProUser.value ) {
            buttonUseFingerprint.isEnabled = true
            buttonUseFingerprint.alpha = 1.0f
        }
        buttonUsePassword.isEnabled = true
        buttonUsePassword.alpha = 1.0f
    }

    private fun setFileTextViewNameAndSize(name: String, size: String) {
        tvFileName.text = name
        tvFileSize.text = size
    }

    private fun showDeterminateProgress() {
        determinateProgressLayout.visibility = VISIBLE
    }

    private fun hideIndeterminateProgress() {
        indeterminateProgressLayout.visibility = GONE
    }

    private fun showFileTextViews() {
        layoutFileName.visibility = VISIBLE
        layoutFileSize.visibility = VISIBLE
    }

    private fun hidePasswordLayout() {
        textInputLayout.visibility = GONE
    }

    private fun Uri.getOriginalFileName(context: Context): String? {
        return context.contentResolver.query(this, null, null, null, null)?.use {
            val nameColumnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            it.moveToFirst()
            it.getString(nameColumnIndex)
        }
    }

    private fun Uri.length(contentResolver: ContentResolver): Long {
        val assetFileDescriptor = try {
            contentResolver.openAssetFileDescriptor(this, "r")
        } catch (e: FileNotFoundException) {
            null
        }
        // uses ParcelFileDescriptor#getStatSize underneath if failed
        val length = assetFileDescriptor?.use { it.length } ?: -1L
        if (length != -1L) {
            return length
        }

        // if "content://" uri scheme, try contentResolver table
        if (scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            return contentResolver.query(this, arrayOf(OpenableColumns.SIZE), null, null, null)
                ?.use { cursor ->
                    // maybe shouldn't trust ContentResolver for size: https://stackoverflow.com/questions/48302972/content-resolver-returns-wrong-size
                    val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                    if (sizeIndex == -1) {
                        return@use -1L
                    }
                    cursor.moveToFirst()
                    return try {
                        cursor.getLong(sizeIndex)
                    } catch (_: Throwable) {
                        -1L
                    }
                } ?: -1L
        } else {
            return -1L
        }
    }

    private fun formatFileSize(size: Long, decimals: Int = 2) : String{
        if (size <= 0L)
            return "0B"

        val kb: Double = size.toDouble() / 1024
        return if (kb < 1024)
            "%.${decimals}f KiB".format(kb)
        else {
            val mb: Double = kb / 1024
            if (mb < 1024) {
                "%.${decimals}f MiB".format(mb)
            }
            else {
                val gb: Double = mb / 1024
                "%.${decimals}f GiB".format(gb)
            }
        }
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(150)
        }
    }
}