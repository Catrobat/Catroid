/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.test.catblocks

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.badlogic.gdx.math.Rectangle

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.catrobat.catroid.R
import org.json.JSONObject

class WebViewUtils(private var activity: Activity, timeoutSeconds: Long? = null) {
    private val DEFAULT_TIMEOUT_SECONDS: Long = 5
    private var TIMEOUT_SECONDS: Long = DEFAULT_TIMEOUT_SECONDS
    private var webView: WebView = activity.findViewById(R.id.catblocksWebView)
    private val jsInterface: JSInterface
    private val pageLoadLatch: CountDownLatch

    init {
        if (timeoutSeconds != null) {
            TIMEOUT_SECONDS = timeoutSeconds
        }

        pageLoadLatch = CountDownLatch(1)
        jsInterface = JSInterface(pageLoadLatch)
        activity.runOnUiThread {
            webView.addJavascriptInterface(jsInterface, "webViewUtils")
            webView.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html")
        }
    }

    fun waitForElementVisible(querySelector: String) = waitForElementVisibility(querySelector, true)
    fun waitForElementInvisible(querySelector: String) = waitForElementVisibility(querySelector, false)
    private fun waitForElementVisibility(querySelector: String, visible: Boolean) {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)
        jsInterface.setAsyncLatch(currentLatch)

        val jsCode = "javascript:window.webViewUtilsFunctions.waitForElementVisibility" +
            "('$querySelector', $visible, ${TIMEOUT_SECONDS * 1000});"

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode, null)
        }

        val success = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (jsInterface.getAsyncLatch() != currentLatch) {
            throw IllegalStateException("Latch was changed by another thread")
        }
        if (!success) {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }
    }

    fun isElementVisible(querySelector: String): Boolean {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)

        val jsCode = "javascript:window.webViewUtilsFunctions.isElementVisible('$querySelector');"

        var found = false
        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                found = result == "true"
                currentLatch.countDown()
            }
        }

        val finished = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!finished) {
            throw ElementNotFoundException(
                "Check for '$querySelector' did not finish within timeout"
            )
        }

        return found
    }

    fun waitForElement(querySelector: String, onElementFound: (() -> Unit)? = null) {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)
        jsInterface.setAsyncLatch(currentLatch)

        val jsCode = "javascript:window.webViewUtilsFunctions.waitForElement" +
            "('$querySelector', ${TIMEOUT_SECONDS * 1000});"

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode, null)
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (jsInterface.getAsyncLatch() != currentLatch) {
            throw IllegalStateException("Latch was changed by another thread")
        }
        if (found) {
            onElementFound?.invoke()
        } else {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }
    }

    fun clickElement(querySelector: String) {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)

        val jsCode = "javascript:window.webViewUtilsFunctions.clickElement('$querySelector');"

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                if (result == "false") {
                    throw ElementNotFoundException("Element '$querySelector' not found")
                } else {
                    currentLatch.countDown()
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }
    }

    fun openBlocklySpinner(elementId: String) {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)

        val jsCode = "javascript:window.webViewUtilsFunctions.openBlocklySpinner('$elementId');"

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                if (result == "false") {
                    throw ElementNotFoundException("Element '$elementId' not found")
                } else {
                    currentLatch.countDown()
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Element '$elementId' not found within the specified timeout")
        }
    }

    fun selectBlocklySpinnerItem(elementId: String) {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)

        val jsCode = "javascript:window.webViewUtilsFunctions.selectBlocklySpinnerElement('$elementId');"

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                if (result == "false") {
                    throw ElementNotFoundException("Element '$elementId' not found")
                } else {
                    currentLatch.countDown()
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Element '$elementId' not found within the specified timeout")
        }
    }

    fun moveElementByPixels(querySelector: String, directionX: Int, directionY: Int) {
        waitForPageToLoad()
        val currentLatch = CountDownLatch(1)

        val jsCode = "javascript:window.webViewUtilsFunctions.moveElementByPixels" +
            "('$querySelector', $directionX, $directionY);"

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                if (result == "false") {
                    throw ElementNotFoundException("Element '$querySelector' not found")
                } else {
                    currentLatch.countDown()
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }
    }

    fun getWorkspaceBoundingClientRect() = getBoundingClientRectOfElement(".blocklyWorkspace")

    fun getBoundingClientRectOfElement(querySelector: String): Rectangle {
        waitForPageToLoad()

        val currentLatch = CountDownLatch(1)
        val jsCode = "javascript:window.webViewUtilsFunctions.getBoundingClientRectOfElement('$querySelector');"

        val resultRect = Rectangle()

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                try {
                    val resultObject = JSONObject(result)
                    resultRect.x = resultObject.getDouble("x").toFloat()
                    resultRect.y = resultObject.getDouble("y").toFloat()
                    resultRect.width = resultObject.getDouble("width").toFloat()
                    resultRect.height = resultObject.getDouble("height").toFloat()
                    currentLatch.countDown()
                } catch (e: Exception) {
                    Log.e("CatBlocks", "Error parsing JSON: $result", e)
                    throw ElementNotFoundException("Element '$querySelector' not found")
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }

        return resultRect
    }

    data class BlocklySpinnerItem(val id: String, val text: String)
    fun getNotSelectedBlocklySpinnerItem(): BlocklySpinnerItem? {
        waitForPageToLoad()

        val currentLatch = CountDownLatch(1)
        val jsCode = "javascript:window.webViewUtilsFunctions.getBlocklySpinnerElementToSelect();"

        var spinnerItem: BlocklySpinnerItem? = null

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                try {
                    val resultObject = JSONObject(result)
                    spinnerItem = BlocklySpinnerItem(
                        resultObject.getString("id"),
                        resultObject.getString("text"))
                    currentLatch.countDown()
                } catch (e: Exception) {
                    Log.e("CatBlocks", "Error parsing JSON: $result", e)
                    throw ElementNotFoundException("No Blockly spinner item found")
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Failed to get free Blockly spinner item.")
        }

        return spinnerItem
    }
    fun getText(querySelector: String): String? {
        waitForPageToLoad()

        val currentLatch = CountDownLatch(1)
        val jsCode = "javascript:window.webViewUtilsFunctions.getText('$querySelector');"

        var foundText: String? = null

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                try {
                    foundText = result
                    currentLatch.countDown()
                } catch (e: Exception) {
                    Log.e("CatBlocks", "Error parsing JSON: $result", e)
                    throw ElementNotFoundException("Element '$querySelector' not found")
                }
            }
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!found) {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }

        return foundText
    }

    private fun waitForPageToLoad() {
        if (!pageLoadLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw PageLoadTimeoutException("Page load timed out")
        }
    }

    private class JSInterface(private val pageLoadLatch: CountDownLatch) {
        private var asyncWaitLatch: CountDownLatch = CountDownLatch(1)
        fun setAsyncLatch(latch: CountDownLatch) {
            asyncWaitLatch = latch
        }
        fun getAsyncLatch() = asyncWaitLatch

        @JavascriptInterface
        fun signalSuccess() {
            asyncWaitLatch.countDown()
        }

        @JavascriptInterface
        fun onPageLoaded() {
            pageLoadLatch.countDown()
        }
    }

    class ElementNotFoundException(message: String) : RuntimeException(message)
    class PageLoadTimeoutException(message: String) : RuntimeException(message)
}
