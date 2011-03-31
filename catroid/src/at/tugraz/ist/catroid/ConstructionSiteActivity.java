/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010  Catroid development team 
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.catroid;

import java.io.File;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.constructionSite.gui.adapter.ProgrammAdapter;
import at.tugraz.ist.catroid.content.brick.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.brick.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.brick.HideBrick;
import at.tugraz.ist.catroid.content.brick.IfTouchedBrick;
import at.tugraz.ist.catroid.content.brick.PlaceAtBrick;
import at.tugraz.ist.catroid.content.brick.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.brick.ShowBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ConstructionSiteActivity extends Activity implements Observer, OnItemLongClickListener {

	/** Called when the activity is first created. */

	static final int TOOLBOX_DIALOG_SPRITE = 0;
	static final int TOOLBOX_DIALOG_BACKGROUND = 1;
	static final int SPRITETOOLBOX_DIALOG = 2;
	static final int NEW_PROJECT_DIALOG = 3;
	static final int LOAD_DIALOG = 4;
	static final int CHANGE_PROJECT_NAME_DIALOG = 6;
	static final int CONTEXT_MENU_DIALOG = 7;

	private final String PREF_ROOT = "pref_root";
	private final String PREF_FILE_SPF = "pref_path";

	private static final String DEFAULT_ROOT = "/sdcard/catroid";
//	private static final String TMP_PATH = DEFAULT_ROOT + "/tmp";
//	private static final String DEFAULT_PROJECT = "/sdcard/catroid/defaultProject";
	private static final String DEFAULT_FILE = "defaultSaveFile.spf";
//	private static final String DEFAULT_FILE_ENDING = ".spf";

	private static final String MEDIA_IGNORE_BY_ANDROID_FILENAME = ".nomedia";

	private static String ROOT_IMAGES;
	private static String ROOT_SOUNDS;
	private static String ROOT;
	private static String SPF_FILE;

	public SharedPreferences mPreferences;

//	private static int SCREEN_WIDTH;
//	private static int SCREEN_HEIGHT;

    //	private ToolBoxDialog mToolboxObjectDialog;
//	private ToolBoxDialog mToolboxStageDialog;
    //	private Dialog mNewProjectDialog;
    //	private Dialog mChangeProgramNameDialog;
    //	private Dialog mLoadDialog;
    //private ContextMenuDialog mContextMenuDialog;

	protected ListView mConstructionListView;
	protected Gallery mContructionGallery;
	private ProgrammAdapter programmAdapter;
	//private ConstructionSiteGalleryAdapter mGalleryAdapter;
	private ProjectManager contentManager;

	private Project currentProject;

    //    private Script currentScript;


    //private SpritesDialog mSpritesToolboxDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

//		SCREEN_WIDTH = dm.widthPixels;
//		SCREEN_HEIGHT = dm.heightPixels;

		// check for SD card, display message and exit if none available
		try {
			StorageHandler.getInstance().loadSoundContent(this);
			mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
			mPreferences = getPreferences(Activity.MODE_PRIVATE);

			String rootPath = mPreferences.getString(PREF_ROOT, DEFAULT_ROOT);
			String spfFile = mPreferences.getString(PREF_FILE_SPF, DEFAULT_FILE);

			File rootFile = new File(rootPath);
			if (!rootFile.exists()) {
				rootPath = Utils.concatPaths(DEFAULT_ROOT, DEFAULT_FILE.replace(".spf", ""));
				spfFile = DEFAULT_FILE;
			}

			if (getIntent().hasExtra(INTENT_EXTRA_ROOT) && getIntent().hasExtra(INTENT_EXTRA_SPF_FILE_NAME)) {
				rootPath = getIntent().getStringExtra(INTENT_EXTRA_ROOT);
				spfFile = getIntent().getStringExtra(INTENT_EXTRA_SPF_FILE_NAME);
			}
			setRoot(rootPath, spfFile);

			initViews();

            contentManager = null;
			contentManager.setObserver(this);

			// Testing
			// mContentManager.testSet();
			// mContentManager.saveContent();
			// mContentManager.loadContent(SPF_FILE);
			//TODO: set real current project
			currentProject = new Project(this, "new");


			Sprite stageSprite = currentProject.getSpriteList().get(0);
			Script script = new Script();

			script.addBrick(new IfTouchedBrick(stageSprite, script));
			script.addBrick(new ComeToFrontBrick(stageSprite, currentProject));
			script.addBrick(new GoNStepsBackBrick(stageSprite, 5));
			script.addBrick(new HideBrick(stageSprite));

			//script.addBrick(new PlaySoundBrick("sound.mp3"));
			script.addBrick(new ScaleCostumeBrick(stageSprite, 80));
			script.addBrick(new ShowBrick(stageSprite));
			//script.addBrick(new WaitBrick(1000));
			script.addBrick(new PlaceAtBrick(stageSprite, 105, 206));

			stageSprite.getScriptList().add(script);
            //			currentScript = script;

			Log.d("testProject", "sprite count: " + currentProject.getSpriteList().size());
			Log.d("testProject", "script count: " + currentProject.getSpriteList().get(0).getScriptList().size());
			programmAdapter.setContent(currentProject.getSpriteList().get(0).getScriptList().get(0));

			setTitle(currentProject.getName());

		} catch (IOException e) {
			
		} 
	}

	private void initViews() {
        //		mConstructionListView = (ListView) findViewById(R.id.MainListView);
        //		programmAdapter = new ProgrammAdapter(this, new Script(), mConstructionListView, ImageContainer.getInstance());
        //		mConstructionListView.setAdapter(programmAdapter);
        //		mConstructionListView.setOnItemLongClickListener(this);
        //
        //		mContructionGallery = (Gallery) findViewById(R.id.ConstructionSiteGallery);
        ////		mGalleryAdapter = new ConstructionSiteGalleryAdapter(this, null, ImageContainer.getInstance());
        ////		mContructionGallery.setAdapter(mGalleryAdapter);
        //
        //		mToolboxButton = (Button) findViewById(R.id.toolbar_button);
        //		mToolboxButton.setOnClickListener(this);
        //
        //		mSpritesToolboxButton = (Button) findViewById(R.id.sprites_button);
        //		mSpritesToolboxButton.setOnClickListener(this);

	}

	public void setProject(Project project) {
		currentProject = project;
		programmAdapter.setContent(currentProject.getSpriteList().get(0).getScriptList().get(0));
	}

	// private static int LAST_SELECTED_ELEMENT_POSITION = 0;

	public void rememberLastSelectedElementAndView(int position, ImageView pictureView) {
		// LAST_SELECTED_ELEMENT_POSITION = position;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
         * if ((requestCode == MediaFileLoader.GALLERY_INTENT_CODE) && (data !=
         * null)) {
         * 
         * HashMap<String, String> content =
         * contentManager.getCurrentSpriteCommandList
         * ().get(LAST_SELECTED_ELEMENT_POSITION); Sprite/Script an the //
         * BrickPosition of the // Image Brick Uri u2 =
         * Uri.parse(data.getDataString()); String[] projection = {
         * MediaStore.Images.ImageColumns.DATA,
         * MediaStore.Images.ImageColumns.DISPLAY_NAME }; Cursor c =
         * managedQuery(u2, projection, null, null, null); if (c != null &&
         * c.moveToFirst()) { File image_full_path = new File(c.getString(0));
         * String imageName =
         * ImageContainer.getInstance().saveImageFromPath(image_full_path
         * .getAbsolutePath(), this); String imageThumbnailName =
         * ImageContainer.
         * getInstance().saveThumbnailFromPath(image_full_path.getAbsolutePath
         * (), this); String oldThumbName =
         * content.get(BrickDefine.BRICK_VALUE_1); String oldImageName =
         * content.get(BrickDefine.BRICK_VALUE);
         * 
         * content.put(BrickDefine.BRICK_VALUE, imageName);
         * content.put(BrickDefine.BRICK_VALUE_1, imageThumbnailName);
         * content.put(BrickDefine.BRICK_NAME, c.getString(1));
         * 
         * int indexOf =
         * contentManager.getCurrentSpriteCostumeNameList().indexOf
         * (oldThumbName); if
         * (contentManager.getCurrentSpriteCostumeNameList().remove
         * (oldThumbName))
         * contentManager.getCurrentSpriteCostumeNameList().add(indexOf,
         * imageThumbnailName); else
         * contentManager.getCurrentSpriteCostumeNameList
         * ().add(imageThumbnailName);
         * 
         * // remove old files
         * ImageContainer.getInstance().deleteImage(oldThumbName);
         * ImageContainer.getInstance().deleteImage(oldImageName);
         * 
         * updateViews(); // debug String column0Value = c.getString(0); String
         * column1Value = c.getString(1);
         * 
         * Log.d("Data", column0Value); Log.d("Display name", column1Value); }
         * 
         * }
         */

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
    protected Dialog onCreateDialog(int id) {
        //		switch (id) {
        //		case TOOLBOX_DIALOG_SPRITE:
        //			mToolboxObjectDialog = new ToolBoxDialog(this, contentManager);
        //			return mToolboxObjectDialog;
        //		case TOOLBOX_DIALOG_BACKGROUND:
        //			mToolboxStageDialog = new ToolBoxDialog(this, contentManager);
        //			return mToolboxStageDialog;
        //		case SPRITETOOLBOX_DIALOG:
        //			mSpritesToolboxDialog = new SpritesDialog(this, true, null, 0);
        //			mSpritesToolboxDialog.setContentManager(contentManager);
        //			return mSpritesToolboxDialog;
        //		case NEW_PROJECT_DIALOG:
        //			mNewProjectDialog = new NewProjectDialog(this, contentManager);
        //			return mNewProjectDialog;
        //		case CHANGE_PROJECT_NAME_DIALOG:
        //			mChangeProgramNameDialog = new RenameProjectDialog(this, contentManager);
        //			return mChangeProgramNameDialog;
        //		case LOAD_DIALOG:
        //			mLoadDialog = new LoadProgramDialog(this, contentManager);
        //			return mLoadDialog;
        //
        //		default:
        //			return null;
        //		}

	    return null;
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
        //		switch (item.getItemId()) {
        //		case R.id.play:
        //			Intent intent = new Intent(ConstructionSiteActivity.this, StageActivity.class);
        //			intent.putExtra(INTENT_EXTRA_ROOT, ROOT);
        //			intent.putExtra(INTENT_EXTRA_ROOT_IMAGES, ROOT_IMAGES);
        //			intent.putExtra(INTENT_EXTRA_ROOT_SOUNDS, ROOT_SOUNDS);
        //			intent.putExtra(INTENT_EXTRA_SPF_FILE_NAME, SPF_FILE);
        //
        //			startActivity(intent);
        //			return true;
        //
        //		case R.id.reset:
        //			Utils.deleteFolder(ROOT_IMAGES, MEDIA_IGNORE_BY_ANDROID_FILENAME);
        //			Utils.deleteFolder(ROOT_SOUNDS, MEDIA_IGNORE_BY_ANDROID_FILENAME);
        //			try {
        //				contentManager.resetProject();
        //			} catch (NameNotFoundException e) {
        //				e.printStackTrace();
        //			}
        //			updateViews();
        //			return true;
        //
        //		case R.id.load:
        //			contentManager.saveProject();
        //			showDialog(LOAD_DIALOG);
        //			return true;
        //
        //		case R.id.newProject:
        //			contentManager.saveProject();
        //			showDialog(NEW_PROJECT_DIALOG);
        //			return true;
        //
        //		case R.id.changeProject:
        //			contentManager.saveProject();
        //			showDialog(CHANGE_PROJECT_NAME_DIALOG);
        //			return true;
        //		case R.id.uploadProject:
        //			contentManager.saveProject();
        //			String projectName = SPF_FILE.substring(0, SPF_FILE.length() - DEFAULT_FILE_ENDING.length());
        //			new ProjectUploadTask(this, projectName, ROOT, TMP_PATH + "/tmp.zip").execute();
        //			return true;
        //
        //		case R.id.aboutCatroid:
        //			Utils.displayWebsite(this, Uri.parse(getString(R.string.about_catroid_url)));
        //			return true;
        //
        //		default:
        //			return super.onOptionsItemSelected(item);
        //		}
        return true;
	}

	public void updateViews() {
        //		programmAdapter.notifyDataSetChanged(contentManager.getCurrentSprite());
        ////		mGalleryAdapter.notifyDataSetChanged();
        //
        //		mSpritesToolboxButton.setText(contentManager.getCurrentSprite().getName());
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

	@Override
    public void onPause() {
		// mContentManager.saveContent(SPF_FILE);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(PREF_ROOT, ROOT);
		editor.putString(PREF_FILE_SPF, SPF_FILE);
		editor.commit();
		super.onPause();
	}

//	public void onClick(View v) {
//		if (v.getId() == R.id.toolbar_button) {
//			openToolbox();
//		} else if (v.getId() == R.id.sprites_button) {
//			openSpriteToolbox();
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

//	private void openToolbox() {
//		if (contentManager.getCurrentSprite().getName().equals(this.getString(R.string.stage))) {
//			showDialog(TOOLBOX_DIALOG_BACKGROUND);
//		} else {
//			showDialog(TOOLBOX_DIALOG_SPRITE);
//		}
//	}
//
//	private void openSpriteToolbox() {
//		showDialog(SPRITETOOLBOX_DIALOG);
//	}

	/**
	 * Test Method
	 * 
	 * @return one of the Toolbox
	 */
//	public Dialog getToolboxDialog() {
//		return mToolboxStageDialog;
//	}

	public void onBrickClickListener(View v) {
        //		if (contentManager.getCurrentSprite().getName().equals(this.getString(R.string.stage))) {
        //			contentManager.addBrick(mToolboxStageDialog.getBrickClone(v), currentScript);
        //			if (mToolboxStageDialog.isShowing()) {
        //                mToolboxStageDialog.dismiss();
        //            }
        //		} else {
        //			contentManager.addBrick(mToolboxObjectDialog.getBrickClone(v), currentScript);
        //			if (mToolboxObjectDialog.isShowing()) {
        //                mToolboxObjectDialog.dismiss();
        //            }
        //		}

	}

	public static void setRoot(String root, String file) {
		File rootFile = new File(root);
		if (!rootFile.exists()) {
			rootFile.mkdirs();
		}
		ROOT = rootFile.getPath();
		File rootImageFile = new File(Utils.concatPaths(root, "/images"));
		if (!rootImageFile.exists()) {
            rootImageFile.mkdirs();
        }
		ConstructionSiteActivity.ROOT_IMAGES = rootImageFile.getPath();
		File rootSoundFile = new File(Utils.concatPaths(root, "/sounds"));
		if (!rootSoundFile.exists()) {
            rootSoundFile.mkdirs();
        }
		ConstructionSiteActivity.ROOT_SOUNDS = rootSoundFile.getPath();

		SPF_FILE = file;
		File spfFile = new File(Utils.concatPaths(root, file));
		if (!spfFile.exists()) {
            try {
				spfFile.createNewFile();
			} catch (IOException e) {
				Log.e("CONSTRUCTION_SITE_ACTIVITY", e.getMessage());
				e.printStackTrace();
			}
        }

		// set ignore files to the folders
		File noMediaFile = new File(Utils.concatPaths(ROOT_IMAGES, MEDIA_IGNORE_BY_ANDROID_FILENAME));
		try {
			if (!noMediaFile.exists()) {
                noMediaFile.createNewFile();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
		noMediaFile = new File(Utils.concatPaths(ROOT_SOUNDS, MEDIA_IGNORE_BY_ANDROID_FILENAME));
		try {
			if (!noMediaFile.exists()) {
                noMediaFile.createNewFile();
            }
		} catch (IOException e) {
			e.printStackTrace();
		}

		//ImageContainer.getInstance().setRootPath(ROOT_IMAGES);
	}

	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        //		if (arg0.equals(mConstructionListView)) {
        //			if (mContextMenuDialog == null) {
        //				if (currentProject.getSpriteList().get(0) == null) {
        //					currentProject.getSpriteList().add(new Sprite(getString(R.string.stage)));
        //					Script currentScript = currentProject.getSpriteList().get(0).getScriptList().get(0);
        //					if (currentScript == null) {
        //						currentProject.getSpriteList().get(0).getScriptList().add(new Script());
        //						currentScript = currentProject.getSpriteList().get(0).getScriptList().get(0);
        //					}
        //					mContextMenuDialog = new ContextMenuDialog(this, contentManager, currentScript);
        //				}
        //				mContextMenuDialog.show(arg1, arg2, mConstructionListView);
        //			}
        //		}
		return false;
	}

	public ProgrammAdapter getProgrammAdapter() {
		return programmAdapter;
	}

}