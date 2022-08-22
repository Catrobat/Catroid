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
import androidx.fragment.app.FragmentActivity
import org.catrobat.catroid.R
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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
        this.type = type
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
        when (type) {
            REQUEST_SPRITE ->
                if (projectToImportFrom?.hasMultipleScenes() == true) {
                    loadSelector(REQUEST_SCENE)
                } else {
                    loadSelector(REQUEST_PROJECT)
                }
            REQUEST_SCENE -> loadSelector(REQUEST_PROJECT)
            REQUEST_PROJECT -> finish()
            else -> throw java.lang.IllegalStateException(
                TAG + "Access press Back in illegal " +
                    "state"
            )
        }
    }

    fun loadNext(type: String) {
        return when (type) {
            REQUEST_PROJECT ->
                if (projectToImportFrom?.hasMultipleScenes() == true) {
                    loadSelector(REQUEST_SCENE)
                } else {
                    sceneToImportFrom = projectToImportFrom?.defaultScene
                    loadSelector(REQUEST_SPRITE)
                }
            REQUEST_SCENE -> loadSelector(REQUEST_SPRITE)
            else -> throw java.lang.IllegalStateException(
                TAG + "Other Types can't navigate to " +
                    "next Fragments"
            )
        }
    }

    override fun finish() {
        val intent = Intent()
        if (projectToImportFrom != null && sceneToImportFrom != null) {
            intent.putExtra(REQUEST_PROJECT, projectToImportFrom?.directory?.absoluteFile)
            intent.putExtra(REQUEST_SCENE, sceneToImportFrom?.name)
            intent.putExtra(REQUEST_SPRITE, spritesToImport)
            intent.putExtra(REQUEST_GROUP_SPRITE, groupSpritesToImport)
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
        val REQUEST_GROUP_SPRITE: String = GroupSprite.TAG
        val TAG: String = ImportLocalObjectActivity::class.java.simpleName

        fun hasExtraTAG(activity: FragmentActivity?) = activity?.intent?.hasExtra(TAG)
    }
}
