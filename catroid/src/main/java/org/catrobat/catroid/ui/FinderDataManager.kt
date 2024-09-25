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

import androidx.test.espresso.idling.CountingIdlingResource

class FinderDataManager {
    companion object {
        val instance: FinderDataManager by lazy {
            FinderDataManager()
        }
    }

    enum class FragmentType(val id: Int) {
        NONE(0), SCENE(1), SPRITE(2), SCRIPT(3), LOOK(4), SOUND(5)
    }

    data class FinderObject(
        var sceneIndex: Int,
        var spriteIndex: Int,
        var elementIndex: Int,
        var fragmentType: FragmentType,
    )

    private var initiatingFragmentInfo = FinderObject(-1, -1, -1, FragmentType.NONE)
    private var searchResults = mutableListOf<FinderObject>()
    private val searchResultsNames = mutableListOf<String>()
    private var searchResultIndex = -1
    private var searchQuery: String? = null
    var currentMatchIndex = -1
    var type = FragmentType.NONE
    var startingIndexSet = false
    var idlingResource = CountingIdlingResource("SearchFinder")

    fun getInitiatingPosition(): Array<Any> {
        return arrayOf(
            initiatingFragmentInfo.sceneIndex, initiatingFragmentInfo.spriteIndex,
            initiatingFragmentInfo.fragmentType
        )
    }

    fun setInitiatingPosition(sceneIndex: Int, spriteIndex: Int, fragmentType: FragmentType) {
        initiatingFragmentInfo.sceneIndex = sceneIndex
        initiatingFragmentInfo.spriteIndex = spriteIndex
        initiatingFragmentInfo.fragmentType = fragmentType
    }

    fun setSearchQuery(searchquery: String?) {
        searchQuery = searchquery
    }

    fun getSearchQuery(): String? {
        return searchQuery
    }

    fun setSearchResultIndex(searchresultIndex: Int) {
        searchResultIndex = searchresultIndex
    }

    fun getSearchResultIndex(): Int {
        return searchResultIndex
    }

    fun addtoSearchResults(
        sceneIndex: Int,
        spriteIndex: Int,
        elementIndex: Int,
        fragmentType: FragmentType
    ) {
        searchResults.add(FinderObject(sceneIndex, spriteIndex, elementIndex, fragmentType))
    }

    fun addtoSearchResultsNames(item: String) {
        searchResultsNames.add(item)
    }

    fun getInitiatingFragment(): FragmentType {
        return initiatingFragmentInfo.fragmentType
    }

    fun setInitiatingFragment(initiatingFragment: FragmentType) {
        initiatingFragmentInfo.fragmentType = initiatingFragment
    }

    fun clearSearchResults() {
        searchResults.clear()
    }

    fun getSearchResults(): MutableList<FinderObject> {
        return searchResults
    }

    fun clearSearchResultsNames() {
        searchResultsNames.clear()
    }

    fun getSearchResultsNames(): MutableList<String> {
        return searchResultsNames
    }
}