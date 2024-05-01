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

package org.catrobat.catroid.ui.recyclerview.fragment

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.Nameable
import org.catrobat.catroid.content.Scene
import org.catrobat.catroid.content.bricks.BroadcastMessageBrick
import org.catrobat.catroid.ui.BottomBar
import org.catrobat.catroid.ui.SpriteActivity
import org.catrobat.catroid.ui.SpriteActivity.FRAGMENT_SCRIPTS
import org.catrobat.catroid.ui.UiUtils
import org.catrobat.catroid.ui.addTabLayout
import org.catrobat.catroid.ui.recyclerview.adapter.BroadcastListAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.RVAdapter
import org.catrobat.catroid.ui.recyclerview.adapter.multiselection.MultiSelectionManager
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableViewHolder
import org.catrobat.catroid.ui.removeTabLayout
import org.koin.java.KoinJavaComponent.inject

class BroadcastListFragment(private val currentBroadcastMessage: String) : Fragment(),
    RVAdapter.OnItemClickListener<String> {

    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var adapter: BroadcastListAdapter? = null
    private var currentBroadcastBrick: BroadcastMessageBrick? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val parent =
            inflater.inflate(R.layout.fragment_list_view, container, false)
        recyclerView = parent.findViewById(R.id.recycler_view)
        emptyView = parent.findViewById(R.id.empty_view)
        return parent
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        initalizeAdapter()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.setTitle(R.string.brick_broadcast)
        BottomBar.showBottomBar(activity)
        BottomBar.hidePlayButton(activity)
        if (currentBroadcastMessage == defaultMessage) {
            removeMessageFromProject(currentBroadcastMessage)
            handleAddButton()
        }
    }

    override fun onPause() {
        super.onPause()
        BottomBar.hideBottomBar(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activity.removeTabLayout()
    }

    override fun onDetach() {
        super.onDetach()
        activity.addTabLayout(FRAGMENT_SCRIPTS)
    }

    private fun initalizeAdapter() {
        adapter = BroadcastListAdapter(getBroadcastMessagesOfProject(), currentBroadcastMessage)
        emptyView?.setText(R.string.fragment_data_text_description)
        onAdapterReady()
    }

    private fun onAdapterReady() {
        recyclerView?.adapter = adapter
        adapter?.setOnItemClickListener(this)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onItemClick(item: String?, selectionManager: MultiSelectionManager?) {
        currentBroadcastBrick?.previousBroadcastMessage = currentBroadcastBrick?.broadcastMessage
        if (currentBroadcastBrick?.previousBroadcastMessage != null &&
            currentBroadcastBrick?.previousBroadcastMessage != "" &&
            currentBroadcastBrick?.previousBroadcastMessage != item
        ) {
            val scriptFragment =
                requireActivity().supportFragmentManager.findFragmentByTag(ScriptFragment.TAG) as ScriptFragment?
            if (scriptFragment != null && scriptFragment.copyProjectForUndoOption()) {
                (scriptFragment.activity as SpriteActivity).setUndoMenuItemVisibility(true)
                scriptFragment.setUndoBrickPosition(currentBroadcastBrick)
            }
        }
        if (item != null) {
            currentBroadcastBrick?.broadcastMessage = item
        }
        parentFragmentManager.popBackStack()
    }

    override fun onItemLongClick(item: String, holder: CheckableViewHolder) {
        onItemClick(item, null)
    }

    private fun getBroadcastMessagesOfProject(): List<String> {
        val projectManager: ProjectManager by inject(ProjectManager::class.java)

        val currentProject = projectManager.currentProject

        return currentProject.broadcastMessageContainer.broadcastMessages
    }

    private fun removeMessageFromProject(message: String?): Boolean {
        val projectManager: ProjectManager by inject(ProjectManager::class.java)

        val currentProject = projectManager.currentProject

        return currentProject.broadcastMessageContainer.removeBroadcastMessage(message)
    }

    private fun addMessageToProject(message: String?): Boolean {
        val projectManager: ProjectManager by inject(ProjectManager::class.java)

        val currentProject = projectManager.currentProject

        return currentProject.broadcastMessageContainer.addBroadcastMessage(message)
    }

    @VisibleForTesting
    fun renameMessage(editMessage: String, message: String) {
        if (removeMessageFromProject(editMessage)) {
            addMessageToProject(message)
            val projectManager: ProjectManager by inject(ProjectManager::class.java)

            val currentScene: Scene = projectManager.currentlyEditedScene

            currentScene.editBroadcastMessagesInUse(editMessage, message)
            adapter?.setCurrentBroadcastMessage(message)
            adapter?.notifyDataSetChanged()
        }
    }

    @VisibleForTesting
    fun addMessage(message: String) {
        addMessageToProject(message)
        onItemClick(message, null)
    }

    private fun showRenameDialog(editMessage: String) {
        val activity: AppCompatActivity? = UiUtils.getActivityFromView(view)
        if (activity !is SpriteActivity) {
            return
        }

        val builder = TextInputDialog.Builder(requireContext())
        builder.setHint(getString(R.string.data_label))
            .setText(editMessage)
            .setTextWatcher(
                DuplicateInputTextWatcher(getBroadcastMessagesAsNameable())
            )
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, textInput: String ->
                renameMessage(editMessage, textInput)
            }
            .setNegativeButton(R.string.cancel, null)
            .setTitle(R.string.dialog_edit_broadcast_message_title)
            .show()
    }

    private fun getBroadcastMessagesAsNameable(): List<Nameable> =
        getBroadcastMessagesOfProject().map {
            object : Nameable {
                override fun getName(): String = it

                override fun setName(name: String?) = Unit
            }
        }

    override fun onSettingsClick(item: String, view: View) {
        val elementList = arrayOf<CharSequence>(
            getString(R.string.rename)
        )

        val popupMenu = PopupMenu(context, view)
        elementList.forEach { option ->
            popupMenu.menu.add(option)
        }
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.title) {
                getString(R.string.rename) -> showRenameDialog(item)
            }
            true
        }
        popupMenu.show()
    }

    fun handleAddButton() {
        val activity = UiUtils.getActivityFromView(view) as? SpriteActivity ?: return
        val builder = TextInputDialog.Builder(activity)
        builder.setHint(getString(R.string.dialog_broadcast_message_name))
            .setTextWatcher(
                DuplicateInputTextWatcher(getBroadcastMessagesAsNameable())
            )
            .setText(
                UniqueNameProvider().getUniqueName(
                    activity.getString(R.string.default_broadcast_message_name),
                    this.getBroadcastMessagesOfProject()
                )
            )
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, textInput: String ->
                addMessage(textInput)
            }
            .setNegativeButton(R.string.cancel, null)
            .setTitle(R.string.dialog_new_broadcast_message_title)
            .show()
    }

    fun setBroadcastBrick(broadcastBrick: BroadcastMessageBrick) {
        this.currentBroadcastBrick = broadcastBrick
    }

    companion object {
        @JvmStatic
        val TAG: String = BroadcastListFragment::class.java.simpleName

        const val defaultMessage: String = "new message"

        fun showFragment(context: Context, broadcastBrick: BroadcastMessageBrick) {
            val activity = UiUtils.getActivityFromContextWrapper(context) ?: return
            val fragmentManager = activity.supportFragmentManager

            var currentFragment =
                fragmentManager.findFragmentByTag(TAG)

            if (currentFragment == null) {
                currentFragment = BroadcastListFragment(broadcastBrick.broadcastMessage)
                currentFragment.setBroadcastBrick(broadcastBrick)
                fragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        currentFragment,
                        TAG
                    )
                    .addToBackStack(TAG)
                    .commit()
            }
        }
    }
}
