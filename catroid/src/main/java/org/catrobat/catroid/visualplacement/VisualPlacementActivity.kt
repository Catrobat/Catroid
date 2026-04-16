/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2026 The Catrobat Team
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
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.scale
import androidx.core.graphics.toColorInt
import androidx.core.view.WindowCompat
import kotlin.math.roundToInt
import org.catrobat.catroid.ProjectManager
import org.catrobat.catroid.R
import org.catrobat.catroid.common.ScreenModes
import org.catrobat.catroid.common.ScreenValues
import org.catrobat.catroid.content.Look.DEGREE_UI_OFFSET
import org.catrobat.catroid.content.Look.ROTATION_STYLE_ALL_AROUND
import org.catrobat.catroid.content.Look.ROTATION_STYLE_LEFT_RIGHT_ONLY
import org.catrobat.catroid.content.Look.ROTATION_STYLE_NONE
import org.catrobat.catroid.ui.BaseCastActivity
import org.catrobat.catroid.ui.EdgeToEdge
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_ALIGNMENT
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_COLOR
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_SIZE
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM
import org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM
import org.catrobat.catroid.utils.Resolution
import org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_CENTERED
import org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_LEFT
import org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_RIGHT
import org.catrobat.catroid.utils.ShowTextUtils.DEFAULT_TEXT_SIZE
import org.catrobat.catroid.utils.ShowTextUtils.DEFAULT_X_OFFSET
import org.catrobat.catroid.utils.ShowTextUtils.calculateAlignmentValuesForText
import org.catrobat.catroid.utils.ShowTextUtils.calculateColorRGBs
import org.catrobat.catroid.utils.ShowTextUtils.isValidColorString
import org.catrobat.catroid.utils.ShowTextUtils.sanitizeTextSize
import org.catrobat.catroid.utils.ToastUtil
import org.catrobat.catroid.utils.getProjectBitmap
import java.util.Locale

