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
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.UserDefinedScript
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.content.bricks.EmptyEventBrick
import org.catrobat.catroid.content.bricks.FormulaBrick
import org.catrobat.catroid.content.bricks.ParameterizedBrick
import org.catrobat.catroid.content.bricks.ParameterizedEndBrick
import org.catrobat.catroid.content.bricks.ScriptBrick
import org.catrobat.catroid.content.bricks.UpdateableSpinnerBrick
import org.catrobat.catroid.content.bricks.UserDefinedBrick
import org.catrobat.catroid.content.bricks.UserDefinedReceiverBrick
import org.catrobat.catroid.content.bricks.brickspinner.EditOption
import org.catrobat.catroid.content.bricks.brickspinner.NewOption
import org.catrobat.catroid.content.bricks.brickspinner.SpinnerBrickUtils
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

    @VisibleForTesting
    private var webview: WebView? = null
    private var lastEditedFormulaBrick: FormulaBrick? = null
    private var allBricks: List<Brick>? = null

    companion object {
        val TAG: String = CatblocksScriptFragment::class.java.simpleName
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

        setHasOptionsMenu(true)

        val view = View.inflate(activity, R.layout.fragment_catblocks, null)
        val webView = view.findViewById<WebView>(R.id.catblocksWebView)
        initWebView(webView)
        this.webview = webView
        return view
    }

    @SuppressLint("VisibleForTests")
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && lastEditedFormulaBrick != null && webview != null) {
            var modifiedBrickId = lastEditedFormulaBrick!!.brickID
            if (lastEditedFormulaBrick is ParameterizedEndBrick) {
                modifiedBrickId = ((lastEditedFormulaBrick as ParameterizedEndBrick).parent as
                    Brick).brickID
            } else if (lastEditedFormulaBrick is ScriptBrick) {
                modifiedBrickId = (lastEditedFormulaBrick as ScriptBrick).script.scriptId
            }

            val updatedFormulaFields = BrickUpdateInfoHolder(modifiedBrickId, arrayListOf())

            for (fieldId in lastEditedFormulaBrick!!.formulaMap.keys) {
                val formula = lastEditedFormulaBrick!!.formulaMap[fieldId]
                val formulaValue = formula?.getTrimmedFormulaString(context) as String

                updatedFormulaFields.fields.add(BrickFieldUpdateInfoHolder(fieldId.toString(), formulaValue))
            }

            val updatedFormualFieldsJsonString = Gson().toJson(updatedFormulaFields)
            webview!!.evaluateJavascript("javascript:CatBlocks.updateBrickFields($updatedFormualFieldsJsonString);", null)
            allBricks = getAllBricks()
        }
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
        }
        catblocksWebView.loadUrl("https://appassets.androidplatform.net/assets/catblocks/index.html")
    }

    inner class SwitchTo1DHelper : Runnable, ValueCallback<String> {
        var brickToFocus: Brick? = null

        override fun run() {
            SettingsFragment.setUseCatBlocks(context, false)

            val scriptFragment: ScriptFragment = when (brickToFocus) {
                null -> ScriptFragment()
                is ScriptBrick -> ScriptFragment.newInstance((brickToFocus as ScriptBrick).script)
                else -> ScriptFragment.newInstance(brickToFocus)
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
        fun getCurrentLanguage(): String =
            Locale.getDefault().toString().replace("_", "-")

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
                if (projectManager?.currentSprite?.scriptList != null &&
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
        fun showFormulaEditor(clickedBrickIdStr: String, fieldName: String) {
            val clickedBrickId = UUID.fromString(clickedBrickIdStr)
            val foundBrick = projectManager.currentSprite.findBrickInSprite(clickedBrickId)

            if (foundBrick != null) {
                if (foundBrick is ParameterizedBrick) {
                    if (fieldName == "CATBLOCKS_ASSERT_LISTS_SELECTED") {
                        foundBrick.onClick(foundBrick.getView(requireContext()))
                        return
                    } else if (fieldName == "ASSERT_LOOP_ACTUAL") {
                        val brickField = Brick.BrickField.valueOf(fieldName)
                        lastEditedFormulaBrick = foundBrick.getEndBrick()
                        activity?.runOnUiThread {
                            foundBrick.getEndBrick().showCatblocksFormulaEditor(brickField, parentFragmentManager, activity)
                        }
                        return
                    }
                } else if (foundBrick is UserDefinedBrick) {
                    lastEditedFormulaBrick = foundBrick
                    activity?.runOnUiThread {
                        foundBrick.showCatblocksFormulaEditor(fieldName, parentFragmentManager, activity)
                    }
                    return
                }

                val brickField = Brick.BrickField.valueOf(fieldName)
                if (foundBrick is FormulaBrick) {
                    val formulaBrick = foundBrick as FormulaBrick
                    lastEditedFormulaBrick = formulaBrick
                    activity?.runOnUiThread {
                        formulaBrick.showCatblocksFormulaEditor(brickField, parentFragmentManager, activity)
                    }
                }
            }
        }

        @JavascriptInterface
        fun getSelectionValuesForBrick(brickType: String, strSpinnerViewId: String): String {
            val foundItems = arrayListOf<String>()
            val spinnerViewId = SpinnerBrickUtils.getSpinnerIdByIdName(strSpinnerViewId)
            val brick = getBrickByTypeName(brickType)
            if (brick != null) {
                var spinnerBrick = brick
                if (brick is ParameterizedBrick) {
                    spinnerBrick = brick.getEndBrick()
                }
                val availableSpinnerItems = SpinnerBrickUtils.getSpinnerItems(spinnerBrick, spinnerViewId, requireContext())
                for (spinnerItem in availableSpinnerItems) {
                    if (spinnerItem is NewOption || spinnerItem is EditOption) {
                        continue
                    }
                    if (spinnerItem is Nameable) {
                        foundItems.add(spinnerItem.name)
                    } else {
                        foundItems.add(spinnerItem.toString())
                    }
                }
            }
            return Gson().toJson(foundItems)
        }

        @JavascriptInterface
        fun updateSpinnerSelecion(strBrickId: String, strSpinnerViewId: String, selectedIndex: Int) {
            val brickId = UUID.fromString(strBrickId)
            val spinnerViewId = SpinnerBrickUtils.getSpinnerIdByIdName(strSpinnerViewId)
            val foundBrick = projectManager.currentSprite.findBrickInSprite(brickId)
            if (foundBrick !is UpdateableSpinnerBrick) {
                return
            }
            val availableSpinnerItems = SpinnerBrickUtils.getSpinnerItems(foundBrick, spinnerViewId, requireContext())
            var selectedIndexCorrected = selectedIndex
            if (availableSpinnerItems.isEmpty()) {
                return
            }
            for (availableItem in availableSpinnerItems) {
                if (availableItem is NewOption || availableItem is EditOption) {
                    selectedIndexCorrected++
                }
            }
            if (selectedIndexCorrected >= 0 && selectedIndexCorrected < availableSpinnerItems.size) {
                val selectedItem = availableSpinnerItems[selectedIndexCorrected]
                if (selectedItem is Nameable) {
                    foundBrick.updateSelectedItem(requireContext(), spinnerViewId, selectedItem.name, selectedIndexCorrected)
                } else {
                    foundBrick.updateSelectedItem(requireContext(), spinnerViewId, selectedItem.toString(), selectedIndexCorrected)
                }
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

    private fun getBrickByTypeName(brickType: String): Brick? {
        if (brickType == UserDefinedReceiverBrick::class.java.simpleName) {
            return UserDefinedReceiverBrick(UserDefinedScript(UUID.randomUUID()))
        }
        if (allBricks == null) {
            allBricks = getAllBricks()
        }
        for (brick in allBricks!!) {
            if (brick.javaClass.simpleName.equals(brickType)) {
                return brick
            }
        }
        return null
    }

    private fun getAllBricks(): List<Brick> = CategoryBricksFactory().getAllBricks(
        projectManager.currentSprite.isBackgroundSprite, requireContext())

    private fun getAvailableBrickCategories(): List<BrickCategoryInfoHolder> {
        val brickCategoryFactory = BrickCategoryListBuilder(requireActivity())
        val categoryNames = brickCategoryFactory.getCategoryNames()

        val brickCategoryInfos = arrayListOf<BrickCategoryInfoHolder>()

        for (categoryName in categoryNames) {
            brickCategoryInfos.add(BrickCategoryInfoHolder(categoryName))
        }

        return brickCategoryInfos
    }

    private data class BrickUpdateInfoHolder(val brickId: UUID, val fields: ArrayList<BrickFieldUpdateInfoHolder>)

    private data class BrickFieldUpdateInfoHolder(val fieldId: String, val value: String)

    private data class BrickCategoryInfoHolder(val name: String)

    private data class BrickInfoHolder(val brickId: String, val brickType: String)
}
