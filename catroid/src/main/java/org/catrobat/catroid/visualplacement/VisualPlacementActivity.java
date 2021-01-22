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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
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
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.BaseCastActivity;
import org.catrobat.catroid.utils.ProjectManagerExtensionsKt;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

import static org.catrobat.catroid.content.Look.DEGREE_UI_OFFSET;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_ALL_AROUND;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_LEFT_RIGHT_ONLY;
import static org.catrobat.catroid.content.Look.ROTATION_STYLE_NONE;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_BRICK_HASH;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_ALIGNMENT;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_COLOR;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_TEXT_SIZE;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_X_TRANSFORM;
import static org.catrobat.catroid.ui.SpriteActivity.EXTRA_Y_TRANSFORM;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_CENTERED;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_LEFT;
import static org.catrobat.catroid.utils.ShowTextUtils.ALIGNMENT_STYLE_RIGHT;
import static org.catrobat.catroid.utils.ShowTextUtils.DEFAULT_TEXT_SIZE;
import static org.catrobat.catroid.utils.ShowTextUtils.DEFAULT_X_OFFSET;
import static org.catrobat.catroid.utils.ShowTextUtils.calculateAlignmentValuesForText;
import static org.catrobat.catroid.utils.ShowTextUtils.calculateColorRGBs;
import static org.catrobat.catroid.utils.ShowTextUtils.isValidColorString;
import static org.catrobat.catroid.utils.ShowTextUtils.sanitizeTextSize;

