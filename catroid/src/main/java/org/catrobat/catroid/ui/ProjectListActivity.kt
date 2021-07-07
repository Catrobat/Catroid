/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2021 The Catrobat Team
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
import androidx.navigation.fragment.NavHostFragment
import org.catrobat.catroid.BuildConfig
import org.catrobat.catroid.R
import org.catrobat.catroid.databinding.ActivityProjectListBinding
import org.catrobat.catroid.ui.recyclerview.dialog.NewProjectDialogFragment

class ProjectListActivity : BaseCastActivity() {

    private lateinit var binding: ActivityProjectListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProjectListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initNavController()

        initActionBar()

        handleAddProjectButton()
    }

    private fun handleAddProjectButton() {
        binding.addProjectButton.setOnClickListener {
            val dialog = NewProjectDialogFragment()
            dialog.show(supportFragmentManager, NewProjectDialogFragment.TAG)
        }
    }

    private fun initNavController() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.project_list_nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        if (intent.action != null) {
            val bundle = Bundle().apply {
                putParcelable("intent", intent)
            }
            navController.setGraph(R.navigation.project_list_nav_graph, bundle)
        } else {
            navController.setGraph(R.navigation.project_list_nav_graph)
        }
    }

    private fun initActionBar() {
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar?.let {
            it.setTitle(R.string.project_list_title)
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_projects_activity, menu)
        menu.findItem(R.id.merge)?.isVisible = BuildConfig.FEATURE_MERGE_ENABLED
        return super.onCreateOptionsMenu(menu)
    }
}
