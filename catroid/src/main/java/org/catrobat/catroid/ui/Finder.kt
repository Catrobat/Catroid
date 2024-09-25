/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
package org.catrobat.catroid.ui

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.content.bricks.Brick
import org.catrobat.catroid.databinding.ViewScriptFinderBinding
import org.catrobat.catroid.ui.FinderDataManager.FragmentType
import org.catrobat.catroid.utils.ToastUtil
import org.koin.java.KoinJavaComponent.inject
import java.util.ArrayList
import java.util.Locale
import androidx.core.view.isVisible

class Finder(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    private var onResultFoundListener: OnResultFoundListener? = null
    private var onCloseListener: OnCloseListener? = null
    private var onOpenListener: OnOpenListener? = null
    private val projectManager: ProjectManager by inject(ProjectManager::class.java)
    private var binding: ViewScriptFinderBinding

    fun showNavigationButtons() {
        binding.find.visibility = GONE
        binding.findNext.visibility = VISIBLE
        binding.findPrevious.visibility = VISIBLE
        binding.searchPositionIndicator.visibility = VISIBLE
    }

    private fun hideNavigationButtons() {
        binding.findNext.visibility = GONE
        binding.findPrevious.visibility = GONE
        binding.searchPositionIndicator.visibility = GONE
        binding.find.visibility = VISIBLE
    }

    private fun formatSearchQuery(query: CharSequence): String =
        query.toString().trim().lowercase(Locale.ROOT)

    init {
        orientation = VERTICAL
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ViewScriptFinderBinding.inflate(inflater, this)

        binding.find.setOnClickListener { find() }
        binding.findNext.setOnClickListener { findNext() }
        binding.findPrevious.setOnClickListener { findPrevious() }
        binding.close.setOnClickListener { close() }

        val textWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(newText: CharSequence, start: Int, before: Int, count: Int) {
                if (FinderDataManager.instance.getSearchQuery() == formatSearchQuery(newText)) {
                    showNavigationButtons()
                } else {
                    hideNavigationButtons()
                }
            }

            override fun afterTextChanged(s: Editable) = Unit
        }
        binding.searchBar.addTextChangedListener(textWatcher)
        binding.searchBar.setOnEditorActionListener { _, actionId, keyEvent ->
            when (actionId) {
                EditorInfo.IME_ACTION_SEARCH, EditorInfo.IME_ACTION_DONE -> find()
                else -> if (keyEvent.action == KeyEvent.KEYCODE_ENTER) find()
            }
            false
        }
    }

    companion object {
        val TAG = Finder::class.java.simpleName

        fun collectBrickText(v: View?): String {
            val sb = StringBuilder()
            when (v) {
                is TextView -> sb.append(v.text.toString().lowercase(Locale.ROOT)).append(" ")
                is ViewGroup -> for (i in 0 until v.childCount) sb.append(collectBrickText(v.getChildAt(i)))
            }
            return sb.toString()
        }

        fun containsInOrder(fullText: String, searchQuery: String): Boolean {
            var remaining = searchQuery.trim()
            var text = fullText

            while (remaining.isNotEmpty()) {
                val nextSpace = remaining.indexOf(' ')
                val token = if (nextSpace == -1) remaining else remaining.substring(0, nextSpace)

                val foundIndex = text.indexOf(token)
                if (foundIndex == -1) {
                    return false
                }

                text = text.substring(foundIndex + token.length)
                remaining = if (nextSpace == -1) "" else remaining.substring(nextSpace + 1).trim()
            }
            return true
        }

        @Suppress("ComplexMethod", "TooGenericExceptionCaught")
        fun searchBrickViews(v: View?, searchQuery: String): Boolean {

            val fullText = collectBrickText(v)
            if (containsInOrder(fullText, searchQuery)) {
                FinderDataManager.instance.addtoSearchResultsNames(fullText.trim())
                return true
            }

            try {
                when (v) {
                    is Spinner -> {
                        val selectedItem = v.selectedItem
                        if (selectedItem is Nameable && selectedItem.name.lowercase(Locale.ROOT)
                                .contains(searchQuery)
                        ) {
                            FinderDataManager.instance.addtoSearchResultsNames(
                                selectedItem.name.lowercase(
                                    Locale.ROOT
                                )
                            )
                            return true
                        }
                    }

                    is ViewGroup -> {
                        for (i in 0 until v.childCount) {
                            val child = v.getChildAt(i)
                            val queryFoundInBrick = searchBrickViews(child, searchQuery)
                            if (queryFoundInBrick) return true
                        }
                    }

                    is TextView -> {
                        if (v.text.toString().lowercase(Locale.ROOT).contains(searchQuery)) {
                            FinderDataManager.instance.addtoSearchResultsNames(
                                v.text.toString().lowercase(Locale.ROOT)
                            )
                            return true
                        }
                    }
                }
            } catch (e: NullPointerException) {
                Log.e(TAG, Log.getStackTraceString(e))
            }
            return false
        }
    }

    private fun find() {
        val query = formatSearchQuery(binding.searchBar.text)
        if (query.isNotEmpty()) {
            if (FinderDataManager.instance.getSearchQuery() != query) {
                FinderDataManager.instance.setSearchQuery(query)
                binding.searchBar.setText(query)
                fillIndices(query)
                binding.find.visibility = GONE
                binding.findNext.visibility = VISIBLE
                binding.findPrevious.visibility = VISIBLE
            } else {
                findNext()
            }
        } else {
            ToastUtil.showError(
                context, context.getString(R.string.query_field_is_empty)
            )
        }
    }

    private fun findNext() {
        FinderDataManager.instance.getSearchResults().let {
            if (it.isNotEmpty()) {
                FinderDataManager.instance.setSearchResultIndex((FinderDataManager.instance.getSearchResultIndex() + 1) % it.size)
                updateUI()
            } else {
                binding.searchPositionIndicator.text = "0/0"
                ToastUtil.showError(context, context.getString(R.string.no_results_found))
            }
        }
    }

    private fun findPrevious() {
        FinderDataManager.instance.getSearchResults().let {
            if (it.isNotEmpty()) {
                FinderDataManager.instance.setSearchResultIndex(
                    if (FinderDataManager.instance.getSearchResultIndex() == 0) it.size - 1 else FinderDataManager.instance.getSearchResultIndex() - 1
                )
                updateUI()
            } else {
                binding.searchPositionIndicator.text = "0/0"
                ToastUtil.showError(context, context.getString(R.string.no_results_found))
            }
        }
    }

    fun onFragmentChanged(sceneAndSpriteName: String) {
        openForChangeFragment()

        binding.searchPositionIndicator.text = String.format(
            Locale.ROOT,
            "%d/%d",
            FinderDataManager.instance.getSearchResultIndex() + 1,
            FinderDataManager.instance.getSearchResults().size
        )
        binding.sceneAndSpriteName.text = sceneAndSpriteName
    }

    private fun updateUI() {
        val result = FinderDataManager.instance.getSearchResults()
            .get(FinderDataManager.instance.getSearchResultIndex())
        binding.searchPositionIndicator.text = String.format(
            Locale.ROOT,
            "%d/%d",
            FinderDataManager.instance.getSearchResultIndex() + 1,
            FinderDataManager.instance.getSearchResults().size
        )
        onResultFoundListener?.onResultFound(
            result.sceneIndex,
            result.spriteIndex,
            result.elementIndex,
            result.fragmentType,
            binding.sceneAndSpriteName
        )
    }

    fun fillIndices(query: String) {
        FinderDataManager.instance.setSearchResultIndex(-1)
        val activeScene = projectManager.currentlyEditedScene
        val activeSprite: Sprite? = projectManager.currentSprite

        FinderDataManager.instance.clearSearchResults()
        FinderDataManager.instance.clearSearchResultsNames()
        startThreadToFillIndices(query, activeScene, activeSprite)
    }

    private fun startThreadToFillIndices(query: String, activeScene: Scene, activeSprite: Sprite?) {
        Thread {
            FinderDataManager.instance.idlingResource.increment()
            val activity = context as Activity
            if (!activity.isFinishing) {
                activity.runOnUiThread {
                    binding.find.visibility = GONE
                    binding.findNext.visibility = GONE
                    binding.findPrevious.visibility = GONE
                    binding.progressBar.visibility = VISIBLE
                }
            }
            val scenes = projectManager.currentProject.sceneList

            for (i in scenes.indices) {
                val scene = scenes[i]

                if (scene.name.lowercase(Locale.ROOT).contains(query) && scenes.size > 1) {
                    val exists = FinderDataManager.instance.getSearchResults().any { res ->
                        res.sceneIndex == i && res.spriteIndex == i && res.elementIndex == i && res.fragmentType == FragmentType.SCENE
                    }
                    if (!exists) {
                        FinderDataManager.instance.addtoSearchResults(
                            i, i, i, FragmentType.SCENE
                        )
                        FinderDataManager.instance.addtoSearchResultsNames(
                            scene.name.lowercase(
                                Locale.ROOT
                            )
                        )
                        val currentFind = arrayOf(
                            -1, -1, FragmentType.SCENE
                        )
                        if (FinderDataManager.instance.getInitiatingPosition()
                                .contentEquals(currentFind) && !FinderDataManager.instance.startingIndexSet
                        ) {
                            FinderDataManager.instance.setSearchResultIndex(
                                (FinderDataManager.instance.getSearchResults().size - 2)
                            )
                            FinderDataManager.instance.startingIndexSet = true
                        }
                    }
                }

                val spriteList = scene.spriteList
                for (j in spriteList.indices) {
                    val sprite = spriteList[j]
                    val scriptList = sprite.scriptList
                    val bricks: List<Brick> = ArrayList()
                    projectManager.setCurrentSceneAndSprite(
                        scene.name, sprite.name
                    )
                    for (script in scriptList) {
                        script.setParents()
                        script.addToFlatList(bricks)
                    }

                    if (sprite.name.lowercase(Locale.ROOT).contains(query)) {
                        FinderDataManager.instance.addtoSearchResults(
                            i, j, j, FragmentType.SPRITE
                        )
                        FinderDataManager.instance.addtoSearchResultsNames(
                            sprite.name.lowercase(
                                Locale.ROOT
                            )
                        )
                        val currentFind = arrayOf(
                            i, -1, FragmentType.SPRITE
                        )
                        if (FinderDataManager.instance.getInitiatingPosition()
                                .contentEquals(currentFind) && !FinderDataManager.instance.startingIndexSet
                        ) {
                            FinderDataManager.instance.setSearchResultIndex(
                                (FinderDataManager.instance.getSearchResults().size - 2)
                            )
                            FinderDataManager.instance.startingIndexSet = true
                        }
                    }

                    for (k in bricks.indices) {
                        val brick = bricks[k]
                        if (searchBrickViews(brick.getView(context), query)) {
                            FinderDataManager.instance.addtoSearchResults(
                                i, j, k, FragmentType.SCRIPT
                            )
                            val currentFind = arrayOf(
                                i, j, FragmentType.SCRIPT
                            )
                            if (FinderDataManager.instance.getInitiatingPosition()
                                    .contentEquals(currentFind) && !FinderDataManager.instance.startingIndexSet
                            ) {
                                FinderDataManager.instance.setSearchResultIndex(
                                    (FinderDataManager.instance.getSearchResults().size) - 2
                                )
                                FinderDataManager.instance.startingIndexSet = true
                            }
                        }
                    }

                    val lookList = sprite.lookList
                    for (k in lookList.indices) {
                        val look = lookList[k]
                        if (look.name.lowercase(Locale.ROOT).contains(query)) {
                            FinderDataManager.instance.addtoSearchResults(
                                i, j, k, FragmentType.LOOK
                            )
                            FinderDataManager.instance.addtoSearchResultsNames(
                                look.name.lowercase(
                                    Locale.ROOT
                                )
                            )
                            val currentFind = arrayOf(
                                i, j, FragmentType.LOOK
                            )
                            if (FinderDataManager.instance.getInitiatingPosition()
                                    .contentEquals(currentFind) && !FinderDataManager.instance.startingIndexSet
                            ) {
                                FinderDataManager.instance.setSearchResultIndex(
                                    (FinderDataManager.instance.getSearchResults().size) - 2
                                )
                                FinderDataManager.instance.startingIndexSet = true
                            }
                        }
                    }

                    val soundList = sprite.soundList
                    for (k in soundList.indices) {
                        val sound = soundList[k]
                        if (sound.name.lowercase(Locale.ROOT).contains(query)) {
                            FinderDataManager.instance.addtoSearchResults(
                                i, j, k, FragmentType.SOUND
                            )
                            FinderDataManager.instance.addtoSearchResultsNames(
                                sound.name.lowercase(
                                    Locale.ROOT
                                )
                            )
                            val currentFind = arrayOf(
                                i, j, FragmentType.SOUND
                            )
                            if (FinderDataManager.instance.getInitiatingPosition()
                                    .contentEquals(currentFind) && !FinderDataManager.instance.startingIndexSet
                            ) {
                                FinderDataManager.instance.setSearchResultIndex(
                                    (FinderDataManager.instance.getSearchResults().size) - 2
                                )
                                FinderDataManager.instance.startingIndexSet = true
                            }
                        }
                    }
                }
            }

            if (!activity.isFinishing) {
                activity.runOnUiThread {
                    binding.findNext.visibility = VISIBLE
                    binding.findPrevious.visibility = VISIBLE
                    binding.searchPositionIndicator.visibility = VISIBLE
                    binding.progressBar.visibility = GONE
                    if (activeSprite != null) {
                        projectManager.setCurrentSceneAndSprite(
                            activeScene.name, activeSprite.name
                        )
                    }
                    findNext()
                    FinderDataManager.instance.idlingResource.decrement()
                }
            }
        }.start()
    }

    val isOpen: Boolean
        get() = isVisible

    fun setInitiatingFragment(fragmentEnum: FragmentType) {
        FinderDataManager.instance.setInitiatingFragment(fragmentEnum)
    }

    fun setInitiatingPosition(sceneIndex: Int, spriteIndex: Int, fragmentType: FragmentType) {
        FinderDataManager.instance.setInitiatingPosition(sceneIndex, spriteIndex, fragmentType)
    }

    fun open() {
        this.visibility = VISIBLE
        binding.searchBar.isFocusable
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.toggleSoftInputFromWindow(
            binding.searchBar.applicationWindowToken, InputMethodManager.SHOW_FORCED, 0
        )
        onOpenListener?.onOpen()
        binding.searchBar.requestFocus()
    }

    fun disableFocusSearchBar() {
        binding.searchBar.isFocusable = false
    }

    private fun openForChangeFragment() {
        this.visibility = VISIBLE
        showNavigationButtons()
        onOpenListener?.onOpen()
        binding.searchBar.setText(FinderDataManager.instance.getSearchQuery())
        binding.searchBar.isFocusable = false
    }

    fun close() {
        this.visibility = GONE
        FinderDataManager.instance.clearSearchResults()
        FinderDataManager.instance.clearSearchResultsNames()
        binding.searchBar.text.clear()
        binding.searchBar.isFocusableInTouchMode = true
        FinderDataManager.instance.setSearchQuery(null)
        FinderDataManager.instance.setInitiatingFragment(FragmentType.NONE)
        FinderDataManager.instance.type = FragmentType.NONE
        FinderDataManager.instance.currentMatchIndex = -1
        FinderDataManager.instance.startingIndexSet = false
        onCloseListener?.onClose()
        hideNavigationButtons()
        this.hideKeyboard()
    }

    val isClosed: Boolean
        get() = visibility == GONE

    fun setOnResultFoundListener(onResultFoundListener: OnResultFoundListener?) {
        this.onResultFoundListener = onResultFoundListener
    }

    fun setOnCloseListener(onCloseListener: OnCloseListener?) {
        this.onCloseListener = onCloseListener
    }

    fun setOnOpenListener(onOpenListener: OnOpenListener?) {
        this.onOpenListener = onOpenListener
    }

    interface OnResultFoundListener {
        fun onResultFound(
            sceneIndex: Int,
            spriteIndex: Int,
            elementIndex: Int,
            type: FragmentType,
            textView: TextView?
        )
    }

    interface OnCloseListener {
        fun onClose()
    }

    interface OnOpenListener {
        fun onOpen()
    }
}