class VisualPlacementActivity :
    BaseCastActivity(),
    View.OnTouchListener,
    DialogInterface.OnClickListener,
    CoordinateInterface {

    companion object {
        val TAG: String = VisualPlacementActivity::class.java.simpleName

        const val X_COORDINATE_BUNDLE_ARGUMENT = "xCoordinate"
        const val Y_COORDINATE_BUNDLE_ARGUMENT = "yCoordinate"
        const val CHANGED_COORDINATES = "changedCoordinates"

        private const val ROTATION_FACING_RIGHT = 90f
        private const val MINIMUM_SCALE_THRESHOLD = 0.01f
        private const val FULL_ALPHA_MASK = -0x1000000 // 0xFF000000 as signed Int
        private const val RED_CHANNEL_SHIFT = 16
        private const val GREEN_CHANNEL_SHIFT = 8
    }

    private lateinit var projectManager: ProjectManager
    private lateinit var frameLayout: FrameLayout
    private lateinit var imageView: ImageView
    private lateinit var visualPlacementTouchListener: VisualPlacementTouchListener
    private lateinit var layoutResolution: Resolution

    private val bitmapOptions = BitmapFactory.Options().apply {
        inPreferredConfig = Bitmap.Config.ARGB_8888
    }

    private var xCoord = 0f
    private var yCoord = 0f
    private var scaleX = 0f
    private var scaleY = 0f
    private var rotation = 0f
    private var rotationMode = 0
    private var translateX = 0f
    private var translateY = 0f
    private var layoutWidthRatio = 0f
    private var layoutHeightRatio = 0f

    private var isText = false
    private var text: String? = null
    private var textColor: String? = null
    private var textAlignment = ALIGNMENT_STYLE_CENTERED
    private var relativeTextSize = 1.0f
    private var xOffsetText = 0f
    private var yOffsetText = 0f

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_confirm, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.confirm -> finishWithResult()
        }
        return true
    }

    // ClickableViewAccessibility: performClick() is called from onTouch() on ACTION_UP,
    // satisfying the accessibility contract even though frameLayout is not a custom subclass.
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(null)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (isFinishing) return

        projectManager = ProjectManager.getInstance()
        val currentProject = projectManager.currentProject ?: run {
            finish()
            return
        }

        setContentView(R.layout.visual_placement_layout)
        hideSystemUI()

        val extras = intent.extras ?: run {
            finish()
            return
        }

        translateX = extras.getInt(EXTRA_X_TRANSFORM).toFloat()
        translateY = extras.getInt(EXTRA_Y_TRANSFORM).toFloat()

        if (extras.containsKey(EXTRA_TEXT)) {
            isText = true
            text = extras.getString(EXTRA_TEXT)
            textAlignment = ALIGNMENT_STYLE_CENTERED
            relativeTextSize = 1.0f
            if (extras.containsKey(EXTRA_TEXT_COLOR)) {
                textColor = extras.getString(EXTRA_TEXT_COLOR)
                textAlignment = extras.getInt(EXTRA_TEXT_ALIGNMENT)
                relativeTextSize = extras.getFloat(EXTRA_TEXT_SIZE)
            }
            xOffsetText = -DEFAULT_X_OFFSET
        }

        val toolbar = findViewById<Toolbar>(R.id.transparent_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setTitle(R.string.brick_option_place_visually)
        }
        EdgeToEdge.applyTopPadding(toolbar)

        requestedOrientation = if (projectManager.isCurrentProjectLandscapeMode()) {
            SCREEN_ORIENTATION_LANDSCAPE
        } else {
            SCREEN_ORIENTATION_PORTRAIT
        }

        visualPlacementTouchListener = VisualPlacementTouchListener()
        frameLayout = findViewById(R.id.frame_container)

        val projectResolution = Resolution(
            currentProject.xmlHeader.virtualScreenWidth,
            currentProject.xmlHeader.virtualScreenHeight
        )
        val fullscreenWindowResolution = getFullscreenWindowResolution(projectResolution)

        layoutResolution = when (currentProject.screenMode) {
            ScreenModes.MAXIMIZE -> projectResolution.resizeToFit(fullscreenWindowResolution)
            ScreenModes.STRETCH -> fullscreenWindowResolution
            else -> projectResolution.resizeToFit(fullscreenWindowResolution)
        }

        frameLayout.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
            width = layoutResolution.width
            height = layoutResolution.height
        }

        layoutHeightRatio = layoutResolution.height.toFloat() / projectResolution.height.toFloat()
        layoutWidthRatio = layoutResolution.width.toFloat() / projectResolution.width.toFloat()

        setBackground()
        showMovableImageView()

        toolbar.bringToFront()
        frameLayout.setOnTouchListener(this)
    }

    @Suppress("TooGenericExceptionCaught")
    private fun setBackground() {
        try {
            val backgroundBitmap = projectManager.getProjectBitmap()
            val scaledBitmap = backgroundBitmap.scale(
                (backgroundBitmap.width * layoutWidthRatio).toInt(),
                (backgroundBitmap.height * layoutHeightRatio).toInt()
            )
            frameLayout.background = scaledBitmap.toDrawable(resources).apply {
                setColorFilter("#6F000000".toColorInt(), PorterDuff.Mode.SRC_ATOP)
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load background bitmap", e)
            frameLayout.setBackgroundColor(Color.WHITE)
        }
    }

    fun showMovableImageView() {
        imageView = ImageView(this)

        val visualPlacementBitmap = if (isText) {
            convertTextToBitmap()
        } else {
            loadSpriteBitmap()
        }

        val matrix = Matrix()
        when (rotationMode) {
            ROTATION_STYLE_NONE -> Unit
            ROTATION_STYLE_ALL_AROUND -> if (rotation != ROTATION_FACING_RIGHT) {
                matrix.postRotate(rotation - DEGREE_UI_OFFSET)
            }
            ROTATION_STYLE_LEFT_RIGHT_ONLY -> if (rotation < 0) {
                matrix.postScale(
                    -1f, 1f,
                    visualPlacementBitmap.width / 2f,
                    visualPlacementBitmap.height / 2f
                )
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            visualPlacementBitmap, 0, 0,
            visualPlacementBitmap.width, visualPlacementBitmap.height,
            matrix, true
        )
        val scaledBitmap = rotatedBitmap.scale(
            (rotatedBitmap.width * layoutWidthRatio).toInt(),
            (rotatedBitmap.height * layoutHeightRatio).toInt()
        )

        imageView.setImageBitmap(scaledBitmap)
        imageView.scaleType = ImageView.ScaleType.CENTER

        if (isText) {
            imageView.translationX = translateX + xOffsetText
            imageView.translationY = -translateY + yOffsetText
        } else {
            imageView.translationX = translateX
            imageView.translationY = -translateY
        }

        xCoord = translateX * layoutWidthRatio
        yCoord = translateY * layoutHeightRatio

        if (scaleX > MINIMUM_SCALE_THRESHOLD) imageView.scaleX = scaleX
        if (scaleY > MINIMUM_SCALE_THRESHOLD) imageView.scaleY = scaleY

        frameLayout.addView(imageView)
    }

    private fun loadSpriteBitmap(): Bitmap {
        val currentSprite = projectManager.currentSprite
        val imagePath = currentSprite.look.imagePath
        if (imagePath.isNotEmpty()) {
            scaleX = currentSprite.look.scaleX
            scaleY = currentSprite.look.scaleY
            rotationMode = currentSprite.look.rotationMode
            rotation = currentSprite.look.motionDirectionInUserInterfaceDimensionUnit
            return BitmapFactory.decodeFile(imagePath, bitmapOptions)
        }
        if (currentSprite.lookList.isNotEmpty()) {
            return BitmapFactory.decodeFile(
                currentSprite.lookList[0].file.absolutePath,
                bitmapOptions
            )
        }
        val drawable = ContextCompat.getDrawable(applicationContext, R.drawable.pc_toolbar_icon)
            ?: return createBitmap(1, 1)
        return createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight
        ).also { bitmap ->
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
        }
    }

    private fun convertTextToBitmap(): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val textSizeInPx = sanitizeTextSize(DEFAULT_TEXT_SIZE * relativeTextSize)
        paint.textSize = textSizeInPx

        val colorString = textColor
        if (colorString != null && isValidColorString(colorString)) {
            val rgb = calculateColorRGBs(colorString.uppercase(Locale.getDefault()))
            paint.color = FULL_ALPHA_MASK or (rgb[0] shl RED_CHANNEL_SHIFT) or (rgb[1] shl GREEN_CHANNEL_SHIFT) or rgb[2]
        } else {
            paint.color = Color.BLACK
        }

        val baseline = -paint.ascent()
        val safeText = text.orEmpty()
        val bitmapWidth = maxOf(1, paint.measureText(safeText).toInt())
        val canvasWidth = calculateAlignmentValuesForText(paint, bitmapWidth, textAlignment)
        val height = maxOf(1, (baseline + paint.descent()).toInt())

        val bitmap = createBitmap(bitmapWidth, height)
        Canvas(bitmap).drawText(safeText, canvasWidth.toFloat(), baseline, paint)

        yOffsetText = textSizeInPx - height / 2f
        when (textAlignment) {
            ALIGNMENT_STYLE_LEFT -> xOffsetText += bitmap.width / 2f
            ALIGNMENT_STYLE_RIGHT -> xOffsetText -= bitmap.width / 2f
        }
        return bitmap
    }

    override fun onTouch(view: View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            view.performClick()
        }
        return visualPlacementTouchListener.onTouch(imageView, event, this)
    }

    override fun onStart() {
        super.onStart()
        hideSystemUI()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) hideSystemUI()
    }

    /**
     * Hides both the status bar and the navigation bar to ensure the visible
     * viewport matches the project's coordinate system (fix for CATROID-1648).
     *
     * On some devices (e.g. Xiaomi 11T / Android 14) the navigation bar would
     * appear even after FLAG_FULLSCREEN was set, shifting the viewport upward
     * and causing a mismatch between the visually placed position and the
     * actual sprite position at runtime.
     */
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or
                View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )
        }
    }

    private fun getFullscreenWindowResolution(projectResolution: Resolution): Resolution {
        val fullscreenResolution = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            Resolution(bounds.width(), bounds.height())
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
            Resolution(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }

        return fullscreenResolution.flipToFit(projectResolution)
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        val xCoordinate = (xCoord / layoutWidthRatio).roundToInt()
        val yCoordinate = (yCoord / layoutHeightRatio).roundToInt()
        if (translateX.toInt() != xCoordinate || translateY.toInt() != yCoordinate) {
            showSaveChangesDialog(this)
        } else {
            finish()
        }
    }

    override fun onClick(dialog: DialogInterface, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> finishWithResult()
            DialogInterface.BUTTON_NEGATIVE -> {
                ToastUtil.showError(this, R.string.formula_editor_changes_discarded)
                finish()
            }
        }
    }

    private fun finishWithResult() {
        val xCoordinate = (xCoord / layoutWidthRatio).roundToInt()
        val yCoordinate = (yCoord / layoutHeightRatio).roundToInt()
        val returnIntent = Intent()
        returnIntent.putExtra(EXTRA_BRICK_HASH, intent.getIntExtra(EXTRA_BRICK_HASH, -1))
        returnIntent.putExtra(X_COORDINATE_BUNDLE_ARGUMENT, xCoordinate)
        returnIntent.putExtra(Y_COORDINATE_BUNDLE_ARGUMENT, yCoordinate)
        returnIntent.putExtra(
            CHANGED_COORDINATES,
            translateX.toInt() != xCoordinate || translateY.toInt() != yCoordinate
        )
        setResult(Activity.RESULT_OK, returnIntent)
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

    override fun setXCoordinate(xCoordinate: Float) {
        xCoord = if (isText) xCoordinate - xOffsetText else xCoordinate
    }

    override fun setYCoordinate(yCoordinate: Float) {
        yCoord = if (isText) yCoordinate + yOffsetText else yCoordinate
    }
}
