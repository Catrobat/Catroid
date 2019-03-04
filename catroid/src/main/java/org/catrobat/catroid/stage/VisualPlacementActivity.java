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
package org.catrobat.catroid.stage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
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
import org.catrobat.catroid.common.ScreenModes;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.BaseCastActivity;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.common.ScreenModes.MAXIMIZE;
import static org.catrobat.catroid.content.Look.DEGREE_UI_OFFSET;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_ALL_AROUND;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_LEFT_RIGHT_ONLY;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_NONE;
import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_MANUAL_FILE_NAME;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;

public class VisualPlacementActivity extends BaseCastActivity implements View.OnTouchListener,
		DialogInterface.OnClickListener {

	public static final String TAG = VisualPlacementActivity.class.getSimpleName();

	public static final String X_COORDINATE_BUNDLE_ARGUMENT = "xCoordinate";
	public static final String Y_COORDINATE_BUNDLE_ARGUMENT = "yCoordinate";

	private File projectDir = new File(DEFAULT_ROOT_DIRECTORY,
			ProjectManager.getInstance().getCurrentProject().getName());
	private File sceneDir = new File(projectDir,
			ProjectManager.getInstance().getCurrentlyPlayingScene().getName());
	private File automaticScreenshot = new File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
	private File manualScreenshot = new File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME);

	private ImageView imageView;
	private float xCoord;
	private float yCoord;
	private float scaleX;
	private float scaleY;
	private float rotation;
	private int rotationMode;
	private float startX;
	private float startY;
	private float previousY;
	private float previousX;
	private float reversedScaleHeightRatio;
	private float reversedScaleWidthRatio;
	private int maximizeLayoutWidth;
	private int maximizeLayoutHeight;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_visual_placement, menu);
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
		setContentView(R.layout.visual_placement_layout);
		Toolbar toolbar = findViewById(R.id.transparent_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.brick_place_at_option_place_visually);

		if (ProjectManager.getInstance().isCurrentProjectLandscapeMode()) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		FrameLayout frameLayout = findViewById(R.id.frame_container);
		Project currentProject = ProjectManager.getInstance().getCurrentProject();
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		int currentScreenWidth = getResources().getDisplayMetrics().widthPixels;
		int currentScreenHeight = getResources().getDisplayMetrics().heightPixels;
		int virtualScreenWidth = currentProject.getXmlHeader().virtualScreenWidth;
		int virtualScreenHeight = currentProject.getXmlHeader().virtualScreenHeight;

		final float scaleHeightRatio = (float) currentScreenHeight / (float) virtualScreenHeight;
		reversedScaleHeightRatio = (float) virtualScreenHeight / (float) currentScreenHeight;
		final float scaleWidthRatio = (float) currentScreenWidth / (float) virtualScreenWidth;
		reversedScaleWidthRatio = (float) virtualScreenWidth / (float) currentScreenWidth;

		ScreenModes projectScreenMood = currentProject.getXmlHeader().getScreenMode();
		if (projectScreenMood == MAXIMIZE) {
			float aspectRatio = (float) virtualScreenWidth / (float) virtualScreenHeight;
			float screenAspectRatio = (float) currentScreenWidth / (float) currentScreenHeight;
			float scale;
			if (aspectRatio < screenAspectRatio) {
				scale = scaleHeightRatio / scaleWidthRatio;
				maximizeLayoutWidth = (int) (currentScreenWidth * scale);
				maximizeLayoutHeight = currentScreenHeight;
			} else if (aspectRatio > screenAspectRatio) {
				scale = scaleWidthRatio / scaleHeightRatio;
				maximizeLayoutHeight = (int) (currentScreenHeight * scale);
				maximizeLayoutWidth = currentScreenWidth;
			} else {
				maximizeLayoutHeight = currentScreenHeight;
				maximizeLayoutWidth = currentScreenWidth;
			}
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.CENTER;
			layoutParams.width = maximizeLayoutWidth;
			layoutParams.height = maximizeLayoutHeight;
			frameLayout.setLayoutParams(layoutParams);
		}

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;

		try {
			String backgroundBitmapPath;
			if (automaticScreenshot.exists()) {
				backgroundBitmapPath = automaticScreenshot.getPath();
			} else if (manualScreenshot.exists()) {
				backgroundBitmapPath = manualScreenshot.getPath();
			} else {
				backgroundBitmapPath = ProjectManager.getInstance().getCurrentlyEditedScene()
						.getBackgroundSprite().getLookList().get(0).getFile().getAbsolutePath();
			}

			Bitmap backgroundBitmap = BitmapFactory.decodeFile(backgroundBitmapPath, options);
			Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
					(int) (backgroundBitmap.getWidth() * scaleWidthRatio),
					(int) (backgroundBitmap.getHeight() * scaleHeightRatio), true);
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
			spriteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		}

		Bitmap scaledSpriteBitmap = Bitmap.createScaledBitmap(spriteBitmap,
				(int) (spriteBitmap.getWidth() * scaleWidthRatio),
				(int) (spriteBitmap.getHeight() * scaleHeightRatio), true);
		imageView = new ImageView(this);

		switch (rotationMode) {
			case ROTATION_STYLE_NONE:
				imageView.setRotation(0f);
				break;
			case ROTATION_STYLE_ALL_AROUND:
				if (rotation != 90) {
					imageView.setRotation(rotation - DEGREE_UI_OFFSET);
				}
				break;
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				if (rotation < 0) {
					scaledSpriteBitmap = createFlippedBitmap(scaledSpriteBitmap);
				}
				break;
		}
		imageView.setImageBitmap(scaledSpriteBitmap);

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
		float currentX = event.getRawX();
		float currentY = event.getRawY();
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				previousX = currentX;
				previousY = currentY;
				startX = currentX;
				startY = currentY;
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				if (Math.abs(currentX - startX) < 10 && Math.abs(currentY - startY) < 10) {
					imageView.animate()
							.translationX(event.getX() - (float) view.getWidth() / 2)
							.translationY(event.getY() - (float) view.getHeight() / 2)
							.setDuration(0)
							.start();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				float dX = currentX - previousX;
				float dY = currentY - previousY;
				imageView.setX(imageView.getX() + dX);
				imageView.setY(imageView.getY() + dY);
				previousX = currentX;
				previousY = currentY;
				break;
		}
		xCoord = imageView.getX();
		yCoord = -imageView.getY();
		return true;
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
		extras.putInt(X_COORDINATE_BUNDLE_ARGUMENT, Math.round(xCoord * reversedScaleWidthRatio));
		extras.putInt(Y_COORDINATE_BUNDLE_ARGUMENT, Math.round(yCoord * reversedScaleHeightRatio));
		returnIntent.putExtras(extras);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	private void showSaveChangesDialog(Context context) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.formula_editor_discard_changes_dialog_title)
				.setMessage(R.string.formula_editor_discard_changes_dialog_message)
				.setPositiveButton(R.string.yes, this)
				.setNegativeButton(R.string.no, this)
				.setCancelable(true)
				.show();
	}

	private Bitmap createFlippedBitmap(Bitmap source) {
		Matrix matrix = new Matrix();
		matrix.postScale(-1, 1, (float) source.getWidth() / 2, (float) source.getHeight() / 2);
		return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
}
