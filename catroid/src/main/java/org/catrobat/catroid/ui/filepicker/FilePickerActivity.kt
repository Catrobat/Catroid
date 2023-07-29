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
package org.catrobat.catroid.ui.filepicker

import org.catrobat.catroid.ui.BaseCastActivity
import org.catrobat.catroid.ui.filepicker.ListProjectFilesTask.OnListProjectFilesListener
import org.catrobat.catroid.ui.filepicker.SelectActionModeCallback.ActionModeClickListener
import org.catrobat.catroid.ui.recyclerview.fragment.ProjectListFragment.ProjectImportFinishedListener
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import android.Manifest.permission
import android.annotation.SuppressLint
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder
import org.catrobat.catroid.utils.ToastUtil
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ActionMode
import android.view.View
import androidx.appcompat.widget.Toolbar
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import java.io.File
import java.util.ArrayList

class FilePickerActivity : BaseCastActivity(), OnListProjectFilesListener, ActionModeClickListener,
    ProjectImportFinishedListener {
    private var recyclerView: RecyclerView? = null
    private var filePickerAdapter: FilePickerAdapter? = null
    private var actionMode: ActionMode? = null
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(this)
        setContentView(R.layout.activity_file_picker)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar?.setTitle(R.string.import_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        recyclerView = findViewById(R.id.recycler_view)
        setShowProgressBar(true)
        files
    }

    private fun setShowProgressBar(show: Boolean) {
        findViewById<View>(R.id.progress_bar).visibility =
            if (show) View.VISIBLE else View.GONE
        findViewById<View>(R.id.recycler_view).visibility =
            if (show) View.GONE else View.VISIBLE
    }
    private val files: Unit
        get() {
            object : RequiresPermissionTask(
                PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE,
                listOf(permission.READ_EXTERNAL_STORAGE),
                R.string.runtime_permission_general
            ) {
                @SuppressWarnings("SpreadOperator")
                override fun task() {
                    ListProjectFilesTask(this@FilePickerActivity).execute(*storageRoots.toTypedArray())
                }
            }.execute(this)
        }

    // needed for APIs 21 & 22
    private val storageRoots: List<File>
        get() {
            val rootDirs: MutableList<File> = ArrayList()
            for (externalFilesDir in getExternalFilesDirs(null)) {
                var path = externalFilesDir.absolutePath
                Log.e(TAG, externalFilesDir.canRead().toString() + " Path: " + path)
                val packageName = applicationContext.packageName
                path = path.replace("/Android/data/" + packageName + "/files".toRegex(), "")
                rootDirs.add(File(path))
            }
            return rootDirs
        }

    override fun onListProjectFilesComplete(files: List<File>?) {
        setShowProgressBar(false)
        if (files?.isEmpty() == true) {
            findViewById<View>(R.id.empty_view).visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            files?.let { initializeAdapter(it) }
        }
    }

    private fun initializeAdapter(files: List<File>) {
        filePickerAdapter = FilePickerAdapter(files)
        filePickerAdapter?.setOnItemClickListener(object : RVAdapter.OnItemClickListener<File?> {
            override fun onItemClick(item: File?, selectionManager: MultiSelectionManager) {
                item?.let { toggleItemSelection(it) }
            }

            override fun onItemLongClick(item: File?, holder: CheckableViewHolder?) {
                item?.let { toggleItemSelection(it) }
            }

            @SuppressWarnings("EmptyFunctionBlock")
            override fun onSettingsClick(item: File?, view: View?) {}
        })
        filePickerAdapter?.setSelectionListener { selectedItemCount: Int -> onSelectionChangedAction() }
        recyclerView?.adapter = filePickerAdapter
        startMultiSelectionMode()
    }

    private fun toggleItemSelection(item: File) {
        filePickerAdapter?.toggleSelection(item)
        actionMode?.invalidate()
    }

    override fun onToggleSelection() {
        filePickerAdapter?.toggleSelection()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun startMultiSelectionMode() {
        filePickerAdapter?.showCheckBoxes = true
        actionMode = startActionMode(SelectActionModeCallback(this))
        filePickerAdapter?.notifyDataSetChanged()
    }

    private fun onSelectionChangedAction() {
        actionMode?.invalidate()
    }

    override fun endMultiSelectionMode() {
        finish()
    }

    override fun onConfirm() {
        val selectedFiles = filePickerAdapter?.selectedItems
        if (selectedFiles?.isEmpty() == true) {
            ToastUtil.showError(this, R.string.no_projects_selected)
            return
        }
        setShowProgressBar(true)
        val uris = ArrayList<Uri>()
        if (selectedFiles != null) {
            for (file in selectedFiles) {
                uris.add(Uri.fromFile(file))
            }
        }
        val data = Intent(Intent.ACTION_SEND_MULTIPLE)
        data.type = "*/*"
        data.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
        setResult(RESULT_OK, data)
        finish()
    }

    override fun hasUnselectedItems(): Boolean = filePickerAdapter?.selectedItems?.size != filePickerAdapter?.selectableItemCount

    override fun notifyActivityFinished(success: Boolean) {
        setShowProgressBar(false)
        if (success) {
            setResult(RESULT_OK)
        } else {
            setResult(RESULT_CANCELED)
        }
        finish()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        val TAG: String = FilePickerActivity::class.java.simpleName
        private const val PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801
    }
}
