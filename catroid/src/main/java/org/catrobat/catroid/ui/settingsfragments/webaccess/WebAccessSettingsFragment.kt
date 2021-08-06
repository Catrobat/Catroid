/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2023 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments.webaccess

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.DividerItemDecoration
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.content.bricks.brickspinner.StringOption
import org.catrobat.catroid.databinding.PreferenceFragmentWebAccessLayoutBinding
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.utils.ToastUtil
import org.koin.android.ext.android.inject

val WEB_ACCESS_SETTINGS_FRAGMENT_TAG: String = WebAccessSettingsFragment::class.java.simpleName
private const val NONE = 0
private const val DELETE = 1

class WebAccessSettingsFragment : PreferenceFragmentCompat(),
    ActionMode.Callback {

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(NONE, DELETE)
    internal annotation class ActionModeType

    @ActionModeType
    private var actionModeType = NONE
    private var actionMode: ActionMode? = null

    private var _binding: PreferenceFragmentWebAccessLayoutBinding? = null
    private val binding get() = _binding!!

    private val repository by inject<WebAccessRepository>()
    private val domainsList = mutableListOf<String>()
    private val adapter by lazy {
        WebAccessAdapter()
    }

    override fun onResume() {
        super.onResume()
        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.setTitle(R.string.preference_title_web_access)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.id.menu_item_help)?.isVisible = true
        menu.findItem(R.id.menu_item_delete)?.isVisible = true
        super.onPrepareOptionsMenu(menu)
    }

    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) = Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PreferenceFragmentWebAccessLayoutBinding
            .inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabAddTrustedDomain.setOnClickListener {
            showDialog()
        }

        binding.rvWebAccess.apply {
            adapter = this@WebAccessSettingsFragment.adapter
            if (itemDecorationCount == 0) {
                addItemDecoration(
                    DividerItemDecoration(
                        requireContext(),
                        DividerItemDecoration.VERTICAL
                    )
                )
            }
        }

        repository.getUserTrustList().observe(viewLifecycleOwner) { list ->
            adapter.setItems(list)
            domainsList.addAll(list.map { it.name })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_help) {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(Constants.WEB_REQUEST_WIKI_URL)
            )
            startActivity(intent)
        } else if (item.itemId == R.id.menu_item_delete) {
            if (adapter.itemCount == 0) {
                ToastUtil.showError(requireActivity(), R.string.am_empty_list)
                return false
            }
            actionModeType = DELETE
            actionMode = requireActivity().startActionMode(this)
        }
        return true
    }

    private fun showDialog() {
        val scopeList = domainsList.map { StringOption(it) }
        val textWatcher = DuplicateInputTextWatcher(scopeList)
        TextInputDialog.Builder(requireContext())
            .setTextWatcher(textWatcher)
            .setPositiveButton(getString(R.string.ok)) { _, textInput: String? ->
                textInput?.let {
                    repository.addToUserTrustList(textInput)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setTitle(R.string.preference_screen_web_access_dialog_title)
            .create()
            .show()
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        when (actionModeType) {
            DELETE -> mode?.title = getString(R.string.am_delete)
            NONE -> return false
        }
        mode?.menuInflater?.inflate(R.menu.context_menu, menu)
        adapter.showCheckBoxes(true)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.confirm -> handleContextualAction()
            else -> return false
        }
        return true
    }

    private fun handleContextualAction() {
        if (adapter.getSelectedItems().isEmpty()) {
            actionMode?.finish()
            return
        }
        when (actionModeType) {
            DELETE -> showDeleteAlert(adapter.getSelectedItems())
            NONE -> throw IllegalStateException("ActionModeType not set Correctly")
        }
    }

    private fun showDeleteAlert(selectedItems: List<TrustedDomain>) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.deletion_alert_title)
            .setMessage(R.string.dialog_confirm_delete)
            .setPositiveButton(
                R.string.delete
            ) { _: DialogInterface?, _: Int ->
                deleteItems(selectedItems)
            }
            .setNegativeButton(R.string.cancel) { _, _ ->
                finishActionMode()
            }
            .setCancelable(false)
            .show()
    }

    @VisibleForTesting
    fun deleteItems(selectedItems: List<TrustedDomain>) {
        finishActionMode()
        repository.deleteFromUserTrustDomain(selectedItems.map { it.name })
        ToastUtil.showSuccess(requireContext(), resources.getQuantityString(
                R.plurals.deleted_Items,
                selectedItems.size,
                selectedItems.size
            ))
    }

    private fun finishActionMode() {
        adapter.clearSelection()
        if (actionModeType != NONE) {
            actionMode?.finish()
        }
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        resetActionModeParameters()
        adapter.clearSelection()
    }

    private fun resetActionModeParameters() {
        actionModeType = NONE
        actionMode = null
        adapter.showCheckBoxes(false)
    }
}
