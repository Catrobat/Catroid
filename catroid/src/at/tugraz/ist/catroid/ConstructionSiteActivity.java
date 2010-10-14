package at.tugraz.ist.catroid;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;

import at.tugraz.ist.catroid.constructionSite.content.BrickDefine;
import at.tugraz.ist.catroid.constructionSite.content.ContentManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ConstructionSiteGalleryAdapter;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ConstructionSiteListViewAdapter;
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.ChangeProgramNameDialog;
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.ContextMenuDialog;
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.LoadProgramDialog;
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.NewProjectDialog;
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.SpritesDialog;
import at.tugraz.ist.catroid.constructionSite.gui.dialogs.ToolBoxDialog;
import at.tugraz.ist.catroid.stage.StageActivity;
import at.tugraz.ist.catroid.utils.ImageContainer;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.utils.filesystem.MediaFileLoader;

public class ConstructionSiteActivity extends Activity implements Observer, OnClickListener, OnItemLongClickListener {

	/** Called when the activity is first created. */

	static final int TOOLBOX_DIALOG_SPRITE = 0;
	static final int TOOLBOX_DIALOG_BACKGROUND = 1;
	static final int SPRITETOOLBOX_DIALOG = 2;
	static final int NEW_PROJECT_DIALOG = 3;
	static final int LOAD_DIALOG = 4;
	static final int CHANGE_PROJECT_NAME_DIALOG = 6;
	static final int CONTEXT_MENU_DIALOG = 7;

	static final String PREF_ROOT = "pref_root";
	static final String PREF_FILE_SPF = "pref_path";

	public static final String DEFAULT_ROOT = "/sdcard/catroid/defaultProject";
	public static final String DEFAULT_PROJECT = "/sdcard/catroid";
	public static final String DEFAULT_FILE = "defaultSaveFile.spf";
	public static final String DEFAULT_FILE_ENDING = ".spf";

	public static final String MEDIA_IGNORE_BY_ANDROID_FILENAME = ".nomedia";

	public static String ROOT_IMAGES;
	public static String ROOT_SOUNDS;
	public static String ROOT;
	public static String SPF_FILE;

	public SharedPreferences mPreferences;
	
	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;

	private Button mToolboxButton;
	private ToolBoxDialog mToolboxObjectDialog;
	private ToolBoxDialog mToolboxStageDialog;
	private Dialog mNewProjectDialog;
	private Dialog mChangeProgramNameDialog;
	private Dialog mLoadDialog;
	private ContextMenuDialog mContextMenuDialog;

	protected ListView mConstructionListView;
	protected Gallery mContructionGallery;
	private ConstructionSiteListViewAdapter mListViewAdapter;
	private ConstructionSiteGalleryAdapter mGalleryAdapter;
	private ContentManager mContentManager;

