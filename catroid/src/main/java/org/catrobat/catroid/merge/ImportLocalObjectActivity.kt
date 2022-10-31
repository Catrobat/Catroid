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

package org.catrobat.catroid.merge

import android.content.Intent
import android.os.Bundle
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.GroupSprite
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.databinding.ActivityRecyclerBinding
import org.catrobat.catroid.ui.BaseActivity
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SceneListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment

class ImportLocalObjectActivity : BaseActivity() {
    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var listFragment: RecyclerViewFragment<*>
    private val request: String
        get() = intent.getStringExtra(Constants.EXTRA_IMPORT_REQUEST_CODE) ?: REQUEST_SPRITE
    private var currentFragmentType: String = REQUEST_PROJECT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        if (isFinishing) {
            return
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        BottomBar.hideBottomBar(this)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadSelector(currentFragmentType)
    }

    override fun onResume() {
        super.onResume()
        BottomBar.hideBottomBar(this)
        loadSelector(currentFragmentType)
    }

    fun loadSelector(type: String) {
        this.currentFragmentType = type
        listFragment = when (type) {
            REQUEST_PROJECT -> ProjectListFragment()
            REQUEST_SCENE -> SceneListFragment()
            REQUEST_SPRITE -> SpriteListFragment()
            else -> throw IllegalStateException(TAG + R.string.reject_import)
        }
        loadFragment(listFragment.javaClass.simpleName)
    }

    private fun loadFragment(tag: String) {
        intent?.apply {
            if (action != null) {
                val data = Bundle()
                data.putParcelable("intent", intent)
                listFragment.arguments = data
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, listFragment, tag)
            .commit()
    }

    override fun onBackPressed() {
        when (currentFragmentType) {
            REQUEST_SPRITE -> if (projectToImportFrom?.hasMultipleScenes() == true) {
                loadSelector(REQUEST_SCENE)
            } else {
                loadSelector(REQUEST_PROJECT)
            }
            REQUEST_SCENE -> loadSelector(REQUEST_PROJECT)
            REQUEST_PROJECT -> finish()
        }
    }

    fun loadNext() {
        when (currentFragmentType) {
            REQUEST_PROJECT -> if (projectToImportFrom?.hasMultipleScenes() == true) {
                loadSelector(REQUEST_SCENE)
            } else {
                sceneToImportFrom = projectToImportFrom?.defaultScene
                when (request) {
                    REQUEST_SPRITE -> loadSelector(REQUEST_SPRITE)
                    REQUEST_SCENE -> finish()
                }
            }
            REQUEST_SCENE ->
                when (request) {
                REQUEST_SPRITE -> loadSelector(REQUEST_SPRITE)
                REQUEST_SCENE -> finish()
            }
        }
    }

    override fun finish() {
        val intent = Intent()
        if (request == REQUEST_SCENE) {
            sceneToImportFrom?.spriteList?.forEach { sprite ->
                if (sprite is GroupSprite) {
                    groupSpritesToImport.add(sprite.name)
                } else {
                    spritesToImport.add(sprite.name)
                }
            }
        }
        if (projectToImportFrom != null && sceneToImportFrom != null) {
            intent.putExtra(
                Constants.EXTRA_PROJECT_PATH,
                projectToImportFrom?.directory?.absoluteFile
            )
            intent.putExtra(Constants.EXTRA_SCENE_NAME, sceneToImportFrom?.name)
            intent.putExtra(Constants.EXTRA_SPRITE_NAMES, spritesToImport)
            intent.putExtra(Constants.EXTRA_GROUP_SPRITE_NAMES, groupSpritesToImport)
            setResult(RESULT_OK, intent)
        } else {
            setResult(RESULT_CANCELED)
        }
        super.finish()
    }

    companion object {
        var projectToImportFrom: Project? = null
        var sceneToImportFrom: Scene? = null
        var spritesToImport: ArrayList<String> = ArrayList()
        var groupSpritesToImport: ArrayList<String> = ArrayList()
        var backPressedInActionMode: Boolean = false

        val REQUEST_PROJECT = ProjectListFragment.TAG
        val REQUEST_SCENE = SceneListFragment.TAG
        val REQUEST_SPRITE = SpriteListFragment.TAG
        val TAG: String = ImportLocalObjectActivity::class.java.simpleName
    }
}
