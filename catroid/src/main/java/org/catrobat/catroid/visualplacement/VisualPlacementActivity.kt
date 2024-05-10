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

package org.catrobat.catroid.visualplacement

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.ui.BaseCastActivity
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH
import org.catrobat.catroid.utils.AndroidCoordinates
import org.catrobat.catroid.utils.GameCoordinates
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.getProjectBitmap
import org.catrobat.catroid.visualplacement.model.DrawableSprite
import org.koin.android.ext.android.inject

open class VisualPlacementActivity :
    BaseCastActivity(), OnTouchListener, DialogInterface.OnClickListener {

    private val projectManager: ProjectManager by inject()
    private val touchListener: VisualPlacementTouchListener by inject()
    private val layoutCalculator: LayoutComputer by inject()

    private lateinit var viewModel: VisualPlacementViewModel
    private lateinit var frameLayout: FrameLayout
    private lateinit var imageViewToMove: ImageView
    private lateinit var toolbar: Toolbar

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_confirm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.confirm -> saveAndFinish()
        }
        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    @Suppress("MagicNumber")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)

        if (isFinishing) return

        setContentView(R.layout.visual_placement_layout)

        toolbar = findViewById(R.id.transparent_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setTitle(R.string.brick_option_place_visually)
        requestedOrientation = if (projectManager.isCurrentProjectLandscapeMode) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        frameLayout = findViewById(R.id.frame_container)
        frameLayout.layoutParams = layoutCalculator.getLayoutParams()
        frameLayout.setOnTouchListener(this)
        //frameLayout.setBackgroundColor(Color.WHITE)
        setBackground()

        viewModel = ViewModelProvider(this)[VisualPlacementViewModel::class.java]
        subscribeToViewModel()

        viewModel.drawAllSprites()
    }

    private fun subscribeToViewModel() {
        viewModel.drawableSprites.observe(this) { spriteList ->
            spriteList.forEach { drawImage(it) }
        }

        viewModel.spriteToPlace.observe(this) {
            if (!::imageViewToMove.isInitialized) {
                imageViewToMove = drawImage(it)
                toolbar.bringToFront()
            }
        }
    }

    private fun setBackground() {
        try {
            val backgroundBitmap = projectManager.getProjectBitmap()
            backgroundBitmap.eraseColor(Color.WHITE)
            val scaledBackgroundBitmap = Bitmap.createScaledBitmap(
                backgroundBitmap,
                layoutCalculator.getLayoutParams().width,
                layoutCalculator.getLayoutParams().height,
                true
            )
            val backgroundDrawable: Drawable = BitmapDrawable(resources, scaledBackgroundBitmap)
            backgroundDrawable.colorFilter = PorterDuffColorFilter(Color.parseColor("#6F000000"),
                                                                   PorterDuff.Mode.SRC_ATOP)
            frameLayout.background = backgroundDrawable
        } catch (e: Exception) {
            frameLayout.setBackgroundColor(Color.WHITE)
        }
    }

    private fun drawImage(image: DrawableSprite): ImageView {
        val view = ImageView(this)
        view.setImageDrawable(image.drawable)
        view.setPosition(image.coordinates)
        view.scaleType = ImageView.ScaleType.CENTER
        if (hasChanged(image.scalingFactor.width)) {
            view.scaleX = image.scalingFactor.width
        }
        if (hasChanged(image.scalingFactor.height)) {
            view.scaleY = image.scalingFactor.height
        }
        frameLayout.addView(view)
        return view
    }

    @Suppress("MagicNumber")
    private fun hasChanged(valueToCheck: Float) = valueToCheck > 0.01

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(view: View, event: MotionEvent): Boolean =
        touchListener.onTouch(
            viewModel, imageViewToMove, viewModel.spriteToPlace.value!!.coordinates
                .toAndroidCoordinates(viewModel.layoutSize), event
        )

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    override fun onBackPressed() {
        if (viewModel.imageWasMoved) {
            showSaveChangesDialog(this)
        } else finish()
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> saveAndFinish()
            DialogInterface.BUTTON_NEGATIVE -> {
                ToastUtil.showError(this, R.string.formula_editor_changes_discarded)
                finish()
            }
        }
    }

    private fun saveAndFinish() {
        setResult(
            RESULT_OK,
            Intent().putExtras(viewModel.save(intent.getIntExtra(EXTRA_BRICK_HASH, -1)))
        )
        finish()
    }

    private fun showSaveChangesDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(R.string.formula_editor_discard_changes_dialog_title)
            .setMessage(R.string.formula_editor_discard_changes_dialog_message)
            .setPositiveButton(R.string.save, this)
            .setNegativeButton(R.string.discard, this)
            .setCancelable(true)
            .show()
    }

    fun setCoordinates(screenTapCoordinates: AndroidCoordinates) {
        viewModel.setCoordinates(screenTapCoordinates, imageViewToMove)
    }

    private fun ImageView.setPosition(centeredCoordinates: GameCoordinates) {
        this.x = centeredCoordinates.x
        this.y = centeredCoordinates.y
    }
}
