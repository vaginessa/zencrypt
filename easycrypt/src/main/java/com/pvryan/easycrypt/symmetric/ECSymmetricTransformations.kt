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

package com.pvryan.easycrypt.symmetric

/**
 * Transformations that can be used for symmetric encryption/decryption
 */
@Suppress("EnumEntryName")
enum class ECSymmetricTransformations(val value: String) {

    AES_CTR_NoPadding("AES/CTR/NoPadding"),
    AES_CBC_PKCS7Padding("AES/CBC/PKCS7Padding")

}
