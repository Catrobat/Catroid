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
package org.catrobat.catroid.ui.filepicker

import android.Manifest.permission
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.R
import org.catrobat.catroid.io.StorageOperations
import org.catrobat.catroid.ui.BaseCastActivity
import org.catrobat.catroid.ui.filepicker.SelectActionModeCallback.ActionModeClickListener
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH
import org.catrobat.catroid.ui.runtimepermissions.RequiresPermissionTask
import org.catrobat.catroid.ui.settingsfragments.SettingsFragment
import org.catrobat.catroid.utils.ToastUtil
import java.io.File
import java.util.ArrayList

open class FilePickerActivity : BaseCastActivity(), ActionModeClickListener {
    private var recyclerView: RecyclerView? = null
    private var filePickerAdapter: FilePickerAdapter? = null
    private var actionMode: ActionMode? = null

    companion object {
        val TAG = FilePickerActivity::class.java.simpleName
        private const val PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE = 801
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(this)

        setContentView(R.layout.activity_file_picker)
        setSupportActionBar(findViewById<View>(R.id.toolbar) as Toolbar)
        supportActionBar?.setTitle(R.string.import_project)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView = findViewById(R.id.recycler_view)
        setShowProgressBar(true)
        getFiles()
    }

    private fun setShowProgressBar(show: Boolean) {
        findViewById<View>(R.id.progress_bar).visibility = if (show) View.VISIBLE else View.GONE
        findViewById<View>(R.id.recycler_view).visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun getFiles() {
        object : RequiresPermissionTask(
            PERMISSIONS_REQUEST_IMPORT_FROM_EXTERNAL_STORAGE,
            listOf(permission.READ_EXTERNAL_STORAGE),
            R.string.runtime_permission_general
        ) {
            override fun task() {
                ProjectFileLister().listProjectFilesAsync(
                    StorageOperations.getStorageRoots(this@FilePickerActivity),
                    { success: List<File> -> onListProjectFilesFinished(success) })
            }
        }.execute(this)
    }

    private fun onListProjectFilesFinished(files: List<File>) {
        setShowProgressBar(false)

        if (files.isEmpty()) {
            findViewById<View>(R.id.empty_view).visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        } else {
            initializeAdapter(files)
        }
        return
    }

    private fun initializeAdapter(files: List<File>) {
        filePickerAdapter = FilePickerAdapter(files)
        filePickerAdapter?.setOnItemClickListener(object : RVAdapter.OnItemClickListener<File?> {
            override fun onItemClick(item: File?) {
                toggleItemSelection(item)
            }

            override fun onItemLongClick(item: File?, holder: CheckableVH?) {
                toggleItemSelection(item)
            }

            @SuppressWarnings("EmptyFunctionBlock")
            override fun onSettingsClick(item: File?, view: View?) {}
        })
        filePickerAdapter?.setSelectionListener { onSelectionChangedAction() }
        recyclerView?.adapter = filePickerAdapter
        startMultiSelectionMode()
    }

    private fun toggleItemSelection(item: File?) {
        filePickerAdapter?.toggleSelection(item)
        actionMode?.invalidate()
    }

    override fun onToggleSelection() {
        filePickerAdapter?.toggleSelection()
    }

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
        if (selectedFiles == null || selectedFiles.isEmpty()) {
            ToastUtil.showError(this, R.string.no_projects_selected)
            return
        }
        setShowProgressBar(true)
        val uris = ArrayList<Uri>()
        selectedFiles.forEach { uris.add(Uri.fromFile(it)) }
        Intent(Intent.ACTION_SEND_MULTIPLE).run {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            setResult(RESULT_OK, this)
        }
        finish()
    }

    override fun hasUnselectedItems(): Boolean =
        filePickerAdapter?.selectedItems?.size != filePickerAdapter?.selectableItemCount

    fun notifyActivityFinished(success: Boolean) {
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
}
