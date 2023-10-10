/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2022 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid

import android.util.Log
import androidx.annotation.VisibleForTesting
import org.catrobat.catroid.common.Constants.JSON_INDENTATION
import org.catrobat.catroid.common.Constants.TRUSTED_DOMAINS_FILE_NAME
import org.catrobat.catroid.common.Constants.TRUSTED_USER_DOMAINS_FILE
import org.catrobat.catroid.common.Constants.TRUST_LIST_JSON_ARRAY_NAME
import org.catrobat.catroid.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

object TrustedDomainManager {
    private val TAG = TrustedDomainManager::class.java.simpleName
    private const val READ_ERROR_LOG = "Cannot read trusted domains"
    private const val PARSE_ERROR_LOG = "Cannot parse trusted domains"

    private var trustListPattern: Pattern? = null
    var userTrustListPattern: Pattern? = null

    @Synchronized
    fun isURLTrusted(url: String): Boolean {
        if (userTrustListPattern == null) {
            userTrustListPattern = initializeUserTrustListPattern()
        }
        if (trustListPattern == null) {
            trustListPattern = initializeTrustListPattern()
        }
        return trustListPattern?.matcher(url)?.matches() ?: false ||
            userTrustListPattern?.matcher(url)?.matches() ?: false
    }

    fun addToUserTrustList(domain: String): Boolean {
        try {
            val domains = when {
                TRUSTED_USER_DOMAINS_FILE.exists() ->
                    TRUSTED_USER_DOMAINS_FILE.inputStream().use {
                        val trustList = Utils.getJsonObjectFromInputStream(it)
                        trustList.getJSONArray(TRUST_LIST_JSON_ARRAY_NAME)
                            .put(cleanUpUserInput(domain))
                    }

                TRUSTED_USER_DOMAINS_FILE.createNewFile() -> JSONArray(
                    listOf(cleanUpUserInput(domain))
                )

                else -> return false
            }
            userTrustListPattern = getTrustListPatternFromDomains(domains)
            val trustList = JSONObject(mapOf(TRUST_LIST_JSON_ARRAY_NAME to domains))
            TRUSTED_USER_DOMAINS_FILE.writeText(trustList.toString(JSON_INDENTATION))
            return true
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return false
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return false
        }
    }

    fun getUserTrustList(): String {
        return try {
            if (TRUSTED_USER_DOMAINS_FILE.exists()) {
                TRUSTED_USER_DOMAINS_FILE.inputStream().use {
                    val trustList = Utils.getJsonObjectFromInputStream(it)
                    val domains = trustList.getJSONArray(TRUST_LIST_JSON_ARRAY_NAME)
                    cleanUpUserInput(domains.join("\n"))
                }
            } else ""
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            ""
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            ""
        }
    }

    fun setUserTrustList(domains: String): Boolean {
        try {
            return when {
                domains.isBlank() -> resetUserTrustList()
                TRUSTED_USER_DOMAINS_FILE.exists() || TRUSTED_USER_DOMAINS_FILE.createNewFile() -> {
                    val jsonArray = JSONArray(cleanUpUserInput(domains).split("\n"))
                    userTrustListPattern = getTrustListPatternFromDomains(jsonArray)
                    val trustList = JSONObject(mapOf(TRUST_LIST_JSON_ARRAY_NAME to jsonArray))
                    TRUSTED_USER_DOMAINS_FILE.writeText(trustList.toString(JSON_INDENTATION))
                    true
                }

                else -> false
            }
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return false
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return false
        }
    }

    private fun initializeTrustListPattern(): Pattern? {
        try {
            Utils.getInputStreamFromAsset(
                CatroidApplication.getAppContext(),
                TRUSTED_DOMAINS_FILE_NAME
            ).use {
                val trustList = Utils.getJsonObjectFromInputStream(it)
                val domains = trustList.getJSONArray(TRUST_LIST_JSON_ARRAY_NAME)
                return getTrustListPatternFromDomains(domains)
            }
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return null
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return null
        }
    }

    private fun initializeUserTrustListPattern(): Pattern? {
        try {
            if (!TRUSTED_USER_DOMAINS_FILE.exists()) {
                return null
            }
            TRUSTED_USER_DOMAINS_FILE.inputStream().use {
                val trustList = Utils.getJsonObjectFromInputStream(it)
                val domains = trustList.getJSONArray(TRUST_LIST_JSON_ARRAY_NAME)
                return getTrustListPatternFromDomains(domains)
            }
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return null
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return null
        }
    }

    private fun getTrustListPatternFromDomains(domains: JSONArray): Pattern {
        val trustedDomains = StringBuilder("(")
        for (i in 0 until domains.length()) {
            trustedDomains.append(domains.getString(i))
            if (i < domains.length() - 1) {
                trustedDomains.append('|')
            }
        }
        trustedDomains.append(')')

        return Pattern.compile(
            "https?://([a-zA-Z0-9-]+\\.)*" +
                trustedDomains.toString().replace("\\.".toRegex(), "\\\\.") +
                "(:[0-9]{1,5})?(/.*)?"
        )
    }

    private fun cleanUpUserInput(string: String): String =
        string.replace("[ \"{}\\[\\]]|(\\n){2,}".toRegex(), "").removeSuffix("\n")

    private fun resetUserTrustList(): Boolean {
        userTrustListPattern = null
        return TRUSTED_USER_DOMAINS_FILE.delete()
    }

    @VisibleForTesting
    fun reset() {
        resetUserTrustList()
        trustListPattern = null
    }
}
