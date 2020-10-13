/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
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
package org.catrobat.catroid.visualplacement;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Scene;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.BaseCastActivity;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.content.Look.DEGREE_UI_OFFSET;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_ALL_AROUND;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_LEFT_RIGHT_ONLY;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_NONE;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.common.Constants.SCREENSHOT_MANUAL_FILE_NAME;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM;

public class VisualPlacementActivity extends BaseCastActivity implements View.OnTouchListener,
		DialogInterface.OnClickListener, CoordinateInterface {

	public static final String TAG = VisualPlacementActivity.class.getSimpleName();

	public static final String X_COORDINATE_BUNDLE_ARGUMENT = "xCoordinate";
	public static final String Y_COORDINATE_BUNDLE_ARGUMENT = "yCoordinate";

	private ImageView imageView;
	private float xCoord;
	private float yCoord;
	private float scaleX;
	private float scaleY;
	private float rotation;
	private int rotationMode;
	private float translateX;
	private float translateY;

	private float reversedRatioHeight;
	private float reversedRatioWidth;
	private int layoutWidth;
	private int layoutHeight;
	private VisualPlacementTouchListener visualPlacementTouchListener;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_confirm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				break;
			case R.id.confirm:
				finishWithResult();
				break;
		}
		return true;
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (isFinishing()) {
			return;
		}

		ProjectManager projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager.getCurrentProject();
		Sprite currentSprite = projectManager.getCurrentSprite();
		Scene currentlyEditedScene = projectManager.getCurrentlyEditedScene();
		Scene currentlyPlayingScene = projectManager.getCurrentlyPlayingScene();

		setContentView(R.layout.visual_placement_layout);
		Bundle extras = getIntent().getExtras();
		translateX = extras.getInt(EXTRA_X_TRANSFORM);
		translateY = extras.getInt(EXTRA_Y_TRANSFORM);
		Toolbar toolbar = findViewById(R.id.transparent_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.brick_option_place_visually);

		File projectDir = new File(DEFAULT_ROOT_DIRECTORY, currentProject.getName());
		File sceneDir = new File(projectDir, currentlyPlayingScene.getName());
		File automaticScreenshot = new File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
		File manualScreenshot = new File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME);

		if (projectManager.isCurrentProjectLandscapeMode()) {
			setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
		}
		visualPlacementTouchListener = new VisualPlacementTouchListener();

		FrameLayout frameLayout = findViewById(R.id.frame_container);

		int screenWidth = ScreenValues.SCREEN_WIDTH;
		int screenHeight = ScreenValues.SCREEN_HEIGHT;
		int virtualScreenWidth = currentProject.getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = currentProject.getXmlHeader().virtualScreenHeight;

		float aspectRatio = (float) virtualScreenWidth / (float) virtualScreenHeight;
		float screenAspectRatio = ScreenValues.getAspectRatio();

		float scale;
		float ratioHeight = (float) screenHeight / (float) virtualScreenHeight;
		float ratioWidth = (float) screenWidth / (float) virtualScreenWidth;

		switch (currentProject.getScreenMode()) {
			case MAXIMIZE:
				if (aspectRatio < screenAspectRatio) {
					scale = ratioHeight / ratioWidth;
					layoutWidth = (int) (screenWidth * scale);
					layoutHeight = screenHeight;
				} else if (aspectRatio > screenAspectRatio) {
					scale = ratioWidth / ratioHeight;
					layoutHeight = (int) (screenHeight * scale);
					layoutWidth = screenWidth;
				} else {
					layoutHeight = screenHeight;
					layoutWidth = screenWidth;
				}
				break;
			case STRETCH:
				layoutHeight = screenHeight;
				layoutWidth = screenWidth;
				break;
		}

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = layoutWidth;
		layoutParams.height = layoutHeight;
		frameLayout.setLayoutParams(layoutParams);

		float layoutHeightRatio = (float) layoutHeight / (float) virtualScreenHeight;
		float layoutWidthRatio = (float) layoutWidth / (float) virtualScreenWidth;
		reversedRatioHeight = (float) virtualScreenHeight / (float) layoutHeight;
		reversedRatioWidth = (float) virtualScreenWidth / (float) layoutWidth;

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		try {
			String backgroundBitmapPath;
			if (automaticScreenshot.exists()) {
				backgroundBitmapPath = automaticScreenshot.getPath();
			} else if (manualScreenshot.exists()) {
				backgroundBitmapPath = manualScreenshot.getPath();
			} else {
				backgroundBitmapPath = currentlyEditedScene
						.getBackgroundSprite().getLookList().get(0).getFile().getAbsolutePath();
			}

			Bitmap backgroundBitmap = BitmapFactory.decodeFile(backgroundBitmapPath, options);
			Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
					(int) (backgroundBitmap.getWidth() * layoutWidthRatio),
					(int) (backgroundBitmap.getHeight() * layoutHeightRatio), true);
			Drawable backgroundDrawable = new BitmapDrawable(getResources(), scaledBackgroundBitmap);
			backgroundDrawable.setColorFilter(Color.parseColor("#6F000000"), PorterDuff.Mode.SRC_ATOP);

			frameLayout.setBackground(backgroundDrawable);
		} catch (Exception e) {
			frameLayout.setBackgroundColor(Color.WHITE);
		}

		Bitmap spriteBitmap;
		String objectLookPath;

		if (!currentSprite.look.getImagePath().isEmpty()) {
			objectLookPath = currentSprite.look.getImagePath();
			scaleX = currentSprite.look.getScaleX();
			scaleY = currentSprite.look.getScaleY();
			rotationMode = currentSprite.look.getRotationMode();
			rotation = currentSprite.look.getDirectionInUserInterfaceDimensionUnit();
			spriteBitmap = BitmapFactory.decodeFile(objectLookPath, options);
		} else if (currentSprite.getLookList().size() != 0) {
			objectLookPath = currentSprite.getLookList().get(0).getFile().getAbsolutePath();
			spriteBitmap = BitmapFactory.decodeFile(objectLookPath, options);
		} else {
			spriteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pc_toolbar_icon);
		}

		imageView = new ImageView(this);
		Matrix matrix = new Matrix();
		switch (rotationMode) {
			case ROTATION_STYLE_NONE:
				matrix.postRotate(0);
				break;
			case ROTATION_STYLE_ALL_AROUND:
				if (rotation != 90) {
					matrix.postRotate(rotation - DEGREE_UI_OFFSET);
				}
				break;
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				if (rotation < 0) {
					matrix.postScale(-1, 1, (float) spriteBitmap.getWidth() / 2, (float) spriteBitmap.getHeight() / 2);
				}
				break;
		}

		Bitmap bitmap = Bitmap.createBitmap(spriteBitmap, 0, 0,
				spriteBitmap.getWidth(),
				spriteBitmap.getHeight(), matrix, true);
		Bitmap scaledSpriteBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * layoutWidthRatio),
				(int) (bitmap.getHeight() * layoutHeightRatio), true);
		imageView.setImageBitmap(scaledSpriteBitmap);
		imageView.setTranslationX(translateX);
		imageView.setTranslationY(-translateY);
		xCoord = translateX / reversedRatioWidth;
		yCoord = translateY / reversedRatioHeight;

		if (scaleX > 0.01) {
			imageView.setScaleX(scaleX);
		}

		if (scaleY > 0.01) {
			imageView.setScaleY(scaleY);
		}
		imageView.setScaleType(ImageView.ScaleType.CENTER);
		frameLayout.addView(imageView);
		toolbar.bringToFront();
		frameLayout.setOnTouchListener(this);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		return visualPlacementTouchListener.onTouch(imageView, event, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	}

	@Override
	public void onBackPressed() {
		showSaveChangesDialog(this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case BUTTON_POSITIVE:
				finishWithResult();
				break;
			case BUTTON_NEGATIVE:
				ToastUtil.showError(this, R.string.formula_editor_changes_discarded);
				finish();
				break;
		}
	}

	private void finishWithResult() {
		Intent returnIntent = new Intent();
		Bundle extras = new Bundle();
		extras.putInt(EXTRA_BRICK_HASH, getIntent().getIntExtra(EXTRA_BRICK_HASH, -1));
		extras.putInt(X_COORDINATE_BUNDLE_ARGUMENT, Math.round(xCoord * reversedRatioWidth));
		extras.putInt(Y_COORDINATE_BUNDLE_ARGUMENT, Math.round(yCoord * reversedRatioHeight));
		returnIntent.putExtras(extras);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	private void showSaveChangesDialog(Context context) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.formula_editor_discard_changes_dialog_title)
				.setMessage(R.string.formula_editor_discard_changes_dialog_message)
				.setPositiveButton(R.string.save_button_text, this)
				.setNegativeButton(R.string.cancel, this)
				.setCancelable(true)
				.show();
	}

	@Override
	public void setXCoordinate(float xCoordinate) {
		xCoord = xCoordinate;
	}

	@Override
	public void setYCoordinate(float yCoordinate) {
		yCoord = yCoordinate;
	}
}
