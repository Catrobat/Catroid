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

	private ArrayList<HashMap<String, String>> mCurrentSpriteCommandList;
	private ArrayList<String> mCurrentSpriteCostumeNameList;
	private ArrayList<String> mAllSpriteNameList;

	private ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> mAllSpriteCommandList;

	private FileSystem mFileSystem;
	private Parser mParser;
	private Context mContext;
	private static final String mTempFile = "defaultSaveFile.spf";
	private int mCurrentSpritePosition;
	private int mBrickIdCounter;

	public ContentManager(Context context) {
		mContext = context;
		mCurrentSpriteCommandList = null;
		mAllSpriteCommandList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();

		mFileSystem = new FileSystem();
		mParser = new Parser();

		mBrickIdCounter = 0;
		mCurrentSpriteCostumeNameList = new ArrayList<String>();

		mAllSpriteNameList = new ArrayList<String>();

		resetContent();
		setEmptyStage();
	}

	public void loadContent() {
		loadContent(mTempFile);
	}

	public void loadContent(String fileName) {
		resetContent();
		FileInputStream projectXMLFileInputStream = mFileSystem.createOrOpenFileInput(Utils.concatPaths(ConstructionSiteActivity.ROOT, fileName), mContext);

		try {
			if (projectXMLFileInputStream != null && projectXMLFileInputStream.available() > 0) {
				setAllSpriteCommandList(mParser.parse(projectXMLFileInputStream, mContext));
				mCurrentSpriteCommandList = mAllSpriteCommandList.get(0).second;
				loadCurrentSpriteCostumeNameList();
				mBrickIdCounter = getHighestBrickId();
				mCurrentSpritePosition = 0;

				projectXMLFileInputStream.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		if (mAllSpriteCommandList.size() == 0) {
			setEmptyStage();
			createDemoSprite();
		}

		loadAllSpriteNameList();

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
		for (int i = 0; i < mAllSpriteCommandList.size(); i++) {
			spriteBrickList.add(mAllSpriteCommandList.get(i));
		}

		FileOutputStream fd = mFileSystem.createOrOpenFileOutput(Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mContext);
		DataOutputStream ps = new DataOutputStream(fd);

		String xml = mParser.toXml(spriteBrickList, mContext);
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
		mCurrentSpriteCommandList = null;
		mAllSpriteCommandList.clear();
		mAllSpriteNameList.clear();
		mCurrentSpritePosition = 0;
		mBrickIdCounter = 0;
		mCurrentSpriteCostumeNameList.clear();
	}

	public void addSprite(Pair<String, ArrayList<HashMap<String, String>>> sprite) {
		mAllSpriteCommandList.add(sprite);
		mCurrentSpritePosition = mAllSpriteCommandList.size() - 1;
		mAllSpriteNameList.add(sprite.first);
		switchSprite(mCurrentSpritePosition);
	}

	public void switchSprite(int position) {
		mCurrentSpriteCommandList = mAllSpriteCommandList.get(position).second;
		mCurrentSpritePosition = position;
		loadCurrentSpriteCostumeNameList();
		setChanged();
		notifyObservers();
	}

	public void addBrick(HashMap<String, String> map) {
		map.put(BrickDefine.BRICK_ID, ((Integer) mBrickIdCounter).toString());
		mBrickIdCounter++;
		mCurrentSpriteCommandList.add(map);

		setChanged();
		notifyObservers(mCurrentSpriteCommandList.size() - 1);
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
		int type = Integer.parseInt(mCurrentSpriteCommandList.get(position).get(BrickDefine.BRICK_TYPE));
		if (type == BrickDefine.SET_BACKGROUND || type == BrickDefine.SET_COSTUME) {
			mCurrentSpriteCostumeNameList.remove(mCurrentSpriteCommandList.get(position).get(BrickDefine.BRICK_VALUE_1));
		} else if (type == BrickDefine.PLAY_SOUND) {
			Log.d("ContentManager", "Deleting \"Play sound\" brick.");
			String soundName = mCurrentSpriteCommandList.get(position).get(BrickDefine.BRICK_VALUE);
			mCurrentSpriteCommandList.remove(position);
			setChanged();
			notifyObservers();
			deleteSound(soundName);
			return;
		}
		mCurrentSpriteCommandList.remove(position);
		setChanged();
		notifyObservers();
	}

	public boolean moveBrickUpInList(int position) {
		if (position > 0 && position < mCurrentSpriteCommandList.size()) {
			HashMap<String, String> map = mCurrentSpriteCommandList.get(position);
			mCurrentSpriteCommandList.remove(position);
			mCurrentSpriteCommandList.add(position - 1, map);

			loadCurrentSpriteCostumeNameList();
			setChanged();
			notifyObservers(position - 1);
			return true;
		}
		return false;
	}

	public boolean moveBrickDownInList(int position) {
		if (position < mCurrentSpriteCommandList.size() - 1 && position >= 0) {
			HashMap<String, String> map = mCurrentSpriteCommandList.get(position);
			mCurrentSpriteCommandList.remove(position);
			mCurrentSpriteCommandList.add(position + 1, map);

			loadCurrentSpriteCostumeNameList();
			setChanged();
			notifyObservers(position + 1);
			return true;
		}
		return false;
	}

	public ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> getAllSpriteCommandList() {
		return mAllSpriteCommandList;
	}

	private void setAllSpriteCommandList(ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> list) {
		mAllSpriteCommandList = list;
	}

	public void loadAllSpriteNameList() {
		mAllSpriteNameList.clear();
		for (int i = 0; i < mAllSpriteCommandList.size(); i++)
			mAllSpriteNameList.add(mAllSpriteCommandList.get(i).first);
	}

	public ArrayList<String> getAllSpriteNameList() {
		return mAllSpriteNameList;
	}

	public ArrayList<HashMap<String, String>> getCurrentSpriteCommandList() {
		return mCurrentSpriteCommandList;
	}

	public String getCurrentSpriteName() {
		return mAllSpriteCommandList.get(mCurrentSpritePosition).first;
	}

	public Integer getCurrentSpritePosition() {
		return mCurrentSpritePosition;
	}

	public ArrayList<String> getCurrentSpriteCostumeNameList() {
		return mCurrentSpriteCostumeNameList;
	}

	private void loadCurrentSpriteCostumeNameList() {
		mCurrentSpriteCostumeNameList.clear();
		for (int i = 0; i < mCurrentSpriteCommandList.size(); i++) {
			String type = mCurrentSpriteCommandList.get(i).get(BrickDefine.BRICK_TYPE);

			if (type.equals(String.valueOf(BrickDefine.SET_BACKGROUND)) || type.equals(String.valueOf(BrickDefine.SET_COSTUME))) {

				mCurrentSpriteCostumeNameList.add(mCurrentSpriteCommandList.get(i).get(BrickDefine.BRICK_VALUE_1));
			}
		}
	}

	public void setEmptyStage() {
		// initialize stage
		mCurrentSpriteCommandList = new ArrayList<HashMap<String, String>>();
		mCurrentSpritePosition = 0;
		mAllSpriteCommandList.add(new Pair<String, ArrayList<HashMap<String, String>>>(mContext.getString(R.string.stage), mCurrentSpriteCommandList));
		this.loadAllSpriteNameList(); // if we do not call this here, we will
										// have problems at the sprite dialog!

	}

	public void initializeNewProject() {
		resetContent();
		setEmptyStage();
		loadAllSpriteNameList();
		setChanged();
		notifyObservers();
	}

	public void createDemoSprite() {
		// create a new sprite with 3 costumes
		Pair<String, ArrayList<HashMap<String, String>>> sprite = new Pair<String, ArrayList<HashMap<String, String>>>(mContext.getResources()
				.getText(R.string.default_sprite).toString(), new ArrayList<HashMap<String, String>>());
		this.addSprite(sprite);

		ImageContainer imageContainer = ImageContainer.getInstance();
		Bitmap costume1 = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.catroid)).getBitmap();
		String image1Path = imageContainer.saveImageFromBitmap(costume1, "catroid.png", false, mContext);
		String thumb1Path = imageContainer.saveThumbnailFromBitmap(costume1, "catroid_thumb.png", false, mContext);
		Bitmap costume2 = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.catroid_banzai)).getBitmap();
		String image2Path = imageContainer.saveImageFromBitmap(costume2, "catroid_banzai.png", false, mContext);
		String thumb2Path = imageContainer.saveThumbnailFromBitmap(costume2, "catroid_banzai_thumb.png", false, mContext);
		Bitmap costume3 = ((BitmapDrawable) mContext.getResources().getDrawable(R.drawable.catroid_cheshire)).getBitmap();
		String image3Path = imageContainer.saveImageFromBitmap(costume3, "catroid_cheshire.png", false, mContext);
		String thumb3Path = imageContainer.saveThumbnailFromBitmap(costume3, "catroid_cheshire_thumb.png", false, mContext);

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

		loadCurrentSpriteCostumeNameList();
	}

	public void setObserver(Observer observer) {
		addObserver(observer);
	}

	private int getHighestBrickId() {
		ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spriteList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>(
				mAllSpriteCommandList);

		int highestId = 0;
		for (int i = 0; i < mAllSpriteCommandList.size(); i++) {
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
	public int getBrickIdCounter() {
		return mBrickIdCounter;
	}
}