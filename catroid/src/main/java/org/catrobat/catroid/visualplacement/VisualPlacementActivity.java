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
import android.util.DisplayMetrics;
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
import org.catrobat.catroid.utils.ScreenValueHandler;
import org.catrobat.catroid.content.Project;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.BaseCastActivity;
import org.catrobat.catroid.utils.ProjectManagerExtensionsKt;
import org.catrobat.catroid.utils.Resolution;
import org.catrobat.catroid.utils.ToastUtil;

import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

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
		DialogInterface.OnClickListener, TransformationInterface {

	public static final String TAG = VisualPlacementActivity.class.getSimpleName();

	public static final String X_COORDINATE_BUNDLE_ARGUMENT = "xCoordinate";
	public static final String Y_COORDINATE_BUNDLE_ARGUMENT = "yCoordinate";
	public static final String SCALE_BUNDLE_ARGUMENT = "scaleFactor";
	public static final String ROTATION_BUNDLE_ARGUMENT = "rotationDegrees";
	public static final String CHANGED_COORDINATES = "changedCoordinates";

	private ProjectManager projectManager;
	private FrameLayout frameLayout;
	private BitmapFactory.Options bitmapOptions;
	private ImageView imageView;

	private float xCoord;
	private float yCoord;
	private float scaleX;
	private float scaleY;
	private float initialLookRotation;
	private int rotationMode;
	private float translateX;
	private float translateY;

	private float scaleFactor = 1.0f;
	private float rotation = 0.0f;
	private float initialSpriteScale = 1.0f;
	private float initialSpriteRotation = DEGREE_UI_OFFSET;

	private boolean isText;
	private String text;
	private String textColor;
	private int textAlignment;
	private float relativeTextSize;
	private float xOffsetText;
	private float yOffsetText;
	private Resolution layoutResolution;
	private float layoutWidthRatio;
	private float layoutHeightRatio;
	private VisualPlacementTouchListener visualPlacementTouchListener;
	private ResizeRotateGestureDetector resizeRotateDetector;
	private BoundingBoxOverlay boundingBoxOverlay;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_confirm, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			onBackPressed();
		} else if (itemId == R.id.confirm) {
			finishWithResult();
		} else if (itemId == R.id.reset) {
			resetTransformations();
		} else if (itemId == R.id.rotate_90) {
			rotateBy90Degrees();
		}
		return true;
	}

	private void resetTransformations() {
		setupImageViewPositionAndScale();
		if (boundingBoxOverlay != null) {
			boundingBoxOverlay.updateOverlay();
		}
	}

	private void rotateBy90Degrees() {
		if (imageView == null) {
			return;
		}
		rotation += 90f;
		imageView.setRotation(rotation);
		resizeRotateDetector.setCumulativeRotation(rotation);
		if (boundingBoxOverlay != null) {
			boundingBoxOverlay.updateOverlay();
		}
	}

	private void updateCoordinatesFromView() {
		if (imageView == null) {
			return;
		}
		setXCoordinate(imageView.getX());
		setYCoordinate(imageView.getY());
	}

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(null);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		if (isFinishing()) {
			return;
		}

		projectManager = ProjectManager.getInstance();
		Project currentProject = projectManager.getCurrentProject();
		if (currentProject == null || projectManager.getCurrentSprite() == null) {
			finish();
			return;
		}

		setContentView(R.layout.visual_placement_layout);
		Bundle extras = getIntent().getExtras();
		if (extras == null) {
			finish();
			return;
		}
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
		ScreenValueHandler.updateScreenWidthAndHeight(this);

		resizeRotateDetector = new ResizeRotateGestureDetector(new ResizeRotateGestureDetector.OnTransformGestureListener() {
			@Override
			public void onScale(float scale) {
				scaleFactor = scale;
				if (imageView != null) {
					float appliedScale = initialSpriteScale * scaleFactor;
					imageView.setScaleX(appliedScale);
					imageView.setScaleY(appliedScale);
				}
			}

			@Override
			public void onRotate(float rotation) {
				VisualPlacementActivity.this.rotation = rotation;
				if (imageView != null) {
					imageView.setRotation(VisualPlacementActivity.this.rotation);
				}
			}

			@Override
			public void onPan(float dx, float dy) {
				if (imageView != null) {
					imageView.setX(imageView.getX() + dx);
					imageView.setY(imageView.getY() + dy);
					updateCoordinatesFromView();
				}
			}
		});

		visualPlacementTouchListener = new VisualPlacementTouchListener();
		visualPlacementTouchListener.setResizeRotateDetector(resizeRotateDetector);

		frameLayout = findViewById(R.id.frame_container);

		Resolution projectResolution = new Resolution(
				currentProject.getXmlHeader().virtualScreenWidth,
				currentProject.getXmlHeader().virtualScreenHeight);

		switch (currentProject.getScreenMode()) {
			case MAXIMIZE:
				layoutResolution = projectResolution.resizeToFit(ScreenValues.currentScreenResolution);
				break;
			case STRETCH:
				layoutResolution = ScreenValues.currentScreenResolution;
				break;
		}

		LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
		layoutParams.gravity = Gravity.CENTER;
		layoutParams.width = layoutResolution.getWidth();
		layoutParams.height = layoutResolution.getHeight();
		frameLayout.setLayoutParams(layoutParams);

		layoutHeightRatio = (float) layoutResolution.getHeight() / (float) projectResolution.getHeight();
		layoutWidthRatio = (float) layoutResolution.getWidth() / (float) projectResolution.getWidth();

		bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

		setBackground();
		showMovableImageView();

		boundingBoxOverlay = new BoundingBoxOverlay(this);
		boundingBoxOverlay.setTrackedImageView(imageView);
		frameLayout.addView(boundingBoxOverlay,
				new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.MATCH_PARENT));

		visualPlacementTouchListener.setBoundingBoxOverlay(boundingBoxOverlay);

		toolbar.bringToFront();
		frameLayout.setOnTouchListener(this);
	}

	private void setBackground() {
		try {
			Bitmap backgroundBitmap = ProjectManagerExtensionsKt.getProjectBitmap(projectManager);
			if (backgroundBitmap != null) {
				Bitmap scaledBackgroundBitmap = Bitmap.createScaledBitmap(backgroundBitmap,
						(int) (backgroundBitmap.getWidth() * layoutWidthRatio),
						(int) (backgroundBitmap.getHeight() * layoutHeightRatio), true);
				Drawable backgroundDrawable = new BitmapDrawable(getResources(), scaledBackgroundBitmap);
				backgroundDrawable.setColorFilter(Color.parseColor("#6F000000"), PorterDuff.Mode.SRC_ATOP);

				frameLayout.setBackground(backgroundDrawable);
				if (backgroundBitmap != scaledBackgroundBitmap) {
					backgroundBitmap.recycle();
				}
			}
		} catch (Exception e) {
			frameLayout.setBackgroundColor(Color.WHITE);
		}
	}

	public void showMovableImageView() {
		imageView = new ImageView(this);
		Bitmap visualPlacementBitmap = loadBitmapForPlacement();

		if (visualPlacementBitmap != null) {
			visualPlacementBitmap = applyInitialBitmapTransformations(visualPlacementBitmap);
			imageView.setImageBitmap(visualPlacementBitmap);
		}

		setupImageViewPositionAndScale();
		frameLayout.addView(imageView);
	}

	private Bitmap loadBitmapForPlacement() {
		Bitmap bitmap = null;
		Sprite currentSprite = projectManager.getCurrentSprite();

		if (isText) {
			bitmap = convertTextToBitmap();
		} else if (currentSprite.look != null && !currentSprite.look.getImagePath().isEmpty()) {
			String objectLookPath = currentSprite.look.getImagePath();
			scaleX = currentSprite.look.getScaleX();
			scaleY = currentSprite.look.getScaleY();
			rotationMode = currentSprite.look.getRotationMode();
			initialLookRotation = currentSprite.look.getMotionDirectionInUserInterfaceDimensionUnit();
			bitmap = BitmapFactory.decodeFile(objectLookPath, bitmapOptions);
		} else if (currentSprite.getLookList() != null && !currentSprite.getLookList().isEmpty()
				&& currentSprite.getLookList().get(0).getFile() != null) {
			String path = currentSprite.getLookList().get(0).getFile().getAbsolutePath();
			rotationMode = ROTATION_STYLE_ALL_AROUND;
			initialLookRotation = DEGREE_UI_OFFSET;
			bitmap = BitmapFactory.decodeFile(path, bitmapOptions);
		}

		if (bitmap == null) {
			bitmap = loadDefaultToolbarIcon();
		}
		return bitmap;
	}

	private Bitmap loadDefaultToolbarIcon() {
		Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.pc_toolbar_icon);
		if (drawable == null) {
			return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
		}
		int width = Math.max(1, drawable.getIntrinsicWidth());
		int height = Math.max(1, drawable.getIntrinsicHeight());
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}

	private Bitmap applyInitialBitmapTransformations(Bitmap bitmap) {
		Matrix matrix = createInitialRotationMatrix(bitmap);
		Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		if (bitmap != rotatedBitmap) {
			bitmap.recycle();
		}
		bitmap = rotatedBitmap;

		Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * layoutWidthRatio),
				(int) (bitmap.getHeight() * layoutHeightRatio), true);
		if (bitmap != scaledBitmap) {
			bitmap.recycle();
		}
		return scaledBitmap;
	}

	private Matrix createInitialRotationMatrix(Bitmap bitmap) {
		Matrix matrix = new Matrix();
		switch (rotationMode) {
			case ROTATION_STYLE_NONE:
				matrix.postRotate(0);
				break;
			case ROTATION_STYLE_ALL_AROUND:
				if (initialLookRotation != 90) {
					matrix.postRotate(initialLookRotation - DEGREE_UI_OFFSET);
				}
				break;
			case ROTATION_STYLE_LEFT_RIGHT_ONLY:
				if (initialLookRotation < 0) {
					matrix.postScale(-1, 1, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
				}
				break;
		}
		return matrix;
	}

	private void setupImageViewPositionAndScale() {
		imageView.setScaleType(ImageView.ScaleType.CENTER);
		if (isText) {
			imageView.setTranslationX(translateX + xOffsetText);
			imageView.setTranslationY(-translateY + yOffsetText);
		} else {
			imageView.setTranslationX(translateX);
			imageView.setTranslationY(-translateY);
		}
		xCoord = translateX * layoutWidthRatio;
		yCoord = translateY * layoutHeightRatio;

		if (scaleX > 0.01) {
			imageView.setScaleX(scaleX);
			initialSpriteScale = scaleX;
		}
		if (scaleY > 0.01) {
			imageView.setScaleY(scaleY);
		}
		initialSpriteRotation = initialLookRotation;

		resizeRotateDetector.setCumulativeScale(1.0f);
		resizeRotateDetector.setCumulativeRotation(0.0f);
		scaleFactor = 1.0f;
		rotation = 0.0f;
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
		int height = (int) (baseline + paint.descent());

		bitmapWidth = Math.max(1, bitmapWidth);
		height = Math.max(1, height);

		visualPlacementBitmap = Bitmap.createBitmap(bitmapWidth, height,
				Bitmap.Config.ARGB_8888);

		int canvasWidth = calculateAlignmentValuesForText(paint, bitmapWidth, textAlignment);
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
		int xCoordinate = Math.round(xCoord / layoutWidthRatio);
		int yCoordinate = Math.round(yCoord / layoutHeightRatio);

		boolean hasChanges = translateX != xCoordinate || translateY != yCoordinate
				|| scaleFactor != 1.0f || rotation != 0.0f;

		if (hasChanges) {
			showSaveChangesDialog(this);
		} else {
			finish();
		}
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
		int xCoordinate = Math.round(xCoord / layoutWidthRatio);
		int yCoordinate = Math.round(yCoord / layoutHeightRatio);

		extras.putInt(X_COORDINATE_BUNDLE_ARGUMENT, xCoordinate);
		extras.putInt(Y_COORDINATE_BUNDLE_ARGUMENT, yCoordinate);

		float finalScale = initialSpriteScale * scaleFactor;
		float finalRotation = initialSpriteRotation + rotation;
		extras.putFloat(SCALE_BUNDLE_ARGUMENT, finalScale);
		extras.putFloat(ROTATION_BUNDLE_ARGUMENT, finalRotation);

		boolean hasChanges = translateX != xCoordinate || translateY != yCoordinate
				|| scaleFactor != 1.0f || rotation != 0.0f;
		extras.putBoolean(CHANGED_COORDINATES, hasChanges);

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

	@Override
	public void setScale(float scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	@Override
	public void setRotation(float rotationDegrees) {
		this.rotation = rotationDegrees;
	}
}
