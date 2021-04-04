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
package org.catrobat.catroid.ui.recyclerview.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.PagerSnapHelper
import kotlinx.android.synthetic.main.landing_page.categoriesRecyclerView
import kotlinx.android.synthetic.main.landing_page.editProject
import kotlinx.android.synthetic.main.landing_page.featuredProjectsRecyclerView
import kotlinx.android.synthetic.main.landing_page.featuredProjectsTextView
import kotlinx.android.synthetic.main.landing_page.myProjectsRecyclerView
import kotlinx.android.synthetic.main.landing_page.myProjectsTextView
import kotlinx.android.synthetic.main.landing_page.newProjectFloatingActionButton
import kotlinx.android.synthetic.main.landing_page.noInternetLayout
import kotlinx.android.synthetic.main.landing_page.projectImageView
import kotlinx.android.synthetic.main.landing_page.shimmerViewContainer
import kotlinx.android.synthetic.main.landing_page.uploadProject
import kotlinx.android.synthetic.main.progress_bar.progress_bar
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.FlavoredConstants.BASE_URL_HTTPS
import org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY
import org.catrobat.catroid.common.ProjectData
import org.catrobat.catroid.io.ProjectAndSceneScreenshotLoader
import org.catrobat.catroid.io.asynctask.ProjectLoadTask
import org.catrobat.catroid.io.asynctask.ProjectLoadTask.ProjectLoadListener
import org.catrobat.catroid.ui.ProjectActivity
import org.catrobat.catroid.ui.ProjectListActivity
import org.catrobat.catroid.ui.ProjectUploadActivity
import org.catrobat.catroid.ui.WebViewActivity
import org.catrobat.catroid.ui.recyclerview.CategoryTitleCallback
import org.catrobat.catroid.ui.recyclerview.FeaturedProjectCallback
import org.catrobat.catroid.ui.recyclerview.IndicatorDecoration
import org.catrobat.catroid.ui.recyclerview.ProjectListener
import org.catrobat.catroid.ui.recyclerview.adapter.CategoriesAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.FeaturedProjectsAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.HorizontalProjectsAdapter
import org.catrobat.catroid.ui.recyclerview.dialog.NewProjectDialogFragment
import org.catrobat.catroid.ui.recyclerview.viewmodel.MainFragmentViewModel
import org.catrobat.catroid.utils.FileMetaDataExtractor
import org.catrobat.catroid.utils.NetworkConnectionMonitor
import org.catrobat.catroid.utils.ProjectDownloadUtil.setFragment
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.Utils
import org.catrobat.catroid.utils.setVisibleOrGone
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class MainMenuFragment : Fragment(),
    ProjectListener,
    View.OnClickListener,
    FeaturedProjectCallback,
    CategoryTitleCallback,
    ProjectLoadListener {

    var currentProject: String? = null
    private lateinit var projectsAdapter: HorizontalProjectsAdapter
    private val viewModel: MainFragmentViewModel by viewModel()
    private val connectionMonitor: NetworkConnectionMonitor by inject()
    private val featuredProjectsAdapter: FeaturedProjectsAdapter by inject()
    private val categoriesAdapter: CategoriesAdapter by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.setIsLoading(true)
        return inflater.inflate(R.layout.landing_page, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.isLoading().observe(viewLifecycleOwner, Observer { show ->
            progress_bar.setVisibleOrGone(show)
        })

        setupViewVisibility()

        editProject.setOnClickListener(this)
        uploadProject.setOnClickListener(this)
        newProjectFloatingActionButton.setOnClickListener(this)
        myProjectsTextView.setOnClickListener(this)
        projectImageView.setOnClickListener(this)
        featuredProjectsTextView.setOnClickListener(this)

        setFragment(this)

        setupProjectsRV()
        setupFeaturedProjectsRV()
        setupCategoriesRV()
        viewModel.setIsLoading(false)
    }

    private fun setupCategoriesRV() {
        categoriesRecyclerView.setHasFixedSize(true)
        categoriesAdapter.apply {
            setOnProjectClickCallback(this@MainMenuFragment)
            setOnCategoryTitleClickCallback(this@MainMenuFragment)
        }.let {
            categoriesRecyclerView.adapter = it
        }

        viewModel.getProjectCategories().observe(viewLifecycleOwner, Observer { items ->
            stopShimmer()
            if (items.isNullOrEmpty()) {
                return@Observer
            }
            categoriesAdapter.setItems(items)
        })
    }

    private fun setupViewVisibility() {
        connectionMonitor.observe(viewLifecycleOwner, Observer { connectionActive ->
            viewModel.fetchData()
            noInternetLayout.setVisibleOrGone(connectionActive.not())
            featuredProjectsRecyclerView.setVisibleOrGone(connectionActive)
            featuredProjectsTextView.isEnabled = connectionActive
            categoriesRecyclerView.setVisibleOrGone(connectionActive)

            if (connectionActive && viewModel.getProjectCategories().value == null) {
                startShimmer()
            } else {
                stopShimmer()
            }
        })
    }

    private fun setupProjectsRV() {
        projectsAdapter = HorizontalProjectsAdapter(this)
        myProjectsRecyclerView.apply {
            setHasFixedSize(true)
            LinearSnapHelper().attachToRecyclerView(this)
            adapter = projectsAdapter
        }

        viewModel.getProjects().observe(viewLifecycleOwner, Observer { projects ->
            setAndLoadCurrentProject(projects)
            updateRecyclerview(projects)
        })
    }

    private fun setupFeaturedProjectsRV() {
        featuredProjectsAdapter.setCallback(this@MainMenuFragment)
        featuredProjectsRecyclerView.apply {
            setHasFixedSize(true)
            if (itemDecorationCount == 0) {
                addItemDecoration(IndicatorDecoration(requireContext()))
            }
            adapter = featuredProjectsAdapter
            onFlingListener = null
        }.run {
            PagerSnapHelper().attachToRecyclerView(featuredProjectsRecyclerView)
            resumeAutoScroll()
        }

        viewModel.getFeaturedProjects().observe(viewLifecycleOwner, Observer { items ->
            if (items.isNullOrEmpty()) {
                return@Observer
            }

            featuredProjectsAdapter.setItems(items)
            featuredProjectsRecyclerView.itemsCount = items.size
        })
    }

    override fun onResume() {
        super.onResume()
        connectionMonitor.registerDefaultNetworkCallback()
        viewModel.setIsLoading(false)
        val projectName = requireActivity().intent.getStringExtra(Constants.EXTRA_PROJECT_NAME)
        if (projectName != null) {
            requireActivity().intent.removeExtra(Constants.EXTRA_PROJECT_NAME)
            loadDownloadedProject(projectName)
        }
        refreshData()
    }

    override fun onPause() {
        super.onPause()
        connectionMonitor.unregisterDefaultNetworkCallback()
    }

    private fun setAndLoadCurrentProject(myProjects: List<ProjectData>) {
        currentProject = if (myProjects.isNotEmpty()) {
            myProjects[0].name
        } else {
            Utils.getCurrentProjectName(context)
        }
        val projectDir = File(
            DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(currentProject)
        )
        ProjectLoadTask.task(projectDir, context)
        loadProjectImage()
    }

    private fun loadDownloadedProject(name: String) {
        val projectDir = File(
            DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(name)
        )
        ProjectLoadTask(projectDir, context)
            .setListener(this)
            .execute()
    }

    override fun onLoadFinished(success: Boolean) {
        if (success) {
            val intent = Intent(activity, ProjectActivity::class.java)
            intent.putExtra(
                ProjectActivity.EXTRA_FRAGMENT_POSITION,
                ProjectActivity.FRAGMENT_SCENES
            )
            startActivity(intent)
        } else {
            viewModel.setIsLoading(false)
            ToastUtil.showError(activity, R.string.error_load_project)
        }
    }

    private fun loadProjectImage() {
        val projectDir = File(
            DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(currentProject)
        )
        val loader = ProjectAndSceneScreenshotLoader(CURRENT_THUMBNAIL_SIZE, CURRENT_THUMBNAIL_SIZE)
        loader.loadAndShowScreenshot(
            projectDir.name, loader.getScreenshotSceneName(projectDir), false,
            projectImageView
        )
    }

    private fun updateRecyclerview(myProjects: List<ProjectData>) {
        if (myProjects.size < 2) {
            projectsAdapter.setItems(null)
        } else {
            val projectsCount = myProjects.size.coerceAtMost(MAX_PROJECTS_NUMBER)
            projectsAdapter.setItems(myProjects.subList(1, projectsCount))
        }
    }

    override fun onProjectClick(projectData: ProjectData?) {
        viewModel.setIsLoading(true)
        val projectDir = File(
            DEFAULT_ROOT_DIRECTORY,
            FileMetaDataExtractor
                .encodeSpecialCharsForFileSystem(projectData!!.name)
        )
        ProjectLoadTask(projectDir, context)
            .setListener(this)
            .execute()
    }

    fun refreshData() {
        viewModel.forceUpdate()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.image_view,
            R.id.editProject -> {
                viewModel.setIsLoading(true)
                val projectDir = File(
                    DEFAULT_ROOT_DIRECTORY,
                    FileMetaDataExtractor.encodeSpecialCharsForFileSystem(currentProject)
                )
                ProjectLoadTask(projectDir, context)
                    .setListener(this)
                    .execute()
            }

            R.id.newProjectFloatingActionButton ->
                NewProjectDialogFragment().show(parentFragmentManager, NewProjectDialogFragment.TAG)

            R.id.uploadProject -> {
                viewModel.setIsLoading(true)
                val intent = Intent(activity, ProjectUploadActivity::class.java)
                    .putExtra(
                        ProjectUploadActivity.PROJECT_DIR,
                        File(
                            DEFAULT_ROOT_DIRECTORY,
                            FileMetaDataExtractor.encodeSpecialCharsForFileSystem(
                                Utils.getCurrentProjectName(activity)
                            )
                        )
                    )
                startActivity(intent)
            }

            R.id.myProjectsTextView -> {
                viewModel.setIsLoading(true)
                startActivity(Intent(activity, ProjectListActivity::class.java))
            }

            R.id.featuredProjectsTextView -> {
                viewModel.setIsLoading(true)
                startActivity(Intent(activity, WebViewActivity::class.java))
            }
        }
    }

    override fun onFeatureProjectClicked(projectUrl: String) {
        openWebView(projectUrl)
    }

    override fun onCategoryTitleClicked(categoryId: String) {
        val categoryUrl = BASE_URL_HTTPS
        openWebView(categoryUrl)
    }

    private fun openWebView(url: String?) {
        val webViewActivityIntent = Intent(activity, WebViewActivity::class.java)
        webViewActivityIntent.putExtra(WebViewActivity.INTENT_PARAMETER_URL, url)
        viewModel.setIsLoading(true)
        startActivity(webViewActivityIntent)
    }

    private fun stopShimmer() {
        shimmerViewContainer.apply {
            stopShimmer()
            setVisibleOrGone(false)
        }
    }

    private fun startShimmer() {
        shimmerViewContainer.apply {
            setVisibleOrGone(true)
            startShimmer()
        }
    }

    companion object {
        private const val CURRENT_THUMBNAIL_SIZE = 500
        val TAG = MainMenuFragment::class.java.simpleName
        private const val MAX_PROJECTS_NUMBER = 10
    }
}
