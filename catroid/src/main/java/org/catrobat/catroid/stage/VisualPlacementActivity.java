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
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.ui.BaseCastActivity;
import org.catrobat.catroid.utils.ToastUtil;

import java.io.File;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;
import static org.catrobat.catroid.common.FlavoredConstants.DEFAULT_ROOT_DIRECTORY;
import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_AUTOMATIC_FILE_NAME;
import static org.catrobat.catroid.stage.StageListener.SCREENSHOT_MANUAL_FILE_NAME;


public class VisualPlacementActivity extends BaseCastActivity implements View.OnTouchListener, DialogInterface.OnClickListener {

	public static final String TAG = VisualPlacementActivity.class.getSimpleName();

	public static final String X_COORDINATE_BUNDLE_ARGUMENT = "xCoordinate";
	public static final String Y_COORDINATE_BUNDLE_ARGUMENT = "yCoordinate";

	private File ProjectDir = new File(DEFAULT_ROOT_DIRECTORY, ProjectManager.getInstance().getCurrentProject().getName());
	private File sceneDir = new File(ProjectDir, ProjectManager.getInstance().getCurrentlyPlayingScene().getName());
	private File automaticScreenshot = new File(sceneDir, SCREENSHOT_AUTOMATIC_FILE_NAME);
	private File manualScreenshot = new File(sceneDir, SCREENSHOT_MANUAL_FILE_NAME);

	private ImageView imageView;
	private boolean coordinatesChanged = false;
	private float xCoord;
	private float yCoord;
	private float scaleX;
	private float scaleY;
	private float rotation;
	private float startX;
	private float startY;
	private float previousY;
	private float previousX;

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
				if (coordinatesChanged) {
					saveCoordinates();
				} else {
					finish();
				}
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
		Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();

		FrameLayout frameLayout = findViewById(R.id.frame_container);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Bitmap.Config.ARGB_8888;
		try {
			String backgroundBitmapPath;
			if (manualScreenshot.exists()) {
				backgroundBitmapPath = manualScreenshot.getPath();
			} else if (automaticScreenshot.exists()) {
				backgroundBitmapPath = automaticScreenshot.getPath();
			} else {
				backgroundBitmapPath = ProjectManager.getInstance().getCurrentlyEditedScene().getBackgroundSprite().getLookList().get(0).getFile().getAbsolutePath();
			}
			Bitmap backgroundBitmap = BitmapFactory.decodeFile(backgroundBitmapPath, options);
			Drawable backgroundDrawable = new BitmapDrawable(getResources(), backgroundBitmap);
			backgroundDrawable.setColorFilter(Color.parseColor("#6F000000"), PorterDuff.Mode.SRC_ATOP);
			frameLayout.setBackground(backgroundDrawable);
		} catch (Exception e) {
			Log.e(TAG, "this sprite has No screenshots or background Look" + e);
			frameLayout.setBackgroundColor(Color.WHITE);
		}

		Bitmap spriteBitmap;
		String objectLookPath;

		if (!currentSprite.look.getImagePath().isEmpty()) {
			objectLookPath = currentSprite.look.getImagePath();
			scaleX = currentSprite.look.getScaleX();
			scaleY = currentSprite.look.getScaleY();
			rotation = currentSprite.look.getRealRotation();
			spriteBitmap = BitmapFactory.decodeFile(objectLookPath, options);
		} else if (currentSprite.getLookList().size() != 0) {
			objectLookPath = currentSprite.getLookList().get(0).getFile().getAbsolutePath();
			spriteBitmap = BitmapFactory.decodeFile(objectLookPath, options);
		} else {
			Log.e(TAG, "this sprite has no Looks");
			spriteBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
		}
		imageView = new ImageView(this);
		imageView.setImageBitmap(spriteBitmap);
		imageView.setScaleType(ImageView.ScaleType.CENTER);

		if (scaleX > 0.01) {
			imageView.setScaleX(scaleX);
		}

		if (scaleY > 0.01) {
			imageView.setScaleY(scaleY);
		}

		if (rotation != 0) {
			imageView.setRotation(rotation - 90);
		}

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
				if (Math.abs(currentX - startX) < 5 && Math.abs(currentY - startY) < 5) {
					imageView.animate()
							.translationX(event.getX() - view.getWidth() / 2)
							.translationY(event.getY() - view.getHeight() / 2)
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
		yCoord = - imageView.getY();
		coordinatesChanged = true;
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
		if (coordinatesChanged) {
			showSaveChangesDialog(this);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
			case BUTTON_POSITIVE:
				saveCoordinates();
				break;
			case BUTTON_NEGATIVE:
				ToastUtil.showError(this, R.string.formula_editor_changes_discarded);
				finish();
				break;
		}
	}

	private void saveCoordinates() {
		Intent returnIntent = new Intent();
		Bundle extras = new Bundle();
		extras.putInt(X_COORDINATE_BUNDLE_ARGUMENT, Math.round(xCoord));
		extras.putInt(Y_COORDINATE_BUNDLE_ARGUMENT, Math.round(yCoord));
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
}
