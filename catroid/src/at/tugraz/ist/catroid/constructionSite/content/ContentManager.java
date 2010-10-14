package at.tugraz.ist.catroid.constructionSite.content;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;
import at.tugraz.ist.catroid.ConstructionSiteActivity;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.utils.ImageContainer;
import at.tugraz.ist.catroid.utils.Utils;
import at.tugraz.ist.catroid.utils.filesystem.FileSystem;
import at.tugraz.ist.catroid.utils.parser.Parser;

/**
 * provides content
 * 
 * @author alex, niko, thomas
 * 
 */
public class ContentManager extends Observable {

	private ArrayList<HashMap<String, String>> mCurrentSpriteList;
	private ArrayList<String> mContentGalleryList;
	private ArrayList<String> mAllContentNameList;

	private ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> mAllContentArrayList;

	private FileSystem mFilesystem;
	private Parser mParser;
	private Context mCtx;
	private static final String mTempFile = "defaultSaveFile.spf";
	private int mCurrentSprite;
	private int mIdCounter;

	public ContentManager(Context context) {
		mCtx = context;
		mCurrentSpriteList = null;
		mAllContentArrayList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();

		mFilesystem = new FileSystem();
		mParser = new Parser();

		mIdCounter = 0;
		mContentGalleryList = new ArrayList<String>();

		mAllContentNameList = new ArrayList<String>();

		resetContent();
		setEmptyStage();
	}

	public void loadContent() {
		loadContent(mTempFile);
	}

