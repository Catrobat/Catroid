/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2024 The Catrobat Team
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

package org.catrobat.catroid.ui.settingsfragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.catrobat.catroid.R
import org.catrobat.catroid.TrustedDomainManager
import org.catrobat.catroid.common.Constants
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder

class WebAccessSettingsFragment : PreferenceFragmentCompat(),
    RVAdapter.OnItemClickListener<String>, RVAdapter.SelectionListener {

    private var adapter: WebAccessAdapter? = null
    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var addButton: FloatingActionButton? = null
    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SettingsFragment.setToChosenLanguage(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent = inflater.inflate(R.layout.fragment_web_access, container, false)
        recyclerView = parent.findViewById(R.id.recycler_view)
        emptyView = parent.findViewById(R.id.empty_view)
        addButton = parent.findViewById(R.id.fab_add_trusted_domain)
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addButton?.setOnClickListener {
            handleAddButton()
        }
        setEmptyViewVisibility()
    }

    override fun onResume() {
        super.onResume()

        requireActivity()
            .takeIf { it is AppCompatActivity }
            .let { it as AppCompatActivity }
            .apply {
                supportActionBar?.setTitle(R.string.preference_screen_web_access_title)
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        initializeAdapter()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) = Unit

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_web_access, menu)
        this.menu = menu
        setDeleteMenuItemEnabled()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_delete -> handleDelete()
            R.id.menu_help -> handleHelp()
            R.id.menu_done -> handleDone()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun initializeAdapter() {
        adapter = WebAccessAdapter(getTrustedDomains())
        adapter?.setOnItemClickListener(this)
        adapter?.setSelectionListener(this)
        emptyView?.setText(R.string.preference_empty_web_access)
        recyclerView?.adapter = adapter
    }

    private fun handleDone() {
        deleteTrustedDomains(adapter?.selectedItems!!)
        setMenuItemVisibility(R.id.menu_delete, true)
        setMenuItemVisibility(R.id.menu_help, true)
        setMenuItemVisibility(R.id.menu_done, false)

        adapter?.setCheckboxVisibility(false)
        addButton?.isVisible = true
    }

    private fun handleDelete() {
        setMenuItemVisibility(R.id.menu_delete, false)
        setMenuItemVisibility(R.id.menu_help, false)
        setMenuItemVisibility(R.id.menu_done, true)

        adapter?.setCheckboxVisibility(true)
        addButton?.isVisible = false
    }

    private fun setMenuItemVisibility(resourceId: Int, isVisible: Boolean) {
        val menuItem = menu?.findItem(resourceId)
        menuItem?.isVisible = isVisible
    }

    private fun handleHelp() {
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(Constants.WEB_REQUEST_WIKI_URL)
        )
        startActivity(intent)
    }

    private fun getTrustedDomains(): MutableList<String> {
        val userTrustList = TrustedDomainManager.getUserTrustList()
        if (userTrustList == "") {
            return mutableListOf<String>()
        }

        return userTrustList.split("\n").toMutableList()
    }

    private fun handleAddButton() {
        TextInputDialog.Builder(requireContext())
            .setTextWatcher(
                DuplicateInputTextWatcher(getTrustedDomainsAsNameable())
            )
            .setPositiveButton(getString(R.string.ok)) { _, input: String ->
                addTrustedDomain(input)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setTitle(R.string.web_access_add_trusted_domain)
            .show()
    }

    private fun addTrustedDomain(domain: String) {
        TrustedDomainManager.addToUserTrustList(domain)
        adapter?.addTrustedDomain(domain)
        setEmptyViewVisibility()
        setDeleteMenuItemEnabled()
    }

    private fun setDeleteMenuItemEnabled() {
        menu?.findItem(R.id.menu_delete)?.isEnabled = getTrustedDomains().size > 0
    }

    override fun onItemClick(item: String, selectionManager: MultiSelectionManager?) {
        showRenameDialog(item)
    }

    override fun onItemLongClick(item: String, holder: CheckableViewHolder?) {
        onItemClick(item, null)
    }

    override fun onSettingsClick(item: String, view: View?) {
        val elementList = arrayOf<CharSequence>(
            getString(R.string.delete)
        )

        val popupMenu = PopupMenu(context, view)
        elementList.forEach { option ->
            popupMenu.menu.add(option)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                getString(R.string.delete) -> deleteTrustedDomain(item)
            }
            true
        }
        popupMenu.show()
    }

    private fun deleteTrustedDomain(domain: String) {
        val userTrustList = getTrustedDomains()
        val domainIndex = userTrustList.indexOf(domain)
        userTrustList.removeAt(domainIndex)
        TrustedDomainManager.setUserTrustList(userTrustList.joinToString("\n"))
        adapter?.removeTrustedDomain(domain)
        setEmptyViewVisibility()
        setDeleteMenuItemEnabled()
    }

    private fun deleteTrustedDomains(domains: List<String>) {
        val userTrustList = getTrustedDomains()
        for (domain in domains) {
            val domainIndex = userTrustList.indexOf(domain)
            userTrustList.removeAt(domainIndex)
            adapter?.setSelection(domain, false)
        }
        adapter?.removeTrustedDomains(domains)
        TrustedDomainManager.setUserTrustList(userTrustList.joinToString("\n"))
        setEmptyViewVisibility()
        setDeleteMenuItemEnabled()
    }

    private fun setEmptyViewVisibility() {
        emptyView?.isVisible = getTrustedDomains().size == 0
    }

    private fun showRenameDialog(clickedItem: String) {
        TextInputDialog.Builder(requireContext())
            .setHint(getString(R.string.data_label))
            .setTextWatcher(
                DuplicateInputTextWatcher(getTrustedDomainsAsNameable())
            )
            .setPositiveButton(getString(R.string.ok)) { _, input: String ->
                renameTrustedDomain(clickedItem, input)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .setTitle(R.string.web_access_rename_trusted_domain)
            .show()
    }

    private fun getTrustedDomainsAsNameable(): List<Nameable> =
        getTrustedDomains().map {
            object : Nameable {
                override fun getName(): String = it

                override fun setName(name: String?) = Unit
            }
        }

    private fun renameTrustedDomain(oldValue: String, newValue: String) {
        val userTrustList = getTrustedDomains()
        val domainIndex = userTrustList.indexOf(oldValue)
        userTrustList[domainIndex] = newValue
        TrustedDomainManager.setUserTrustList(userTrustList.joinToString("\n"))
        adapter?.renameTrustedDomain(oldValue, newValue)
    }

    override fun onSelectionChanged(selectedItemCnt: Int) = Unit

    companion object {
        val WEB_ACCESS_SETTINGS_FRAGMENT_TAG: String =
            WebAccessSettingsFragment::class.java.simpleName
    }
}
