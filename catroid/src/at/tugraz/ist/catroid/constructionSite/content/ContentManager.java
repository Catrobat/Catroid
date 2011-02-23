package at.tugraz.ist.catroid.constructionSite.content;

import java.io.IOException; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Pair;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.gui.Brick;
import at.tugraz.ist.catroid.content.brick.gui.PlaySoundBrick;
import at.tugraz.ist.catroid.content.brick.gui.SetCostumeBrick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ContentManager extends Observable {

    private Sprite currentSprite; // TODO: or ID?
    private Context context;
    private Project project;
    private static final String tempFile = "defaultSaveFile";

    private ArrayList<HashMap<String, String>> mCurrentSpriteCommandList;
    private ArrayList<String> mCurrentSpriteCostumeNameList;
    private ArrayList<String> mAllSpriteNameList;

    private ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> mAllSpriteCommandList;

    // obsolete:
//    private FileSystem mFileSystem;
//    private Parser mParser;
    private Context mContext;
    private int mCurrentSpritePosition;
    private int mBrickIdCounter;

    public ContentManager(Context context) { // TODO set project here?
        this.context = context;

        // obsolete:
        mContext = context;
        mCurrentSpriteCommandList = null;
        mAllSpriteCommandList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>();

//        mFileSystem = new FileSystem();
//        mParser = new Parser();

        mBrickIdCounter = 0;
        mCurrentSpriteCostumeNameList = new ArrayList<String>();

        mAllSpriteNameList = new ArrayList<String>();

        resetContent();
        // setEmptyStage();
    }

    public void loadContent() {
        loadContent(tempFile);
    }

    public void loadContent(String projectName) {
        try {
            project = StorageHandler.getInstance().loadProject(projectName);
            currentSprite = project.getSpriteList().get(0); // stage
            setChanged();
            notifyObservers();
        } catch (IOException e) {
            // TODO show error dialog
        }

        // resetContent();
        // FileInputStream projectXMLFileInputStream =
        // mFileSystem.createOrOpenFileInput(
        // Utils.concatPaths(ConstructionSiteActivity.ROOT, fileName),
        // mContext);
        //
        // try {
        // if (projectXMLFileInputStream != null &&
        // projectXMLFileInputStream.available() > 0) {
        // setAllSpriteCommandList(mParser.parse(projectXMLFileInputStream,
        // mContext));
        // mCurrentSpriteCommandList = mAllSpriteCommandList.get(0).second;
        // loadCurrentSpriteCostumeNameList();
        // mBrickIdCounter = getHighestBrickId();
        // mCurrentSpritePosition = 0;
        //
        // projectXMLFileInputStream.close();
        // }
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        // if (mAllSpriteCommandList.size() == 0) {
        // setEmptyStage();
        // // createDemoSprite();
        // }
        //
        // loadAllSpriteNameList();
        //
        // setChanged();
        // notifyObservers();
    }

    public void saveContent() {
        saveContent(tempFile);
    }

    public void saveContent(String projectName) {

        try {
            StorageHandler.getInstance().saveProject(project);
        } catch (IOException e) {
            // TODO show error dialog
        }

        // //
        // ((Activity)mCtx).setTitle(title.replace(ConstructionSiteActivity.DEFAULT_FILE_ENDING,
        // // "").replace("/", ""));
        // // TODO: setTitle-> ClassCastException Testing
        // ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>
        // spriteBrickList = new ArrayList<Pair<String,
        // ArrayList<HashMap<String, String>>>>();
        // for (int i = 0; i < mAllSpriteCommandList.size(); i++) {
        // spriteBrickList.add(mAllSpriteCommandList.get(i));
        // }
        //
        // FileOutputStream fd = mFileSystem.createOrOpenFileOutput(
        // Utils.concatPaths(ConstructionSiteActivity.ROOT, file), mContext);
        // DataOutputStream ps = new DataOutputStream(fd);
        //
        // String xml = mParser.toXml(spriteBrickList, mContext);
        // try {
        // ps.write(xml.getBytes());
        // ps.close();
        // fd.close();
        // } catch (IOException e) {
        // Log.e("Contentmanager", "ERROR saving file " + e.getMessage());
        // e.printStackTrace();
        // }
        //
        // Log.d("Contentmanager", "Save file!");
    }

    public void resetContent() {
        try {
            project = new Project(context, project.getName());
            currentSprite = project.getSpriteList().get(0); // stage
        } catch (NameNotFoundException e) {
        }
        // mCurrentSpriteCommandList = null;
        // mAllSpriteCommandList.clear();
        // mAllSpriteNameList.clear();
        // mCurrentSpritePosition = 0;
        // mBrickIdCounter = 0;
        // mCurrentSpriteCostumeNameList.clear();
    }

    // obsolete
    public void addSprite(Pair<String, ArrayList<HashMap<String, String>>> sprite) {
        mAllSpriteCommandList.add(sprite);
        mCurrentSpritePosition = mAllSpriteCommandList.size() - 1;
        mAllSpriteNameList.add(sprite.first);
        switchSprite(mCurrentSpritePosition);
    }

    public void addSprite(Sprite sprite) {
        // or create here the sprite?
        project.addSprite(sprite);
        currentSprite = sprite;
    }

    // obsolete
    public void switchSprite(int position) {
        mCurrentSpriteCommandList = mAllSpriteCommandList.get(position).second;
        mCurrentSpritePosition = position;
        loadCurrentSpriteCostumeNameList();
        setChanged();
        notifyObservers();
    }

//    public void switchSprite(int position) {
//        if (position >= 0 && position < project.getSpriteList().size()) {
//            currentSprite = project.getSpriteList().get(position);
//            setChanged();
//            notifyObservers();
//        }
//    }

    //obsolete
    public void addBrick(HashMap<String, String> map) {
        map.put(BrickDefine.BRICK_ID, ((Integer) mBrickIdCounter).toString());
        mBrickIdCounter++;
        mCurrentSpriteCommandList.add(map);

        setChanged();
        notifyObservers(mCurrentSpriteCommandList.size() - 1);
    }

    public void addBrick(Brick brick) { // adding brick to last script
        List<Script> scriptList = currentSprite.getScriptList();
        if(!scriptList.isEmpty())
            scriptList.get(scriptList.size() - 1).addBrick(brick);
        //TODO: setChanged()
    }

//    private void deleteSound(String soundName) {
//        if (soundName == null || soundName.length() == 0) {
//            Log.d("ContentManager", "No sound file to delete.");
//        } else {
//            String soundsPath = ConstructionSiteActivity.ROOT_SOUNDS;
//            String soundFilePath = Utils.concatPaths(soundsPath, soundName);
//            if (Utils.deleteFile(soundFilePath)) {
//                Log.d("ContentManager", "Successfully deleted sound file \"" + soundFilePath + "\".");
//            } else {
//                Log.w("ContentManager", "Error! Could not delete sound file \"" + soundFilePath + "\".");
//            }
//        }
//    }

    // Obsolete
//    public void removeBrick(int position) {
//        int type = Integer.parseInt(mCurrentSpriteCommandList.get(position).get(BrickDefine.BRICK_TYPE));
//        if (type == BrickDefine.SET_BACKGROUND || type == BrickDefine.SET_COSTUME) {
//            mCurrentSpriteCostumeNameList
//                    .remove(mCurrentSpriteCommandList.get(position).get(BrickDefine.BRICK_VALUE_1));
//        } else if (type == BrickDefine.PLAY_SOUND) {
//            Log.d("ContentManager", "Deleting \"Play sound\" brick.");
//            String soundName = mCurrentSpriteCommandList.get(position).get(BrickDefine.BRICK_VALUE);
//            mCurrentSpriteCommandList.remove(position);
//            setChanged();
//            notifyObservers();
//            deleteSound(soundName);
//            return;
//        }
//        mCurrentSpriteCommandList.remove(position);
//        setChanged();
//        notifyObservers();
//    }

    public boolean removeBrick(int position) {
        int counter = 0;
        if (position >= 0) {
            for (Script s : currentSprite.getScriptList()) {
                if (counter + s.getBrickList().size() > position) {
                    Brick tempBrick = s.getBrickList().get(position - counter);
                    s.getBrickList().remove(position - counter);
                    if (tempBrick instanceof SetCostumeBrick) {
                        if (((SetCostumeBrick) tempBrick).getCostume() != null) {
                            currentSprite.getCostumeList().remove(((SetCostumeBrick) tempBrick).getCostume());
                        }
                    }
                    if (tempBrick instanceof PlaySoundBrick) {
                        Utils.deleteFile(((PlaySoundBrick) tempBrick).getPathToSoundFile());
                    }
                    setChanged();
                    notifyObservers();
                    return true;
                }
                counter += s.getBrickList().size();
            }
        }
        return false;
    }

    public boolean moveBrickUpInList(int position) {
        int counter = 0;
        if (position >= 0) {
            for (Script s : currentSprite.getScriptList()) {
                if (counter + s.getBrickList().size() > position) {
                    s.moveBrickBySteps(s.getBrickList().get(position - counter), -1);
                    setChanged();
                    notifyObservers(position - 1);
                    return true;
                }
                counter += s.getBrickList().size();
            }
        }
        return false;

        // if (position > 0 && position < mCurrentSpriteCommandList.size()) {
        // HashMap<String, String> map =
        // mCurrentSpriteCommandList.get(position);
        // mCurrentSpriteCommandList.remove(position);
        // mCurrentSpriteCommandList.add(position - 1, map);
        //
        // loadCurrentSpriteCostumeNameList();
        // setChanged();
        // notifyObservers(position - 1);
        // return true;
        // }
        // return false;
    }

    public boolean moveBrickDownInList(int position) {

        int counter = 0;
        if (position >= 0) {
            for (Script s : currentSprite.getScriptList())
            {
                if (counter + s.getBrickList().size() > position) {
                    s.moveBrickBySteps(s.getBrickList().get(position - counter), 1);
                    setChanged();
                    notifyObservers(position + 1);
                    return true;
                }
                counter += s.getBrickList().size();
            }
        }
        return false;


        // if (position < mCurrentSpriteCommandList.size() - 1 && position >= 0)
        // {
        // HashMap<String, String> map =
        // mCurrentSpriteCommandList.get(position);
        // mCurrentSpriteCommandList.remove(position);
        // mCurrentSpriteCommandList.add(position + 1, map);
        //
        // loadCurrentSpriteCostumeNameList();
        // setChanged();
        // notifyObservers(position + 1);
        // return true;
        // }
        // return false;
    }

    public ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> getAllSpriteCommandList() {
        return mAllSpriteCommandList;
    }

//    private void setAllSpriteCommandList(ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> list) {
//        mAllSpriteCommandList = list;
//    }

 // Obsolete
    public void loadAllSpriteNameList() {
        mAllSpriteNameList.clear();
        for (int i = 0; i < mAllSpriteCommandList.size(); i++)
            mAllSpriteNameList.add(mAllSpriteCommandList.get(i).first);
    }

 // Obsolete
    public ArrayList<String> getAllSpriteNameList() {
        return mAllSpriteNameList;
    }

 // Obsolete
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

            if (type.equals(String.valueOf(BrickDefine.SET_BACKGROUND))
                    || type.equals(String.valueOf(BrickDefine.SET_COSTUME))) {

                mCurrentSpriteCostumeNameList.add(mCurrentSpriteCommandList.get(i).get(BrickDefine.BRICK_VALUE_1));
            }
        }
    }

    // stage is first sprite of project so this function is obsolete
    public void setEmptyStage() {
        // initialize stage
        mCurrentSpriteCommandList = new ArrayList<HashMap<String, String>>();
        mCurrentSpritePosition = 0;
        mAllSpriteCommandList.add(new Pair<String, ArrayList<HashMap<String, String>>>(mContext
                .getString(R.string.stage), mCurrentSpriteCommandList));
        this.loadAllSpriteNameList(); // if we do not call this here, we will
        // have problems at the sprite dialog!

    }

    // obsolete
    public void initializeNewProject() {
        resetContent();
        // setEmptyStage();
        loadAllSpriteNameList();
        setChanged();
        notifyObservers();
    }

    public void initializeNewProject(String projectName) {
        try {
            project = new Project(context,projectName);
            currentSprite = project.getSpriteList().get(0);
            setChanged();
            notifyObservers();
        } catch (NameNotFoundException e) {
            // TODO show error dialog
        }

    }

    // public void createDemoSprite() {
    // // create a new sprite with 3 costumes
    // Pair<String, ArrayList<HashMap<String, String>>> sprite = new
    // Pair<String, ArrayList<HashMap<String, String>>>(mContext.getResources()
    // .getText(R.string.default_sprite).toString(), new
    // ArrayList<HashMap<String, String>>());
    // this.addSprite(sprite);
    //
    // ImageContainer imageContainer = ImageContainer.getInstance();
    // Bitmap costume1 = ((BitmapDrawable)
    // mContext.getResources().getDrawable(R.drawable.catroid)).getBitmap();
    // String image1Path = imageContainer.saveImageFromBitmap(costume1,
    // "catroid.png", false, mContext);
    // String thumb1Path = imageContainer.saveThumbnailFromBitmap(costume1,
    // "catroid_thumb.png", false, mContext);
    // Bitmap costume2 = ((BitmapDrawable)
    // mContext.getResources().getDrawable(R.drawable.catroid_banzai)).getBitmap();
    // String image2Path = imageContainer.saveImageFromBitmap(costume2,
    // "catroid_banzai.png", false, mContext);
    // String thumb2Path = imageContainer.saveThumbnailFromBitmap(costume2,
    // "catroid_banzai_thumb.png", false, mContext);
    // Bitmap costume3 = ((BitmapDrawable)
    // mContext.getResources().getDrawable(R.drawable.catroid_cheshire)).getBitmap();
    // String image3Path = imageContainer.saveImageFromBitmap(costume3,
    // "catroid_cheshire.png", false, mContext);
    // String thumb3Path = imageContainer.saveThumbnailFromBitmap(costume3,
    // "catroid_cheshire_thumb.png", false, mContext);
    //
    // HashMap<String, String> map = new HashMap<String, String>();
    // map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.GO_TO));
    // map.put(BrickDefine.BRICK_VALUE, "100");
    // map.put(BrickDefine.BRICK_VALUE_1, "100");
    // this.addBrick(map);
    //
    // map = new HashMap<String, String>();
    // map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
    // map.put(BrickDefine.BRICK_VALUE, image1Path);
    // map.put(BrickDefine.BRICK_VALUE_1, thumb1Path);
    // this.addBrick(map);
    //
    // map = new HashMap<String, String>();
    // map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
    // map.put(BrickDefine.BRICK_VALUE, "2");
    // this.addBrick(map);
    //
    // map = new HashMap<String, String>();
    // map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
    // map.put(BrickDefine.BRICK_VALUE, image2Path);
    // map.put(BrickDefine.BRICK_VALUE_1, thumb2Path);
    // this.addBrick(map);
    //
    // map = new HashMap<String, String>();
    // map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.WAIT));
    // map.put(BrickDefine.BRICK_VALUE, "2");
    // this.addBrick(map);
    //
    // map = new HashMap<String, String>();
    // map.put(BrickDefine.BRICK_TYPE, String.valueOf(BrickDefine.SET_COSTUME));
    // map.put(BrickDefine.BRICK_VALUE, image3Path);
    // map.put(BrickDefine.BRICK_VALUE_1, thumb3Path);
    // this.addBrick(map);
    //
    // loadCurrentSpriteCostumeNameList();
    // }

    public void setObserver(Observer observer) {
        addObserver(observer);
    }

//    private int getHighestBrickId() {
//        ArrayList<Pair<String, ArrayList<HashMap<String, String>>>> spriteList = new ArrayList<Pair<String, ArrayList<HashMap<String, String>>>>(
//                mAllSpriteCommandList);
//
//        int highestId = 0;
//        for (int i = 0; i < mAllSpriteCommandList.size(); i++) {
//            ArrayList<HashMap<String, String>> sprite = spriteList.get(i).second;
//            for (int j = 0; j < sprite.size(); j++) {
//                HashMap<String, String> brickList = sprite.get(j);
//                brickList.get(BrickDefine.BRICK_ID);
//                if (brickList.size() > 0 && !(brickList.get(BrickDefine.BRICK_ID).equals(""))) {
//                    int tempId = Integer.valueOf(brickList.get(BrickDefine.BRICK_ID).toString()).intValue();
//                    boolean test = (highestId < tempId);
//                    if (test) {
//                        highestId = tempId;
//                    }
//                }
//            }
//        }
//        return (highestId + 1); // ID immer aktuellste freie
//    }

    /**
     * test method
     */
    public int getBrickIdCounter() {
        return mBrickIdCounter;
    }
}