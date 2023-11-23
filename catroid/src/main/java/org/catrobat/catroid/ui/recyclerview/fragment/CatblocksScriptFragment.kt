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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import androidx.webkit.WebViewAssetLoader
import com.google.gson.Gson
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.EmptyEventBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.io.XstreamSerializer
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.controller.RecentBrickListManager
import org.catrobat.catroid.ui.fragment.BrickCategoryListBuilder
import org.catrobat.catroid.ui.fragment.CategoryBricksFactory
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.json.JSONArray
import org.koin.java.KoinJavaComponent.inject
import java.util.Locale
import java.util.UUID

class CatblocksScriptFragment(
    private val brickAtTopID: UUID?
) : Fragment() {

    private var webview: WebView? = null

    companion object {
        val TAG: String = CatblocksScriptFragment::class.java.simpleName
        @VisibleForTesting
        var testingMode = false
    }

    private val projectManager = inject(ProjectManager::class.java).value

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.backpack).isVisible = false
        menu.findItem(R.id.copy).isVisible = false
        menu.findItem(R.id.delete).isVisible = false
        menu.findItem(R.id.rename).isVisible = false
        menu.findItem(R.id.show_details).isVisible = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.catblocks) {
            webview!!.evaluateJavascript("javascript:CatBlocks.getBrickAtTopOfScreen();",
                SwitchTo1DHelper())
            return true
        } else if (item.itemId == R.id.catblocks_reorder_scripts) {
            webview!!.evaluateJavascript("javascript:CatBlocks.reorderCurrentScripts();", null)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        BottomBar.showBottomBar(activity)

        if (BuildConfig.FEATURE_AI_ASSIST_ENABLED) {
            BottomBar.showAiAssistButton(activity)
        }

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
            JSInterface(), "Android"
        )

        catblocksWebView.webViewClient = object : WebViewClient() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest
            ): WebResourceResponse? {
                return assetLoader.shouldInterceptRequest(request.url)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                if (testingMode) {
                    view?.evaluateJavascript("""
                        javascript:(function(){
                            console.log("Load webViewUtilsFunctions.js");
                            const scriptElement = document.createElement("script");
                            scriptElement.src = "https://appassets.androidplatform.net/assets/catblocks/webViewUtilsFunctions.js";
                            document.head.appendChild(scriptElement);
                        })()
                    """.trimIndent(), null)
                }

                view?.evaluateJavascript(
                    """javascript:(async function(){
                        await CatBlocks.init({
                           container: 'catroid-catblocks-container',
                           renderSize: 0.75,
                           language: Android.getCurrentLanguage(),
                           rtl: Android.isRTL(),
                           shareRoot: 'https://appassets.androidplatform.net/assets/catblocks',
                           media: 'https://appassets.androidplatform.net/assets/catblocks/media',
                           i18n: 'https://appassets.androidplatform.net/assets/catblocks/i18n',
                           noImageFound: 'No_Image_Available.jpg',
                           renderLooks: false,
                           renderSounds: false,
                           readOnly: false
                        });
                        
                        const programXML = Android.getCurrentProject();
                        const scene = Android.getSceneNameToDisplay();
                        const object = Android.getSpriteNameToDisplay();
                        CatBlocks.render(programXML, scene, object);
                        ${
                            if (testingMode) {
                                "if (window.webViewUtils) window.webViewUtils" +
                                    ".onPageLoaded();"
                            } else {
                                ""
                            }
                        }
                    })()
                """.trimMargin(), null
                )
            }
        }

        if (!testingMode) {
            catblocksWebView.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html")
        }
    }

    inner class SwitchTo1DHelper : Runnable, ValueCallback<String> {
        var brickToFocus: Brick? = null

        override fun run() {
            SettingsFragment.setUseCatBlocks(context, false)
            val scriptFragment: ScriptFragment = if (brickToFocus == null) {
                ScriptFragment()
            } else if (brickToFocus is ScriptBrick) {
                ScriptFragment.newInstance((brickToFocus as ScriptBrick).script)
            } else {
                ScriptFragment.newInstance(brickToFocus)
            }
            val fragmentTransaction = parentFragmentManager.beginTransaction()
            fragmentTransaction.replace(
                R.id.fragment_container, scriptFragment,
                ScriptFragment.TAG
            )
            fragmentTransaction.commit()
        }

        override fun onReceiveValue(strBrickToFocusId: String?) {
            if (strBrickToFocusId != null) {
                val strBrickId = strBrickToFocusId.trim('"')
                if (strBrickId.isNotEmpty()) {
                    try {
                        val brickId = UUID.fromString(strBrickId)
                        brickToFocus = projectManager.currentSprite.findBrickInSprite(brickId)
                    } catch (exception: IllegalArgumentException) {
                        println(exception.message)
                    }
                }
            }
            activity?.runOnUiThread(this)
        }
    }

    inner class JSInterface {

        @JavascriptInterface
        fun getCurrentProject(): String {
            val projectXml = XstreamSerializer.getInstance()
                .getXmlAsStringFromProject(projectManager.currentProject)
            return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n$projectXml"
        }

        @JavascriptInterface
        fun getCurrentLanguage(): String = Locale.getDefault().toString().replace("_", "-")

        @JavascriptInterface
        fun isRTL(): Boolean {
            val directionality = Character.getDirectionality(Locale.getDefault().displayName[0])
            return directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT ||
                directionality == Character.DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC
        }

        @JavascriptInterface
        fun getSceneNameToDisplay(): String? = projectManager.currentlyEditedScene?.name?.trim()

        @JavascriptInterface
        fun getSpriteNameToDisplay(): String? = projectManager.currentSprite?.name?.trim()

        @JavascriptInterface
        fun getBrickIDToFocus(): String? {
            if (brickAtTopID != null) {
                return brickAtTopID.toString()
            } else {
                if (projectManager.currentSprite?.scriptList != null &&
                    projectManager.currentSprite.scriptList.any()) {
                        return projectManager.currentSprite.scriptList[0].scriptId.toString()
                }
            }
            return null
        }

        @SuppressLint
        @JavascriptInterface
        fun updateScriptPosition(strScriptId: String, x: String, y: String) {
            if (projectManager.currentSprite == null) {
                return
            }

            val scriptId = UUID.fromString(strScriptId)
            val posX: Float = x.toFloat()
            val posY: Float = y.toFloat()

            for (script in projectManager.currentSprite.scriptList) {
                if (script.scriptId == scriptId) {
                    script.posX = posX
                    script.posY = posY
                    break
                }
            }
        }

        @JavascriptInterface
        fun moveBricksToEmptyScriptBrick(brickStrIdsToMove: Array<String>): String {
            val emptyBrick = EmptyEventBrick()

            val brickIdsToMove = mutableListOf<UUID>()
            for (strId in brickStrIdsToMove) {
                brickIdsToMove.add(UUID.fromString(strId))
            }

            for (script in projectManager.currentSprite.scriptList) {

                val foundBricks = script
                    .removeBricksFromScript(brickIdsToMove)

                if (foundBricks != null) {
                    emptyBrick.script.brickList.addAll(foundBricks)
                    break
                }
            }

            projectManager.currentSprite.scriptList.add(emptyBrick.script)
            return emptyBrick.script.scriptId.toString()
        }

        @JavascriptInterface
        fun moveBricks(
            newParentStrId: String,
            parentSubStackIndex: Int,
            brickStrIdsToMove: Array<String>
        ): Boolean {
            val brickIdsToMove = mutableListOf<UUID>()
            for (strId in brickStrIdsToMove) {
                brickIdsToMove.add(UUID.fromString(strId))
            }

            val newParentId = UUID.fromString(newParentStrId)

            var bricksToMove: List<Brick>? = null
            for (script in projectManager.currentSprite.scriptList) {
                bricksToMove = script.removeBricksFromScript(brickIdsToMove)
                if (bricksToMove != null) {
                    break
                }
            }

            if (bricksToMove == null) {
                return false
            }

            for (script in projectManager.currentSprite.scriptList) {
                if (script.insertBrickAfter(newParentId, parentSubStackIndex, bricksToMove)) {
                    return true
                }
            }

            return false
        }

        @JavascriptInterface
        fun removeEmptyScriptBricks(): String {
            val removed = projectManager.currentSprite.removeAllEmptyScriptBricks()
            return JSONArray(removed).toString()
        }

        @JavascriptInterface
        fun switchTo1D(strClickedBrickId: String) {
            val brickId = UUID.fromString(strClickedBrickId)

            val foundBrick = projectManager.currentSprite.findBrickInSprite(brickId)

            if (foundBrick != null) {
                val switchTo1DHelper = SwitchTo1DHelper()
                switchTo1DHelper.brickToFocus = foundBrick
                activity?.runOnUiThread(switchTo1DHelper)
            }
        }

        @JavascriptInterface
        fun removeBricks(brickStrIdsToRemove: Array<String>) {
            val brickIdsToRemove = arrayListOf<UUID>()
            for (strId in brickStrIdsToRemove) {
                brickIdsToRemove.add(UUID.fromString(strId))
            }

            var done = false
            for (script in projectManager.currentSprite.scriptList) {
                if (brickIdsToRemove.contains(script.scriptId)) {
                    projectManager.currentSprite.scriptList.remove(script)
                    done = true
                } else if (script.removeBricksFromScript(brickIdsToRemove) != null) {
                    done = true
                }

                if (done) {
                    break
                }
            }
        }

        @JavascriptInterface
        fun duplicateBrick(brickStrIdToClone: String): String? {
            val brickIdToClone = UUID.fromString(brickStrIdToClone)

            val foundBrick = projectManager.currentSprite.findBrickInSprite(brickIdToClone)
                ?: return ""

            if (foundBrick is ScriptBrick) {
                val clone = foundBrick.script?.clone() ?: return null
                projectManager.currentSprite.scriptList.add(clone)
                return clone.scriptId.toString()
            } else {
                val clone = foundBrick.clone()
                val emptyBrick = EmptyEventBrick()
                emptyBrick.script.brickList.add(clone)
                projectManager.currentSprite.scriptList.add(emptyBrick.script)
                return emptyBrick.script.scriptId.toString()
            }
        }

        @JavascriptInterface
        fun getBricksForCategory(category: String): String {
            val bricksForCategory = CategoryBricksFactory().getBricks(category, projectManager
                .currentSprite.isBackgroundSprite, requireContext())

            val brickInfos = arrayListOf<BrickInfoHolder>()

            for (brick in bricksForCategory) {
                val brickType = brick.javaClass.simpleName
                brickInfos.add(BrickInfoHolder(brickType, brickType))
            }

            return Gson().toJson(brickInfos)
        }

        @JavascriptInterface
        fun addBrickByName(categoryName: String, brickName: String): String {

            val bricksOfCategory = CategoryBricksFactory().getBricks(categoryName, projectManager
                .currentSprite.isBackgroundSprite, requireContext())

            val foundBricks = bricksOfCategory.filter { it.javaClass.simpleName.equals(brickName) }

            val addedBricks = arrayListOf<BrickInfoHolder>()

            if (foundBricks.size == 1) {
                val brick = foundBricks[0].clone()
                if (brick is ScriptBrick) {
                    projectManager.currentSprite.scriptList.add(brick.script)

                    addedBricks.add(BrickInfoHolder(brick.script.scriptId.toString(), brick
                        .script.javaClass.simpleName))
                } else {
                    val emptyBrick = EmptyEventBrick()
                    emptyBrick.script.brickList.add(brick)
                    projectManager.currentSprite.scriptList.add(emptyBrick.script)

                    addedBricks.add(BrickInfoHolder(emptyBrick.script.scriptId.toString(),
                                                    emptyBrick.script.javaClass.simpleName))

                    addedBricks.add(BrickInfoHolder(brick.brickID.toString(),
                                                    brick.javaClass.simpleName))
                }

                try {
                    if (brick.javaClass != UserDefinedReceiverBrick::class.java &&
                        brick.javaClass != UserDefinedBrick::class.java) {
                        RecentBrickListManager.getInstance().addBrick(brick.clone())
                    }
                } catch (e: CloneNotSupportedException) {
                    Log.e(ScriptFragment.TAG, Log.getStackTraceString(e))
                }
            }
            return Gson().toJson(addedBricks)
        }

        @JavascriptInterface
        fun getBrickCategoryInfos(): String {
            val brickCategoryInfos = getAvailableBrickCategories()

            return Gson().toJson(brickCategoryInfos)
        }
    }

    fun handleAddButton() {

        val brickCategoryInfos = getAvailableBrickCategories()
        val jsonCategoryInfos = Gson().toJson(brickCategoryInfos)

        webview!!.evaluateJavascript(
            "javascript:CatBlocks.showBrickCategories($jsonCategoryInfos);", null)
    }

    private fun getAvailableBrickCategories(): List<BrickCategoryInfoHolder> {
        val brickCategoryFactory = BrickCategoryListBuilder(requireActivity())
        val categoryNames = brickCategoryFactory.getCategoryNames()

        val brickCategoryInfos = arrayListOf<BrickCategoryInfoHolder>()

        for (categoryName in categoryNames) {
            brickCategoryInfos.add(BrickCategoryInfoHolder(categoryName))
        }

        return brickCategoryInfos
    }

    private data class BrickCategoryInfoHolder(val name: String)

    data class BrickInfoHolder(val brickId: String, val brickType: String)
}
