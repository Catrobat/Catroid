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
import org.catrobat.catroid.content.Project
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.Sprite
import org.catrobat.catroid.databinding.ActivityRecyclerBinding
import org.catrobat.catroid.ui.BaseActivity
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.RecyclerViewFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SceneListFragment
import org.catrobat.catroid.ui.recyclerview.fragment.SpriteListFragment
import org.catrobat.catroid.utils.ToastUtil

class ImportLocalObjectActivity : BaseActivity() {
    private lateinit var binding: ActivityRecyclerBinding
    private lateinit var listFragment: RecyclerViewFragment<*>
    private var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        if (isFinishing) {
            return
        }
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        BottomBar.hideBottomBar(this)
        setTypeFromIntent()
        loadSelector(type)
    }

    private fun setTypeFromIntent() {
        if (intent.hasExtra(TAG) && type == null) {
            type = intent.extras?.getString(TAG)!!
        }
    }

    override fun onResume() {
        super.onResume()
        BottomBar.hideBottomBar(this)
        setTypeFromIntent()
        loadSelector(type)
    }

    fun loadSelector(type: String?) {
        when (type) {
            REQUEST_PROJECT -> {
                this.type = type
                supportActionBar?.apply {
                    setTitle(R.string.import_from_project)
                    setDisplayHomeAsUpEnabled(true)
                }
                listFragment = ProjectListFragment()
                loadFragment(ProjectListFragment.TAG)
            }
            REQUEST_SCENE -> {
                this.type = type
                supportActionBar?.apply {
                    setTitle(R.string.import_from_scene)
                    setDisplayHomeAsUpEnabled(true)
                }
                listFragment = SceneListFragment()
                loadFragment(SceneListFragment.TAG)
            }
            REQUEST_SPRITE -> {
                this.type = type
                supportActionBar?.apply {
                    setTitle(R.string.import_object)
                    setDisplayHomeAsUpEnabled(true)
                }
                listFragment = SpriteListFragment()
                loadFragment(SpriteListFragment.TAG)
            }
            else -> ToastUtil.showError(applicationContext, R.string.reject_import)
        }
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
        when (type) {
            REQUEST_SPRITE -> {
                loadSelector(REQUEST_SCENE)
                return
            }
            REQUEST_SCENE -> {
                loadSelector(REQUEST_PROJECT)
                return
            }
        }
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    fun finishImport() {
        val intent = Intent()
        intent.putExtra(REQUEST_PROJECT, projectToImportFrom.directory?.absoluteFile)
        intent.putExtra(REQUEST_SCENE, sceneToImportFrom.name)
        intent.putExtra(REQUEST_SPRITE, spriteToImport.name)
        setResult(RESULT_OK, intent)
        finish()
    }

    companion object {
        lateinit var projectToImportFrom: Project
        lateinit var sceneToImportFrom: Scene
        lateinit var spriteToImport: Sprite

        const val REQUEST_PROJECT = "projectUri"
        const val REQUEST_SCENE = "sceneName"
        const val REQUEST_SPRITE = "spriteName"
        val TAG: String = ImportLocalObjectActivity::class.java.simpleName
    }
}