	public void loadContent(String fileName) {
		resetContent();
		FileInputStream scratch = mFilesystem.createOrOpenFileInput(Utils.concatPaths(ConstructionSiteActivity.ROOT, fileName), mCtx);

		try {
			if (scratch != null && scratch.available() > 0) {
				setAllContentArrayList(mParser.parse(scratch, mCtx));
				mCurrentSpriteList = mAllContentArrayList.get(0).second;
				loadContentGalleryList();
				mIdCounter = getHighestId();
				mCurrentSprite = 0;

				scratch.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mAllContentArrayList.size() == 0) {
			setEmptyStage();
			createDemoSprite();
		}

		loadAllContentNameList();

		setChanged();
		notifyObservers();
	}

	public void saveContent() {
		saveContent(mTempFile);
	}

	public void saveContent(String file) {
		// ((Activity)mCtx).setTitle(title.replace(ConstructionSiteActivity.DEFAULT_FILE_ENDING,
		// "").replace("/", ""));
		// TODO: setTitle-> ClassCastException Testing
		ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spriteBrickList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();
		for (int i = 0; i < mAllContentArrayList.size(); i++) {
			spriteBrickList.add(mAllContentArrayList.get(i));
		}

		FileOutputStream fd = mFilesystem.createOrOpenFileOutput(Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mCtx);
		DataOutputStream ps = new DataOutputStream(fd);

		String xml = mParser.toXml(spriteBrickList, mCtx);
		try {
			ps.write(xml.getBytes());
			ps.close();
			fd.close();
		} catch (IOException e) {
			Log.e("Contentmanager", "ERROR saving file " + e.getMessage());
			e.printStackTrace();
		}

		Log.d("Contentmanager", "Save file!");
	}

	public void resetContent() {
		mCurrentSpriteList = null;
		mAllContentArrayList.clear();
		mAllContentNameList.clear();
		mCurrentSprite = 0;
		mIdCounter = 0;
		mContentGalleryList.clear();
	}

	public void addSprite(Pair<String, ArrayList<HashMap<String, String>>> sprite) {
		mAllContentArrayList.add(sprite);
		mCurrentSprite = mAllContentArrayList.size() - 1;
		mAllContentNameList.add(sprite.first);
		switchSprite(mCurrentSprite);
	}

	public void switchSprite(int position) {
		mCurrentSpriteList = mAllContentArrayList.get(position).second;
		mCurrentSprite = position;
		loadContentGalleryList();
		setChanged();
		notifyObservers();
	}

	public void addBrick(HashMap<String, String> map) {
		map.put(BrickDefine.BRICK_ID, ((Integer) mIdCounter).toString());
		mIdCounter++;
		mCurrentSpriteList.add(map);

		setChanged();
		notifyObservers(mCurrentSpriteList.size() - 1);
	}
	
	private void deleteSound(String soundName) {
		if (soundName == null || soundName.length() == 0) {
			Log.d("ContentManager", "No sound file to delete.");
		} else {
			String soundsPath = ConstructionSiteActivity.ROOT_SOUNDS;
			String soundFilePath = Utils.concatPaths(soundsPath, soundName);
			if(Utils.deleteFile(soundFilePath)) {
				Log.d("ContentManager", "Successfully deleted sound file \"" + soundFilePath + "\".");
			} else {
				Log.w("ContentManager", "Error! Could not delete sound file \"" + soundFilePath + "\".");
			}
		}
	}

	public void removeBrick(int position) {
		int type = Integer.parseInt(mCurrentSpriteList.get(position).get(BrickDefine.BRICK_TYPE));
		if (type == BrickDefine.SET_BACKGROUND || type == BrickDefine.SET_COSTUME) {
			mContentGalleryList.remove(mCurrentSpriteList.get(position).get(BrickDefine.BRICK_VALUE_1));
		} else if (type == BrickDefine.PLAY_SOUND) {
			Log.d("ContentManager", "Deleting \"Play sound\" brick.");
			String soundName = mCurrentSpriteList.get(position).get(BrickDefine.BRICK_VALUE);
			mCurrentSpriteList.remove(position);
			setChanged();
			notifyObservers();
			deleteSound(soundName);
			return;
		}
		mCurrentSpriteList.remove(position);
		setChanged();
		notifyObservers();
	}

	public boolean moveBrickUpInList(int position) {
		if (position > 0 && position < mCurrentSpriteList.size()) {
			HashMap<String, String> map = mCurrentSpriteList.get(position);
			mCurrentSpriteList.remove(position);
			mCurrentSpriteList.add(position - 1, map);

			loadContentGalleryList();
			setChanged();
			notifyObservers(position - 1);
			return true;
		}
		return false;
	}

	public boolean moveBrickDownInList(int position) {
		if (position < mCurrentSpriteList.size() - 1 && position >= 0) {
			HashMap<String, String> map = mCurrentSpriteList.get(position);
			mCurrentSpriteList.remove(position);
			mCurrentSpriteList.add(position + 1, map);

			loadContentGalleryList();
			setChanged();
			notifyObservers(position + 1);
			return true;
		}
		return false;
	}

	public ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> getAllContentArrayList() {
		return mAllContentArrayList;
	}

	private void setAllContentArrayList(ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> list) {
		mAllContentArrayList = list;
	}

	public void loadAllContentNameList() {
		mAllContentNameList.clear();
		for (int i = 0; i < mAllContentArrayList.size(); i++)
			mAllContentNameList.add(mAllContentArrayList.get(i).first);
	}

	public ArrayList<String> getAllContentNameList() {
		return mAllContentNameList;
	}

	public ArrayList<HashMap<String, String>> getCurrentSpriteList() {
		return mCurrentSpriteList;
	}

	public String getCurrentSpriteName() {
		return mAllContentArrayList.get(mCurrentSprite).first;
	}

	public Integer getCurrentSpritePosition() {
		return mCurrentSprite;
	}

	public ArrayList<String> getContentGalleryList() {
		return mContentGalleryList;
	}

	private void loadContentGalleryList() {
		mContentGalleryList.clear();
		for (int i = 0; i < mCurrentSpriteList.size(); i++) {
			String type = mCurrentSpriteList.get(i).get(BrickDefine.BRICK_TYPE);

			if (type.equals(String.valueOf(BrickDefine.SET_BACKGROUND)) || type.equals(String.valueOf(BrickDefine.SET_COSTUME))) {

				mContentGalleryList.add(mCurrentSpriteList.get(i).get(BrickDefine.BRICK_VALUE_1));
			}
		}
	}

	public void setEmptyStage() {
		// initialize stage
		mCurrentSpriteList = new ArrayList<HashMap<String, String>>();
		mCurrentSprite = 0;
		mAllContentArrayList.add(new Pair<String, ArrayList<HashMap<String, String>>>(mCtx.getString(R.string.stage), mCurrentSpriteList));
		this.loadAllContentNameList(); // if we do not call this here, we will
										// have problems at the sprite dialog!

	}

	public void initializeNewProject() {
		resetContent();
		setEmptyStage();
		loadAllContentNameList();
		setChanged();
		notifyObservers();
	}

	public void createDemoSprite() {
		// create a new sprite with 3 costumes
		Pair<String, ArrayList<HashMap<String, String>>> sprite = new Pair<String, ArrayList<HashMap<String, String>>>(mCtx.getResources()
				.getText(R.string.default_sprite).toString(), new ArrayList<HashMap<String, String>>());
		this.addSprite(sprite);

		ImageContainer imageContainer = ImageContainer.getInstance();
		Bitmap costume1 = ((BitmapDrawable) mCtx.getResources().getDrawable(R.drawable.catroid)).getBitmap();
		String image1Path = imageContainer.saveImageFromBitmap(costume1, "catroid.png", false, mCtx);
		String thumb1Path = imageContainer.saveThumbnailFromBitmap(costume1, "catroid_thumb.png", false, mCtx);
		Bitmap costume2 = ((BitmapDrawable) mCtx.getResources().getDrawable(R.drawable.catroid_banzai)).getBitmap();
		String image2Path = imageContainer.saveImageFromBitmap(costume2, "catroid_banzai.png", false, mCtx);
		String thumb2Path = imageContainer.saveThumbnailFromBitmap(costume2, "catroid_banzai_thumb.png", false, mCtx);
		Bitmap costume3 = ((BitmapDrawable) mCtx.getResources().getDrawable(R.drawable.catroid_cheshire)).getBitmap();
		String image3Path = imageContainer.saveImageFromBitmap(costume3, "catroid_cheshire.png", false, mCtx);
		String thumb3Path = imageContainer.saveThumbnailFromBitmap(costume3, "catroid_cheshire_thumb.png", false, mCtx);

		HashMap<String, String> map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.GO_TO));
		map.put(BrickDefine.BRICK_VALUE, "100");
		map.put(BrickDefine.BRICK_VALUE_1, "100");
		this.addBrick(map);

		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
		map.put(BrickDefine.BRICK_VALUE, image1Path);
		map.put(BrickDefine.BRICK_VALUE_1, thumb1Path);
		this.addBrick(map);

		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
		map.put(BrickDefine.BRICK_VALUE, "2");
		this.addBrick(map);

		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
		map.put(BrickDefine.BRICK_VALUE, image2Path);
		map.put(BrickDefine.BRICK_VALUE_1, thumb2Path);
		this.addBrick(map);

		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
		map.put(BrickDefine.BRICK_VALUE, "2");
		this.addBrick(map);

		map = new HashMap<String, String>();
		map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
		map.put(BrickDefine.BRICK_VALUE, image3Path);
		map.put(BrickDefine.BRICK_VALUE_1, thumb3Path);
		this.addBrick(map);

		loadContentGalleryList();
	}

	public void setObserver(Observer observer) {
		addObserver(observer);
	}

	private int getHighestId() {
		ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spriteList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>(
				mAllContentArrayList);

		int highestId = 0;
		for (int i = 0; i < mAllContentArrayList.size(); i++) {
			ArrayList<HashMap<String, String>> sprite = spriteList.get(i).second;
			for (int j = 0; j < sprite.size(); j++) {
				HashMap<String, String> brickList = sprite.get(j);
				brickList.get(BrickDefine.BRICK_ID);
				if (brickList.size() > 0 && !(brickList.get(BrickDefine.BRICK_ID).equals(""))) {
					int tempId = Integer.valueOf(brickList.get(BrickDefine.BRICK_ID).toString()).intValue();
					boolean test = (highestId < tempId);
					if (test) {
						highestId = tempId;
					}
				}
			}
		}
		return (highestId + 1); // ID immer aktuellste freie
	}

	/**
	 * test method
	 */
	public int getIdCounter() {
		return mIdCounter;
	}
}