	private Button mSpritesToolboxButton;
	private SpritesDialog mSpritesToolboxDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.construction_site);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        SCREEN_WIDTH = dm.widthPixels;
        SCREEN_HEIGHT = dm.heightPixels;

		// check for SD card, display message and exit if none available
		if (Utils.checkForSdCard(this)) {
			// requestWindowFeature(Window.FEATURE_NO_TITLE);
			mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			mPreferences = this.getPreferences(Activity.MODE_PRIVATE);

			setRoot(mPreferences.getString(PREF_ROOT, DEFAULT_ROOT), mPreferences.getString(PREF_FILE_SPF, DEFAULT_FILE));

			mContentManager = new ContentManager(this);
			mContentManager.setObserver(this);

			mConstructionListView = (ListView) findViewById(R.id.MainListView);
			mListViewAdapter = new ConstructionSiteListViewAdapter(this, mContentManager.getCurrentSpriteList(), mConstructionListView,
					ImageContainer.getInstance());
			mConstructionListView.setAdapter(mListViewAdapter);
			mConstructionListView.setOnItemLongClickListener(this);

			mContructionGallery = (Gallery) findViewById(R.id.ConstructionSiteGallery);
			mGalleryAdapter = new ConstructionSiteGalleryAdapter(this, mContentManager.getContentGalleryList(), ImageContainer.getInstance());
			mContructionGallery.setAdapter(mGalleryAdapter);

			mToolboxButton = (Button) this.findViewById(R.id.toolbar_button);
			mToolboxButton.setOnClickListener(this);

			mSpritesToolboxButton = (Button) this.findViewById(R.id.sprites_button);
			mSpritesToolboxButton.setOnClickListener(this);

			// Testing
			// mContentManager.testSet();
			// mContentManager.saveContent();
			mContentManager.loadContent(SPF_FILE);
			setTitle(SPF_FILE);
			
			
		}
	}

	private static int LAST_SELECTED_ELEMENT_POSITION = 0;

	public void rememberLastSelectedElementAndView(int position, ImageView pictureView) {
		LAST_SELECTED_ELEMENT_POSITION = position;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == MediaFileLoader.GALLERY_INTENT_CODE) && (data != null)) {

			HashMap<String, String> content = mContentManager.getCurrentSpriteList().get(LAST_SELECTED_ELEMENT_POSITION);
			Uri u2 = Uri.parse(data.getDataString());
			String[] projection = { MediaStore.Images.ImageColumns.DATA, MediaStore.Images.ImageColumns.DISPLAY_NAME };
			Cursor c = managedQuery(u2, projection, null, null, null);
			if (c != null && c.moveToFirst()) {
				File image_full_path = new File(c.getString(0));
				String imageName = ImageContainer.getInstance().saveImageFromPath(image_full_path.getAbsolutePath(), this);
				String imageThumbnailName = ImageContainer.getInstance().saveThumbnailFromPath(image_full_path.getAbsolutePath(), this);
				String oldThumbName = content.get(BrickDefine.BRICK_VALUE_1);
				String oldImageName = content.get(BrickDefine.BRICK_VALUE);

				content.put(BrickDefine.BRICK_VALUE, imageName);
				content.put(BrickDefine.BRICK_VALUE_1, imageThumbnailName);
				content.put(BrickDefine.BRICK_NAME, c.getString(1));

				int indexOf = mContentManager.getContentGalleryList().indexOf(oldThumbName);
				if (mContentManager.getContentGalleryList().remove(oldThumbName))
					mContentManager.getContentGalleryList().add(indexOf, imageThumbnailName);
				else
					mContentManager.getContentGalleryList().add(imageThumbnailName);

				// remove old files
				ImageContainer.getInstance().deleteImage(oldThumbName);
				ImageContainer.getInstance().deleteImage(oldImageName);

				updateViews();
				// debug
				String column0Value = c.getString(0);
				String column1Value = c.getString(1);

				Log.d("Data", column0Value);
				Log.d("Display name", column1Value);
			}

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TOOLBOX_DIALOG_SPRITE:
			mToolboxObjectDialog = new ToolBoxDialog(this, mContentManager, BrickDefine.getToolBoxBrickContent(BrickDefine.SPRITE_CATEGORY));
			return mToolboxObjectDialog;
		case TOOLBOX_DIALOG_BACKGROUND:
			mToolboxStageDialog = new ToolBoxDialog(this, mContentManager, BrickDefine.getToolBoxBrickContent(BrickDefine.STAGE_CATEGORY));
			return mToolboxStageDialog;
		case SPRITETOOLBOX_DIALOG:
			mSpritesToolboxDialog = new SpritesDialog(this, true, null, 0);
			mSpritesToolboxDialog.setContentManager(mContentManager);
			return mSpritesToolboxDialog;
		case NEW_PROJECT_DIALOG:
			mNewProjectDialog = new NewProjectDialog(this, mContentManager);
			return mNewProjectDialog;
		case CHANGE_PROJECT_NAME_DIALOG:
			mChangeProgramNameDialog = new ChangeProgramNameDialog(this, mContentManager);
			return mChangeProgramNameDialog;
		case LOAD_DIALOG:
			mLoadDialog = new LoadProgramDialog(this, mContentManager);
			return mLoadDialog;

		default:
			return null;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.construction_site_menu, menu);
		return true;
	}

	public static final String INTENT_EXTRA_ROOT = "intent_root";
	public static final String INTENT_EXTRA_ROOT_IMAGES = "intent_root_images";
	public static final String INTENT_EXTRA_ROOT_SOUNDS = "intent_root_sounds";
	public static final String INTENT_EXTRA_SPF_FILE_NAME = "intent_spf_file_name";

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.play:
			Intent intent = new Intent(ConstructionSiteActivity.this, StageActivity.class);
			intent.putExtra(INTENT_EXTRA_ROOT, ROOT);
			intent.putExtra(INTENT_EXTRA_ROOT_IMAGES, ROOT_IMAGES);
			intent.putExtra(INTENT_EXTRA_ROOT_SOUNDS, ROOT_SOUNDS);
			intent.putExtra(INTENT_EXTRA_SPF_FILE_NAME, SPF_FILE);

			startActivity(intent);
			return true;

		case R.id.reset:
			Utils.deleteFolder(ROOT_IMAGES, MEDIA_IGNORE_BY_ANDROID_FILENAME);
			Utils.deleteFolder(ROOT_SOUNDS, MEDIA_IGNORE_BY_ANDROID_FILENAME);
			mContentManager.resetContent();
			mContentManager.setEmptyStage();
			updateViews();
			return true;

		case R.id.load:
			mContentManager.saveContent(SPF_FILE);
			showDialog(LOAD_DIALOG);
			return true;

		case R.id.newProject:
			mContentManager.saveContent(SPF_FILE);
			showDialog(NEW_PROJECT_DIALOG);
			return true;

		case R.id.changeProject:
			mContentManager.saveContent(SPF_FILE);
			showDialog(CHANGE_PROJECT_NAME_DIALOG);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void updateViews() {
		mListViewAdapter.notifyDataSetChanged(mContentManager.getCurrentSpriteList());
		mGalleryAdapter.notifyDataSetChanged();

		mSpritesToolboxButton.setText(mContentManager.getCurrentSpriteName());
	}

	public void update(Observable observable, Object data) {
		updateViews();
		if (data != null) {
			try {
				mConstructionListView.setSelectionFromTop(Integer.parseInt(data.toString()), 120);
			} catch (NumberFormatException e) {
				Log.e("ConstructionSiteActivity", "Parsing positions failed");
				e.printStackTrace();
			}
		}

	}

	public void onPause() {
		mContentManager.saveContent(SPF_FILE);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREF_ROOT, ROOT);
		editor.putString(PREF_FILE_SPF, SPF_FILE);
		editor.commit();
		super.onPause();
	}

	public void onClick(View v) {
		if (v.getId() == R.id.toolbar_button) {
			openToolbox();
		} else if (v.getId() == R.id.sprites_button) {
			openSpriteToolbox();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			this.finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private void openToolbox() {
		if (mContentManager.getCurrentSpriteName().equals(this.getString(R.string.stage))) {
			showDialog(TOOLBOX_DIALOG_BACKGROUND);
		} else {
			showDialog(TOOLBOX_DIALOG_SPRITE);
		}
	}

	private void openSpriteToolbox() {
		showDialog(SPRITETOOLBOX_DIALOG);
	}

	/**
	 * Test Method
	 * 
	 * @return one of the Toolbox
	 */
	public Dialog getToolboxDialog() {
		return mToolboxStageDialog;
	}

	public void onBrickClickListener(View v) {
		if (mContentManager.getCurrentSpriteName().equals(this.getString(R.string.stage))) {
			mContentManager.addBrick(mToolboxStageDialog.getBrickClone(v));
			if (mToolboxStageDialog.isShowing())
				mToolboxStageDialog.dismiss();
		} else {
			mContentManager.addBrick(mToolboxObjectDialog.getBrickClone(v));
			if (mToolboxObjectDialog.isShowing())
				mToolboxObjectDialog.dismiss();
		}

	}

	public static void setRoot(String root, String file) {
		File rootFile = new File(root);
		if (!rootFile.exists())
			rootFile.mkdirs();
		ConstructionSiteActivity.ROOT = rootFile.getPath();
		File rootImageFile = new File(Utils.concatPaths(root, "/images"));
		if (!rootImageFile.exists())
			rootImageFile.mkdirs();
		ConstructionSiteActivity.ROOT_IMAGES = rootImageFile.getPath();
		File rootSoundFile = new File(Utils.concatPaths(root, "/sounds"));
		if (!rootSoundFile.exists())
			rootSoundFile.mkdirs();
		ConstructionSiteActivity.ROOT_SOUNDS = rootSoundFile.getPath();

		SPF_FILE = file;
		File spfFile = new File(Utils.concatPaths(ROOT, SPF_FILE));
		if (!spfFile.exists())
			try {
				spfFile.createNewFile();
			} catch (IOException e) {
				Log.e("CONSTRUCTION_SITE_ACTIVITY", e.getMessage());
				e.printStackTrace();
			}

		// set ignore files to the folders
		File noMediaFile = new File(Utils.concatPaths(ROOT_IMAGES, MEDIA_IGNORE_BY_ANDROID_FILENAME));
		try {
			if (!noMediaFile.exists())
				noMediaFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		noMediaFile = new File(Utils.concatPaths(ROOT_SOUNDS, MEDIA_IGNORE_BY_ANDROID_FILENAME));
		try {
			if (!noMediaFile.exists())
				noMediaFile.createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ImageContainer.getInstance().setRootPath(ROOT_IMAGES);
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg0.equals(mConstructionListView)) {
			if (mContextMenuDialog == null)
				mContextMenuDialog = new ContextMenuDialog(this, mContentManager);
			mContextMenuDialog.show(arg1, arg2, mConstructionListView);
		}
		return false;
	}
}