public class VisualPlacementActivity extends BaseCastActivity implements View.OnTouchListener,
		DialogInterface.OnClickListener, CoordinateInterface {

	public static final String TAG = VisualPlacementActivity.class.getSimpleName();

	public static final String X_COORDINATE_BUNDLE_ARGUMENT = "xCoordinate";
	public static final String Y_COORDINATE_BUNDLE_ARGUMENT = "yCoordinate";

	private ProjectManager projectManager;
	private FrameLayout frameLayout;
	private BitmapFactory.Options bitmapOptions;
	private ImageView imageView;

	private float xCoord;
	private float yCoord;
	private float scaleX;
	private float scaleY;
	private float rotation;
	private int rotationMode;
	private float translateX;
	private float translateY;

	private boolean isText;
	private String text;
	private String textColor;
	private int textAlignment;
	private float relativeTextSize;
	private float xOffsetText;
	private float yOffsetText;

	private float reversedRatioHeight;
	private float reversedRatioWidth;
	private int layoutWidth;
	private int layoutHeight;
	private float layoutWidthRatio;
	private float layoutHeightRatio;
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

		projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager.getCurrentProject();

		setContentView(R.layout.visual_placement_layout);
		Bundle extras = getIntent().getExtras();
		translateX = extras.getInt(EXTRA_X_TRANSFORM);
		translateY = extras.getInt(EXTRA_Y_TRANSFORM);
		if (extras.containsKey(EXTRA_TEXT)) {
			isText = true;
			text = extras.getString(EXTRA_TEXT);
			textAlignment = ALIGNMENT_STYLE_CENTERED;
			relativeTextSize = 1.0f;
			if (extras.containsKey(EXTRA_TEXT_COLOR)) {
				textColor = extras.getString(EXTRA_TEXT_COLOR);
				textAlignment = extras.getInt(EXTRA_TEXT_ALIGNMENT);
				relativeTextSize = extras.getFloat(EXTRA_TEXT_SIZE);
			}
			xOffsetText = -DEFAULT_X_OFFSET;
		}

		Toolbar toolbar = findViewById(R.id.transparent_toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.brick_option_place_visually);

		if (projectManager.isCurrentProjectLandscapeMode()) {
			setRequestedOrientation(SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			setRequestedOrientation(SCREEN_ORIENTATION_PORTRAIT);
		}
		visualPlacementTouchListener = new VisualPlacementTouchListener();

		frameLayout = findViewById(R.id.frame_container);

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

		layoutHeightRatio = (float) layoutHeight / (float) virtualScreenHeight;
		layoutWidthRatio = (float) layoutWidth / (float) virtualScreenWidth;
		reversedRatioHeight = (float) virtualScreenHeight / (float) layoutHeight;
		reversedRatioWidth = (float) virtualScreenWidth / (float) layoutWidth;

		bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

		setBackground();
		showMovableImageView();

		toolbar.bringToFront();
		frameLayout.setOnTouchListener(this);
	}

	private void setBackground() {
		try {
			Bitmap backgroundBitmap = ProjectManagerExtensionsKt.getProjectBitmap(projectManager);
			Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
					(int) (backgroundBitmap.getWidth() * layoutWidthRatio),
					(int) (backgroundBitmap.getHeight() * layoutHeightRatio), true);
			Drawable backgroundDrawable = new BitmapDrawable(getResources(), scaledBackgroundBitmap);
			backgroundDrawable.setColorFilter(Color.parseColor("#6F000000"), PorterDuff.Mode.SRC_ATOP);

			frameLayout.setBackground(backgroundDrawable);
		} catch (Exception e) {
			frameLayout.setBackgroundColor(Color.WHITE);
		}
	}

	public void showMovableImageView() {
		Bitmap visualPlacementBitmap;
		String objectLookPath;
		Sprite currentSprite = projectManager.getCurrentSprite();

		imageView = new ImageView(this);

		if (isText) {
			visualPlacementBitmap = convertTextToBitmap();
		} else {
			if (!currentSprite.look.getImagePath().isEmpty()) {
				objectLookPath = currentSprite.look.getImagePath();
				scaleX = currentSprite.look.getScaleX();
				scaleY = currentSprite.look.getScaleY();
				rotationMode = currentSprite.look.getRotationMode();
				rotation = currentSprite.look.getDirectionInUserInterfaceDimensionUnit();
				visualPlacementBitmap = BitmapFactory.decodeFile(objectLookPath, bitmapOptions);
			} else if (currentSprite.getLookList().size() != 0) {
				objectLookPath = currentSprite.getLookList().get(0).getFile().getAbsolutePath();
				visualPlacementBitmap = BitmapFactory.decodeFile(objectLookPath, bitmapOptions);
			} else {
				visualPlacementBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pc_toolbar_icon);
			}
		}

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
					matrix.postScale(-1, 1, (float) visualPlacementBitmap.getWidth() / 2, (float) visualPlacementBitmap.getHeight() / 2);
				}
				break;
		}

		visualPlacementBitmap = Bitmap.createBitmap(visualPlacementBitmap, 0, 0,
				visualPlacementBitmap.getWidth(),
				visualPlacementBitmap.getHeight(), matrix, true);

		Bitmap scaledBitmap = Bitmap.createScaledBitmap(visualPlacementBitmap, (int) (visualPlacementBitmap.getWidth() * layoutWidthRatio),
				(int) (visualPlacementBitmap.getHeight() * layoutHeightRatio), true);

		imageView.setImageBitmap(scaledBitmap);
		imageView.setScaleType(ImageView.ScaleType.CENTER);

		if (isText) {
			imageView.setTranslationX(translateX + xOffsetText);
			imageView.setTranslationY(-translateY + yOffsetText);
		} else {
			imageView.setTranslationX(translateX);
			imageView.setTranslationY(-translateY);
		}
		xCoord = translateX / reversedRatioWidth;
		yCoord = translateY / reversedRatioHeight;

		if (scaleX > 0.01) {
			imageView.setScaleX(scaleX);
		}

		if (scaleY > 0.01) {
			imageView.setScaleY(scaleY);
		}
		frameLayout.addView(imageView);
	}

	private Bitmap convertTextToBitmap() {
		Bitmap visualPlacementBitmap;

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		float textSizeInPx = sanitizeTextSize(DEFAULT_TEXT_SIZE * relativeTextSize);
		paint.setTextSize(textSizeInPx);

		if (isValidColorString(textColor)) {
			textColor = textColor.toUpperCase(Locale.getDefault());
			int[] rgb;
			rgb = calculateColorRGBs(textColor);
			paint.setColor((0xFF000000) | (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2]));
		} else {
			paint.setColor(Color.BLACK);
		}

		float baseline = -paint.ascent();

		int bitmapWidth = (int) paint.measureText(text);
		int canvasWidth = calculateAlignmentValuesForText(paint, bitmapWidth, textAlignment);
		int height = (int) (baseline + paint.descent());

		visualPlacementBitmap = Bitmap.createBitmap(bitmapWidth, height,
				Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(visualPlacementBitmap);
		canvas.drawText(text, canvasWidth,
				baseline,
				paint);

		yOffsetText = textSizeInPx - height / 2;
		switch (textAlignment) {
			case ALIGNMENT_STYLE_LEFT:
				xOffsetText += visualPlacementBitmap.getWidth() / 2;
				break;
			case ALIGNMENT_STYLE_RIGHT:
				xOffsetText -= visualPlacementBitmap.getWidth() / 2;
				break;
		}
		return visualPlacementBitmap;
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
		int xCoordinate = Math.round(xCoord * reversedRatioWidth);
		int yCoordinate = Math.round(yCoord * reversedRatioHeight);
		extras.putInt(X_COORDINATE_BUNDLE_ARGUMENT, xCoordinate);
		extras.putInt(Y_COORDINATE_BUNDLE_ARGUMENT, yCoordinate);
		returnIntent.putExtras(extras);
		setResult(Activity.RESULT_OK, returnIntent);
		finish();
	}

	private void showSaveChangesDialog(Context context) {
		new AlertDialog.Builder(context)
				.setTitle(R.string.formula_editor_discard_changes_dialog_title)
				.setMessage(R.string.formula_editor_discard_changes_dialog_message)
				.setPositiveButton(R.string.save, this)
				.setNegativeButton(R.string.discard, this)
				.setCancelable(true)
				.show();
	}

	@Override
	public void setXCoordinate(float xCoordinate) {
		if (isText) {
			xCoord = xCoordinate - xOffsetText;
		} else {
			xCoord = xCoordinate;
		}
	}

	@Override
	public void setYCoordinate(float yCoordinate) {
		if (isText) {
			yCoord = yCoordinate + yOffsetText;
		} else {
			yCoord = yCoordinate;
		}
	}
}
