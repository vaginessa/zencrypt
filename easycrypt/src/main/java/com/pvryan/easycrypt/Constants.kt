/*
 * Copyright 2018 Priyank Vasa
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pvryan.easycrypt

import android.os.Environment
import com.nulabinc.zxcvbn.Zxcvbn
import java.io.File
import java.security.SecureRandom

internal open class Constants {

    companion object {

        val random = SecureRandom()
        val zxcvbn = Zxcvbn()

        const val SECRET_KEY_FAC_ALGORITHM = "PBKDF2WithHmacSHA1"
//                if (Build.VERSION.SDK_INT < 26) "PBKDF2WithHmacSHA1"
//                else "PBEwithHmacSHA512AndAES_256"

        const val SECRET_KEY_SPEC_ALGORITHM = "AES"

        const val KEY_BITS_LENGTH = 256
        const val SALT_BYTES_LENGTH = KEY_BITS_LENGTH / 8

        const val ITERATIONS = 10000

        const val ASYMMETRIC_ALGORITHM = "RSA"
        const val ASYMMETRIC_TRANSFORMATION = "RSA/NONE/OAEPwithSHA-256andMGF1Padding"
        const val SIGNATURE_ALGORITHM = "SHA512withRSA"

        private const val HASH_FILE_EXT = ".txt"
        const val ECRYPT_FILE_EXT = ".ecrypt"

        private const val HASH_FILE_NAME = "HashOfFile"
        const val ENCRYPTED_FILE_NAME = "EncryptedFile"
        private const val DECRYPTED_FILE_NAME = "DecryptedFile"

        private const val TEMP_DIR_NAME = ".ecrypt"

        val DEF_EXT_TEMP_DIR_PATH =
                Environment.getExternalStorageDirectory().absolutePath +
                        File.separator + TEMP_DIR_NAME

        private val DEF_EXT_FILE_PATH =
                Environment.getExternalStorageDirectory().absolutePath + File.separator

        val DEF_HASH_FILE_PATH = DEF_EXT_FILE_PATH + HASH_FILE_NAME + HASH_FILE_EXT
        val DEF_ENCRYPTED_FILE_PATH = DEF_EXT_FILE_PATH + ENCRYPTED_FILE_NAME + ECRYPT_FILE_EXT
        val DEF_DECRYPTED_FILE_PATH = DEF_EXT_FILE_PATH + DECRYPTED_FILE_NAME + ECRYPT_FILE_EXT

        const val ERR_INVALID_KEY = "The key is not valid. Please provide a valid RSA key."
        const val ERR_VERIFY_EXCEPTION = "Cannot use provided signature to verify input data."
        const val ERR_SIGN_EXCEPTION = "This signature algorithm is unable to process the input data provided."
        const val ERR_INPUT_TYPE_NOT_SUPPORTED = "Input type not supported."
        const val ERR_INVALID_INPUT_DATA = "Invalid input data."
        const val ERR_NO_SUCH_FILE = "File does not exist."
        const val ERR_CANNOT_WRITE = "Cannot write to file."
        const val ERR_CANNOT_READ = "Cannot read from file."
        const val ERR_OUTPUT_FILE_EXISTS = "Output file already exists."
        const val ERR_BAD_BASE64 = "Invalid base64 string."

        const val STANDARD_SYMBOLS =
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                        "abcdefghijklmnopqrstuvwxyz" +
                        "0123456789"

        const val PASSWORD_LENGTH: Int = 24

    }
}
