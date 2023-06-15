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
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.badlogic.gdx.math.Rectangle

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.catrobat.catroid.R
import org.json.JSONObject

class WebViewUtils {
    private val DEFAULT_TIMEOUT_SECONDS: Long = 5
    private var TIMEOUT_SECONDS: Long = DEFAULT_TIMEOUT_SECONDS
    private var activity: Activity
    private var webView: WebView

    companion object {
        private val pageLoadLatch: CountDownLatch = CountDownLatch(1)
    }

    constructor(activity: Activity, timeoutSeconds: Long? = null) {
        this.activity = activity
        this.webView = activity.findViewById(R.id.catblocksWebView)

        if (timeoutSeconds != null) {
            TIMEOUT_SECONDS = timeoutSeconds
        }

        activity.runOnUiThread {
            webView.addJavascriptInterface(JSInterface(pageLoadLatch), "webViewUtils")
            webView.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html")
        }
    }

    fun isElementVisible(querySelector: String): Boolean {
        if(!pageLoadLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw PageLoadTimeoutException("Page load timed out")
        }
        var currentLatch = CountDownLatch(1)

        val jsCode = """
            javascript:(function() {
                const initialElement = document.querySelector('$querySelector');
                if (!initialElement) {
                    return false;
                }
                
                function isVisible(element) {
                    const style = getComputedStyle(element);
                  
                    if (style.display === 'none') {
                        console.log(element, "display none");
                        return false;
                    }
                    if (style.visibility !== 'visible') {
                        console.log(element, "visibility hidden");
                        return false;
                    }
                  
                    if (element.parentNode && element.parentNode.nodeType === Node.ELEMENT_NODE) {
                        console.log("checking parent", element.parentNode);
                        return isVisible(element.parentNode);
                    }
                  
                    console.log(element, "is visible");
                    return true;
                }
                
                return isVisible(initialElement);
            })();
        """.trimIndent()

        var found = false
        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode) { result ->
                found = result == "true"
                currentLatch.countDown()
            }
        }

        val finished = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (!finished) {
            throw ElementNotFoundException("Check for '$querySelector' did not finish " +
                                               "within timeout")
        }

        return found
    }

    fun waitForElement(querySelector: String, onElementFound: (() -> Unit)? = null) {
        if(!pageLoadLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw PageLoadTimeoutException("Page load timed out")
        }
        var currentLatch = CountDownLatch(1)
        JSInterface.waitForElementLatch = currentLatch

        val jsCode = """
            javascript:(function() {
                var found = false;
                var checkInterval = setInterval(function() {
                    console.log('waiting for element: $querySelector');
                    var element = document.querySelector('$querySelector');
                    if (element !== null && element !== undefined) {
                        console.log('element found: $querySelector');
                        found = true;
                        clearInterval(checkInterval);
                        window.webViewUtils.onElementFound();
                    }
                }, 100);
                setTimeout(function() {
                    if (!found) {
                        console.log('element not found: $querySelector');
                        clearInterval(checkInterval);
                    }
                }, ${TIMEOUT_SECONDS * 1000});
            })();
        """.trimIndent()

        activity.runOnUiThread {
            webView.evaluateJavascript(jsCode, null)
        }

        val found = currentLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (found) {
            onElementFound?.invoke()
        } else {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }
    }

    fun clickElement(querySelector: String) {
        if(!pageLoadLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw PageLoadTimeoutException("Page load timed out")
        }

        var currentLatch = CountDownLatch(1)
        val jsCode = """
            javascript:(function() {
                const element = document.querySelector('$querySelector');
                if (!element) {
                    return false;
                }
                
                const events = [
                    'pointerover',
                    'pointerenter',
                    'pointerdown',
                    'touchstart',
                    'pointerup',
                    'pointerout',
                    'pointerleave',
                    'touchend',
                    'mouseover',
                    'click'
                ];
                
                for (const event of events) {
                    const opts = { bubbles: true };
                    let firedEvent;
                    if (event.includes("touch")) {
                        firedEvent = new TouchEvent(event, opts);
                    } else if (event.includes("pointer")) {
                        firedEvent = new PointerEvent(event, opts);
                    } else {
                        firedEvent = new MouseEvent(event, opts);
                    }
                    
                    console.log("Fired Event:", firedEvent);
                    element.dispatchEvent(firedEvent);
                }
                return true;
            })();
        """.trimIndent()

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

    fun moveElementByPixels(querySelector: String, directionX: Int, directionY: Int) {
        if(!pageLoadLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw PageLoadTimeoutException("Page load timed out")
        }

        var currentLatch = CountDownLatch(1)
        val jsCode = """
            javascript:(function() {
                const element = document.querySelector('$querySelector');
                if (!element) {
                    return false;
                }
                
                const events = [
                    { type: 'pointerover', includeCoords: false },
                    { type: 'pointerenter', includeCoords: false },
                    { type: 'pointerdown', includeCoords: false },
                    { type: 'touchstart', includeCoords: false },
                    { type: 'pointermove', includeCoords: true },
                    { type: 'touchmove', includeCoords: true },
                    { type: 'pointerup', includeCoords: true },
                    { type: 'pointerout', includeCoords: true },
                    { type: 'pointerleave', includeCoords: true },
                    { type: 'touchend', includeCoords: true }
                ];
                
                for (const event of events) {
                    const { type, includeCoords } = event;
                    const opts = { bubbles: true };
                    let firedEvent = "unsupported event";
                    if (type.includes("touch")) {
                        if (includeCoords) {
                            opts.touches = [new Touch({identifier: 0, target: element, clientX: $directionX, clientY: $directionY})];
                        }
                        firedEvent = new TouchEvent(type, opts);
                        element.dispatchEvent(firedEvent);
                    } else if (type.includes("pointer")) {
                        if (includeCoords) {
                            opts.clientX = $directionX;
                            opts.clientY = $directionY;
                        }
                        firedEvent = new PointerEvent(type, opts);
                        element.dispatchEvent(firedEvent);
                    }
                    
                    console.log("Fired Event:", firedEvent);
                }

                return true;
            })();
        """.trimIndent()

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

    fun getWorkspaceBoundingClientRect(): Rectangle {
        return getBoundingClientRectOfElement(".blocklyWorkspace")
    }

    fun getBoundingClientRectOfElement(querySelector: String): Rectangle {
        if (!pageLoadLatch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw PageLoadTimeoutException("Page load timed out")
        }

        var currentLatch = CountDownLatch(1)
        val jsCode = """
            javascript:(function() {
                const element = document.querySelector('$querySelector');
                if (!element) {
                    return false;
                }
                const rect = element.getBoundingClientRect();
                return { x: rect.x, y: rect.y, width: rect.width, height: rect.height };
            })();
        """.trimIndent()
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

    private fun getOpenSpinnerJavascript(catblocksSpinnerId: String): String {
        return "document.getElementById('$catblocksSpinnerId').dispatchEvent(new PointerEvent" +
            "('pointerover', {\n" +
            "    bubbles: true\n" +
            "  }));\n" +
            "  document.getElementById('$catblocksSpinnerId').dispatchEvent(new PointerEvent('pointerdown', {\n" +
            "    bubbles: true\n" +
            "  }));\n" +
            "  document.getElementById('$catblocksSpinnerId').dispatchEvent(new PointerEvent('pointerup', {\n" +
            "    bubbles: true\n" +
            "  }));"
    }

    private class JSInterface(private val pageLoadLatch: CountDownLatch) {
        companion object {
            var waitForElementLatch: CountDownLatch = CountDownLatch(1)
        }
        @JavascriptInterface
        fun onElementFound() {
            waitForElementLatch.countDown()
        }

        @JavascriptInterface
        fun onPageLoaded() {
            pageLoadLatch.countDown()
        }
    }

    class ElementNotFoundException(message: String) : RuntimeException(message)
    class PageLoadTimeoutException(message: String) : RuntimeException(message)
}