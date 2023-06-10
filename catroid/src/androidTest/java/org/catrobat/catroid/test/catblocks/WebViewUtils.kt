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
import android.webkit.ValueCallback
import android.webkit.WebView

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.catrobat.catroid.R

class WebViewUtils {
    private val DEFAULT_TIMEOUT_SECONDS: Long = 5
    private var activity: Activity
    private var webView: WebView

    companion object {
        private val pageLoadLatch: CountDownLatch = CountDownLatch(1)
    }

    constructor(activity: Activity) {
        this.activity = activity
        this.webView = activity.findViewById(R.id.catblocksWebView)

        activity.runOnUiThread {
            webView.addJavascriptInterface(JSInterface(pageLoadLatch), "webViewUtils")
            webView.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html")
        }
    }

    fun waitForElement(querySelector: String, onElementFound: (() -> Unit)? = null) {
        if(!pageLoadLatch.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
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
                }, ${DEFAULT_TIMEOUT_SECONDS * 1000});
            })();
        """.trimIndent()

        activity.runOnUiThread {
            // run jsCode, if page is finished with loading
            webView.evaluateJavascript(jsCode, null)
        }

        val found = currentLatch.await(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        if (found) {
            onElementFound?.invoke()
        } else {
            throw ElementNotFoundException("Element '$querySelector' not found within the specified timeout")
        }
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