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

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment
import org.koin.android.ext.android.inject

abstract class BaseFinderFragment<T : Nameable?> : RecyclerViewFragment<T>() {

    protected val projectManager: ProjectManager by inject()
    protected lateinit var currentProject: Project
    protected lateinit var currentScene: Scene
    protected lateinit var currentSprite: Sprite

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val parentView = super.onCreateView(inflater, container, savedInstanceState)
        activity = getActivity() as SpriteActivity?
        recyclerView = parentView!!.findViewById(R.id.recycler_view)
        currentProject = projectManager.currentProject
        currentScene = projectManager.currentlyEditedScene
        currentSprite = projectManager.currentSprite

        setupFinderListeners()

        if (FinderDataManager.instance.getInitiatingFragment() != FinderDataManager.FragmentType.NONE) {
            val sceneAndSpriteName = createActionBarTitle(1)
            finder?.onFragmentChanged(sceneAndSpriteName)
            scrollToSearchResult()
        }

        return parentView
    }

    private fun setupFinderListeners() {
        finder?.setOnResultFoundListener(object : Finder.OnResultFoundListener {
            override fun onResultFound(
                sceneIndex: Int,
                spriteIndex: Int,
                elementIndex: Int,
                type: FinderDataManager.FragmentType,
                textView: TextView?
            ) {
                currentProject = projectManager.currentProject
                currentScene = currentProject.sceneList[sceneIndex]
                FinderDataManager.instance.type = type

                if (type == FinderDataManager.FragmentType.SPRITE) {
                    textView?.text = createActionBarTitle(2)
                } else {
                    currentSprite = currentScene.spriteList[spriteIndex]
                    textView?.text = createActionBarTitle(1)
                }

                FinderDataManager.instance.currentMatchIndex = elementIndex

                when (type) {
                    FinderDataManager.FragmentType.SCENE -> activity.onBackPressed()

                    FinderDataManager.FragmentType.SPRITE -> {
                        projectManager.setCurrentlyEditedScene(currentScene)
                        activity.onBackPressed()
                    }

                    FinderDataManager.FragmentType.SCRIPT -> {
                        projectManager.setCurrentSceneAndSprite(
                            currentScene.name, currentSprite.name
                        )
                        activity.loadFragment(0)
                    }

                    FinderDataManager.FragmentType.LOOK -> {
                        projectManager.setCurrentSceneAndSprite(
                            currentScene.name, currentSprite.name
                        )
                        activity.loadFragment(1)
                    }

                    FinderDataManager.FragmentType.SOUND -> {
                        projectManager.setCurrentSceneAndSprite(
                            currentScene.name, currentSprite.name
                        )
                        activity.loadFragment(2)
                    }

                    FinderDataManager.FragmentType.NONE -> {
                    }
                }

                hideKeyboard()
            }
        })

        finder?.setOnCloseListener(object : Finder.OnCloseListener {
            override fun onClose() {
                finishActionMode()
                if (!activity.isFinishing) {
                    activity.setCurrentSceneAndSprite(
                        projectManager.currentlyEditedScene, projectManager.currentSprite
                    )
                    activity.supportActionBar?.title = createActionBarTitle(1)
                    activity.addTabs()
                }
                activity.findViewById<View>(R.id.toolbar).visibility = View.VISIBLE
            }
        })

        finder?.setOnOpenListener(object : Finder.OnOpenListener {
            override fun onOpen() {
                if (FinderDataManager.instance.getInitiatingFragment() == FinderDataManager.FragmentType.NONE) {
                    finder.setInitiatingFragment(getFragmentType())
                    setInitiatingPosition()
                }
                activity.removeTabs()
                activity.findViewById<View>(R.id.toolbar).visibility = View.GONE
            }
        })
    }

    fun createActionBarTitle(flag: Int): String {
        return if (flag == 1) {
            if (currentProject.sceneList != null && currentProject.sceneList.size == 1) {
                currentSprite.name
            } else {
                currentScene.name + ": " + currentSprite.name
            }
        } else {
            currentScene.name
        }
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    protected abstract fun getFragmentType(): FinderDataManager.FragmentType
    fun setInitiatingPosition() {
        val sceneIndex = ProjectManager.getInstance().currentProject.sceneList.indexOf(currentScene)
        val spriteIndex =
            ProjectManager.getInstance().currentlyEditedScene.spriteList.indexOf(ProjectManager.getInstance().currentSprite)

        finder.setInitiatingPosition(sceneIndex, spriteIndex, getFragmentType())
    }
}
