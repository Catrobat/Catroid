/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2020 The Catrobat Team
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
import org.catrobat.catroid.common.Constants.URL_WHITELIST_JSON_FILE_NAME
import org.catrobat.catroid.common.Constants.USER_WHITELIST_FILE
import org.catrobat.catroid.common.Constants.WHITELIST_JSON_ARRAY_NAME
import org.catrobat.catroid.utils.Utils
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.regex.Pattern

object WhiteListManager {
    private val TAG = WhiteListManager::class.java.simpleName
    private const val READ_ERROR_LOG = "Cannot read whitelist"
    private const val PARSE_ERROR_LOG = "Cannot parse whitelist"

    private val urlWhitelistPattern: Pattern? by lazy {
        initializeURLWhitelistPattern()
    }
    var userWhiteListPattern: Pattern? = null

    @Synchronized
    fun checkIfURLIsWhitelisted(url: String): Boolean {
        if (userWhiteListPattern == null) {
            userWhiteListPattern = initializeUserWhitelistPattern()
        }
        return urlWhitelistPattern?.matcher(url)?.matches() ?: false ||
            userWhiteListPattern?.matcher(url)?.matches() ?: false
    }

    fun addToUserWhitelist(domain: String): Boolean {
        try {
            val domains = when {
                USER_WHITELIST_FILE.exists() ->
                    USER_WHITELIST_FILE.inputStream().use {
                        val whiteList = Utils.getJsonObjectFromInputStream(it)
                        whiteList.getJSONArray(WHITELIST_JSON_ARRAY_NAME).put(cleanUpUserInput(domain))
                }
                USER_WHITELIST_FILE.createNewFile() -> JSONArray(listOf(cleanUpUserInput(domain)))
                else -> return false
            }
            userWhiteListPattern = getWhiteListPatternFromDomains(domains)
            val whiteList = JSONObject(mapOf(WHITELIST_JSON_ARRAY_NAME to domains))
            USER_WHITELIST_FILE.writeText(whiteList.toString(JSON_INDENTATION))
            return true
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return false
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return false
        }
    }

    fun getUserWhiteList(): String {
        return try {
            if (USER_WHITELIST_FILE.exists()) {
                USER_WHITELIST_FILE.inputStream().use {
                    val whiteList = Utils.getJsonObjectFromInputStream(it)
                    val domains = whiteList.getJSONArray(WHITELIST_JSON_ARRAY_NAME)
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

    fun setUserWhiteList(domains: String): Boolean {
        try {
            return when {
                domains.isBlank() -> resetUserWhiteList()
                USER_WHITELIST_FILE.exists() || USER_WHITELIST_FILE.createNewFile() -> {
                    val jsonArray = JSONArray(cleanUpUserInput(domains).split("\n"))
                    userWhiteListPattern = getWhiteListPatternFromDomains(jsonArray)
                    val whiteList = JSONObject(mapOf(WHITELIST_JSON_ARRAY_NAME to jsonArray))
                    USER_WHITELIST_FILE.writeText(whiteList.toString(JSON_INDENTATION))
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

    private fun initializeURLWhitelistPattern(): Pattern? {
        try {
            Utils.getInputStreamFromAsset(
                CatroidApplication.getAppContext(),
                URL_WHITELIST_JSON_FILE_NAME
            ).use {
                val whiteList = Utils.getJsonObjectFromInputStream(it)
                val domains = whiteList.getJSONArray(WHITELIST_JSON_ARRAY_NAME)
                return getWhiteListPatternFromDomains(domains)
            }
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return null
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return null
        }
    }

    private fun initializeUserWhitelistPattern(): Pattern? {
        try {
            if (!USER_WHITELIST_FILE.exists()) {
                return null
            }
            USER_WHITELIST_FILE.inputStream().use {
                val whiteList = Utils.getJsonObjectFromInputStream(it)
                val domains = whiteList.getJSONArray(WHITELIST_JSON_ARRAY_NAME)
                return getWhiteListPatternFromDomains(domains)
            }
        } catch (e: IOException) {
            Log.e(TAG, READ_ERROR_LOG, e)
            return null
        } catch (e: JSONException) {
            Log.e(TAG, PARSE_ERROR_LOG, e)
            return null
        }
    }

    private fun getWhiteListPatternFromDomains(domains: JSONArray): Pattern {
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

    @VisibleForTesting
    fun resetUserWhiteList(): Boolean {
        userWhiteListPattern = null
        return USER_WHITELIST_FILE.delete()
    }
}
