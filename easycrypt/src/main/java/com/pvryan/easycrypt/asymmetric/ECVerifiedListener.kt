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
package com.pvryan.easycrypt.asymmetric

/**
 * Interface to listen for result from [ECAsymmetric.verify]
 */
interface ECVerifiedListener {

    /**
     * @param newBytes count processed after last block
     * @param bytesProcessed count from total input
     */
    fun onProgress(newBytes: Int, bytesProcessed: Long, totalBytes: Long) {}

    /**
     * @param verified on successful execution of the calling method
     */
    fun onSuccess(verified: Boolean)

    /**
     * @param message on failure
     * @param e exception thrown by called method
     */
    fun onFailure(message: String, e: Exception)
}
