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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.ValueCallback
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.webkit.WebViewAssetLoader
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import java.util.Locale
import java.util.UUID

class CatblocksScriptFragment(
    private val currentProject: Project?,
    private val currentScene: Scene?,
    private val currentSprite: Sprite?,
    private val currentScriptIndex: Int
) : Fragment() {

    private var webview: WebView? = null

    companion object {
        val TAG: String = CatblocksScriptFragment::class.java.simpleName
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.removeItem(R.id.backpack)
        menu.removeItem(R.id.copy)
        menu.removeItem(R.id.delete)
        menu.removeItem(R.id.rename)
        menu.removeItem(R.id.show_details)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.catblocks) {
            SettingsFragment.setUseCatBlocks(context, false)

            val fragmentTransaction = parentFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(
                R.id.fragment_container, ScriptFragment(currentProject),
                ScriptFragment.TAG
            )
            fragmentTransaction?.commit()

            return true
        } else if (item.itemId == R.id.catblocks_reorder_scripts) {
            val callback = ReorderCallback()
            webview!!.evaluateJavascript("javascript:CatBlocks.reorderCurrentScripts();", callback)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        val actionBar = (activity as AppCompatActivity?)!!.supportActionBar
        actionBar?.title = currentProject?.name
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BottomBar.showBottomBar(activity)
        BottomBar.showPlayButton(activity)
        BottomBar.hideAddButton(activity)

        setHasOptionsMenu(true)

        val view = View.inflate(activity, R.layout.fragment_catblocks, null)
        val webView = view.findViewById<WebView>(R.id.catblocksWebView)
        initWebView(webView)
        this.webview = webView
        return view
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView(catblocksWebView: WebView) {
        catblocksWebView.settings.javaScriptEnabled = true

        if (BuildConfig.FEATURE_CATBLOCKS_DEBUGABLE) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        val assetLoader: WebViewAssetLoader = WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", WebViewAssetLoader.AssetsPathHandler(requireActivity()))
            .addPathHandler("/res/", WebViewAssetLoader.ResourcesPathHandler(requireActivity()))
            .build()

        catblocksWebView.addJavascriptInterface(
            JSInterface(
                currentProject,
                currentScene,
                currentSprite,
                currentScriptIndex
            ), "Android"
        )

        catblocksWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }
        }
        catblocksWebView.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html")
    }

    class ReorderCallback : ValueCallback<String> {

        override fun onReceiveValue(value: String?) { // do nothing
        }
    }

    class JSInterface(
        private val project: Project?,
        private val scene: Scene?,
        private val sprite: Sprite?,
        private val script: Int
    ) {

        @JavascriptInterface
        fun getCurrentProject(): String {
            val projectXml = XstreamSerializer.getInstance().getXmlAsStringFromProject(project)
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n$projectXml"
        }

        @JavascriptInterface
        fun getCurrentLanguage(): String =
            Locale.getDefault().toString().replace("_", "-")

        @JavascriptInterface
        fun isRTL(): Boolean {
            val directionality = Character.getDirectionality(Locale.getDefault().displayName[0])
            return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
        }

        @JavascriptInterface
        fun getSceneNameToDisplay(): String? {
            if (scene != null) {
                return scene.name.trim()
            }
            return null
        }

        @JavascriptInterface
        fun getSpriteNameToDisplay(): String? {
            if (sprite != null) {
                return sprite.name.trim()
            }
            return null
        }

        @JavascriptInterface
        fun getScriptIndexToDisplay(): Int = script

        @SuppressLint
        @JavascriptInterface
        fun updateScriptPosition(strScriptId: String, x: String, y: String) {
            if (project == null) {
                return
            }

            val scriptId = UUID.fromString(strScriptId)
            val posX = x.toFloat()
            val posY = y.toFloat()

            for (scene in project.sceneList) {
                if (updateScriptPositionInScene(scriptId, posX, posY, scene)) {
                    return
                }
            }
        }

        private fun updateScriptPositionInScene(
            scriptId: UUID,
            x: Float,
            y: Float,
            scene: Scene
        ): Boolean {
            for (sprite in scene.spriteList) {
                if (updateScriptPositionInSprite(scriptId, x, y, sprite)) {
                    return true
                }
            }
            return false
        }

        private fun updateScriptPositionInSprite(
            scriptId: UUID,
            x: Float,
            y: Float,
            sprite: Sprite
        ): Boolean {
            for (script in sprite.scriptList) {
                if (script.scriptId == scriptId) {
                    script.posX = x
                    script.posY = y
                    return true
                }
            }
            return false
        }
    }
}
