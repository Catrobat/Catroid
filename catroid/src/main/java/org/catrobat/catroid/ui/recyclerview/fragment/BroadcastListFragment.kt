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

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
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
import org.catrobat.catroid.ui.recyclerview.dialog.TextInputDialog
import org.catrobat.catroid.ui.recyclerview.dialog.textwatcher.DuplicateInputTextWatcher
import org.catrobat.catroid.ui.recyclerview.util.UniqueNameProvider
import org.catrobat.catroid.ui.recyclerview.viewholder.CheckableVH
import org.catrobat.catroid.ui.removeTabLayout

class BroadcastListFragment(private val highlightMessage: String) : Fragment(),
    RVAdapter.OnItemClickListener<String> {

    private var recyclerView: RecyclerView? = null
    private var emptyView: TextView? = null
    private var adapter: BroadcastListAdapter? = null
    private var onBroadcastMessageSelectedListener: OnBroadcastMessageSelectedListener? = null

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
        initializeAdapter()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)?.supportActionBar?.setTitle(R.string.brick_broadcast)
        BottomBar.showBottomBar(activity)
        BottomBar.hidePlayButton(activity)
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

    fun initializeAdapter() {
        val messages = getSortedBroadcastMessagesOfProject()
        adapter = BroadcastListAdapter(messages, highlightMessage)
        emptyView?.setText(R.string.fragment_data_text_description)
        onAdapterReady()
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        for (index in 0 until menu.size()) {
            menu.getItem(index).isVisible = false
        }

        super.onPrepareOptionsMenu(menu)
    }

    private fun onAdapterReady() {
        recyclerView?.adapter = adapter
        adapter?.setOnItemClickListener(this)
    }

    override fun onItemClick(item: String) {
        notifyDataChangedAndReturn(item)
    }

    override fun onItemLongClick(item: String, holder: CheckableVH) {
        onItemClick(item)
    }

    private fun notifyDataChangedAndReturn(item: String) {
        onBroadcastMessageSelectedListener?.onBroadcastMessageSelected(item)
        Log.d("yes", "yes onbroadcastmessageselected")
        fragmentManager?.popBackStack()
    }

    private fun getSortedBroadcastMessagesOfProject(): List<String> {
        sortMessagesOfProject()
        return getBroadcastMessagesOfProject()
    }

    private fun sortMessagesOfProject() {
        val messages = ProjectManager.getInstance()
            .currentProject.broadcastMessageContainer.broadcastMessages
        messages.sortWith(
            compareBy(String.CASE_INSENSITIVE_ORDER) { it }
        )
    }

    private fun getBroadcastMessagesOfProject(): List<String> {
        val currentProject = ProjectManager.getInstance().currentProject
        return currentProject.broadcastMessageContainer.broadcastMessages
    }

    private fun removeMessageFromProject(message: String?): Boolean {
        return ProjectManager.getInstance().currentProject.broadcastMessageContainer
            .removeBroadcastMessage(message)
    }

    private fun addMessageToProject(message: String?): Boolean {
        return ProjectManager.getInstance().currentProject.broadcastMessageContainer
            .addBroadcastMessage(message)
    }

    @VisibleForTesting
    fun renameMessage(editMessage: String, message: String) {
        if (removeMessageFromProject(editMessage)) {
            addMessageToProject(message)
            val currentScene: Scene = ProjectManager.getInstance().currentlyEditedScene
            currentScene.editBroadcastMessagesInUse(editMessage, message)
            sortMessagesOfProject()
            adapter?.setHighLightMessage(message)
        }
        adapter?.notifyDataSetChanged()
    }

    @VisibleForTesting
    fun addMessage(message: String) {
        addMessageToProject(message)
        sortMessagesOfProject()
        notifyDataChangedAndReturn(message)
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
            .setPositiveButton(getString(R.string.ok)) {
                    _: DialogInterface?, textInput: String ->
                renameMessage(editMessage, textInput)
            }
            .setNegativeButton(R.string.cancel, null)
            .setTitle(R.string.dialog_edit_broadcast_message_title)
            .show()
    }

    private fun getBroadcastMessagesAsNameable(): List<Nameable> =
        getBroadcastMessagesOfProject().map {
            object : Nameable {
                override fun getName(): String {
                    return it
                }

                override fun setName(name: String?) = Unit
            }
        }

    override fun onSettingsClick(item: String, view: View) {
        val elementList = arrayOf<CharSequence>(
            getString(R.string.rename)
        )

        val popupMenu = PopupMenu(context, view)
        elementList.forEach {
                option -> popupMenu.menu.add(option)
        }
        popupMenu.setOnMenuItemClickListener {
                menuItem -> when (menuItem.title) {
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
                    ProjectManager.getInstance().currentProject.broadcastMessageContainer.broadcastMessages
                )
            )
            .setPositiveButton(getString(R.string.ok)) { _: DialogInterface?, textInput: String ->
                addMessage(textInput)
            }
            .setNegativeButton(R.string.cancel, null)
            .setTitle(R.string.dialog_new_broadcast_message_title)
            .show()
    }

    fun setOnBroadcastMessageSelectedListener(listener: OnBroadcastMessageSelectedListener) {
        onBroadcastMessageSelectedListener = listener
    }

    interface OnBroadcastMessageSelectedListener {
        fun onBroadcastMessageSelected(message: String)
    }

    companion object {
        @JvmStatic
        val BROADCAST_LIST_FRAGMENT_TAG: String = BroadcastListFragment::class.java.simpleName

        fun showFragment(context: Context, broadcastBrick: BroadcastMessageBrick) {
            val activity = UiUtils.getActivityFromContextWrapper(context) ?: return
            val fragmentManager = activity.supportFragmentManager

            var broadcastListFragment =
                fragmentManager.findFragmentByTag(BROADCAST_LIST_FRAGMENT_TAG)

            if (broadcastListFragment == null) {
                broadcastListFragment = BroadcastListFragment(broadcastBrick.broadcastMessage)
                broadcastListFragment.setOnBroadcastMessageSelectedListener(broadcastBrick)
                fragmentManager.beginTransaction()
                    .replace(
                        R.id.fragment_container,
                        broadcastListFragment,
                        BROADCAST_LIST_FRAGMENT_TAG
                    )
                    .addToBackStack(BROADCAST_LIST_FRAGMENT_TAG)
                    .commit()
            }
        }
    }
}
