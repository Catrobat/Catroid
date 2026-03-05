package org.catrobat.catroid.ui.recyclerview.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.launcher.LauncherIconCache
import org.catrobat.catroid.ui.launcher.ProjectLauncherIconProvider
import java.io.File

class LauncherIconPreviewDialog : DialogFragment() {

    var iconProvider: ProjectLauncherIconProvider = ProjectLauncherIconProvider()
    var iconCache: LauncherIconCache = sharedCache

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val projectName = requireArguments().getString(ARG_PROJECT_NAME).orEmpty()
        val projectDirPath = requireArguments().getString(ARG_PROJECT_DIR_PATH).orEmpty()
        val projectDir = File(projectDirPath)

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_launcher_icon_preview, null)

        val iconImageView: ImageView = view.findViewById(R.id.launcher_icon_image)
        val nameTextView: TextView = view.findViewById(R.id.launcher_icon_project_name)

        nameTextView.text = projectName

        lifecycleScope.launch {

            val icon = withContext(Dispatchers.IO) {

                iconCache.get(projectDir)
                    ?: iconProvider.getIconForProject(projectDir).also { bmp ->
                        iconCache.put(projectDir, bmp)
                    }
            }

            if (isAdded) {
                iconImageView.setImageBitmap(icon)
            }
        }

        return AlertDialog.Builder(requireContext())
            .setView(view)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    companion object {

        const val TAG = "LauncherIconPreviewDialog"

        private const val ARG_PROJECT_NAME = "projectName"
        private const val ARG_PROJECT_DIR_PATH = "projectDirPath"

        val sharedCache = LauncherIconCache()

        fun newInstance(
            projectName: String,
            projectDir: File
        ): LauncherIconPreviewDialog {

            val dialog = LauncherIconPreviewDialog()

            val bundle = Bundle()
            bundle.putString(ARG_PROJECT_NAME, projectName)
            bundle.putString(ARG_PROJECT_DIR_PATH, projectDir.absolutePath)

            dialog.arguments = bundle

            return dialog
        }
    }
}