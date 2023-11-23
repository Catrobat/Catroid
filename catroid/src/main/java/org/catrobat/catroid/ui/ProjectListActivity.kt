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
package org.catrobat.catroid.ui

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.fragment.app.Fragment
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.databinding.ActivityRecyclerBinding
import org.catrobat.catroid.ui.dialogs.NewProjectDialogFragment
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectListFragment

class ProjectListActivity : BaseCastActivity() {
    private lateinit var binding: ActivityRecyclerBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.apply {
            setTitle(R.string.project_list_title)
            setDisplayHomeAsUpEnabled(true)
        }

        BottomBar.hidePlayButton(this)
        BottomBar.hideAiAssistButton(this)

        val projectListFragment = ProjectListFragment()
        if (intent.hasExtra(IMPORT_LOCAL_INTENT)) {
            BottomBar.hideAddButton(this)
            supportActionBar?.setTitle(R.string.import_from_project)
        }
        intent?.apply {
            if (action != null) {
                val data = Bundle()
                data.putParcelable("intent", intent)
                projectListFragment.arguments = data
            }
        }

        loadFragment(projectListFragment)
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, ProjectListFragment.TAG)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_projects_activity, menu)
        menu.findItem(R.id.merge).isVisible = BuildConfig.FEATURE_MERGE_ENABLED
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun handleAddButton(view: View?) {
        val dialog = NewProjectDialogFragment()
        dialog.show(supportFragmentManager, NewProjectDialogFragment.TAG)
    }

    companion object {
        const val IMPORT_LOCAL_INTENT: String = "merge"
        val TAG: String = ProjectListActivity::class.java.simpleName
    }
}
