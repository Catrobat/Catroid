package at.tugraz.ist.catroid.constructionSite.content;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.brick.gui.Brick;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.utils.Utils;

public class ContentManager extends Observable {

    private Sprite currentSprite;
    private Context context;
    private Project project;
    private final String defaultProjectName = "defaultProject";

    public ContentManager(Context context, String projectName) throws IOException, NameNotFoundException {
        this.context = context;
        if (projectName != null && projectName.length() != 0) {
            try {
                this.loadContent(projectName);
            } catch (IOException e) {
                if (StorageHandler.getInstance().projectExists(defaultProjectName)) {
                    this.loadContent(defaultProjectName);
                } else {
                    project = new Project(context, defaultProjectName);
                    currentSprite = project.getSpriteList().get(0); //stage
                    this.saveContent();
                }
            }
        }
    }

    public void loadContent(String projectName) throws IOException {
        try {
            project = StorageHandler.getInstance().loadProject(projectName);
            currentSprite = project.getSpriteList().get(0); // stage
            setChanged();
            notifyObservers();
        } catch (IOException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
            throw e;
        }
    }

    public void saveContent() {
        try {
            StorageHandler.getInstance().saveProject(project);
        } catch (IOException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_save_project));
        }
    }

    public void resetContent() throws NameNotFoundException {
        project = new Project(context, project.getName());
        currentSprite = project.getSpriteList().get(0); // stage
    }

    public void addSprite(Sprite sprite) {
        project.addSprite(sprite);
        currentSprite = sprite;
    }

    public void switchSprite(int position) {
        if (position >= 0 && position < project.getSpriteList().size()) {
            currentSprite = project.getSpriteList().get(position);
            setChanged();
            notifyObservers();
        }
    }

    public void addBrick(Brick brick,Script script) {
        script.addBrick(brick);
//        List<Script> scriptList = currentSprite.getScriptList();
//        if(!scriptList.isEmpty())
//            scriptList.get(scriptList.size() - 1).addBrick(brick);
        setChanged();
        notifyObservers();
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

    public void removeBrick(int position, Script script) {
        if (position >= 0 && position < script.getBrickList().size()) {
            script.getBrickList().remove(position);
            setChanged();
            notifyObservers();
        }
//        int counter = 0;
//        int scriptSize = 0;
//        if (position >= 0) {
//            for (Script s : currentSprite.getScriptList()) {
//                scriptSize = s.getBrickList().size();
//                if (counter + scriptSize > position) {
//                    Brick tempBrick = s.getBrickList().get(position - counter);
//                    s.getBrickList().remove(position - counter);
//                    if (tempBrick instanceof SetCostumeBrick) {
//                        if (((SetCostumeBrick) tempBrick).getCostume() != null) {
//                            currentSprite.getCostumeList().remove(((SetCostumeBrick) tempBrick).getCostume());
//                        }
//                    }
//                    if (tempBrick instanceof PlaySoundBrick) {
//                        Utils.deleteFile(((PlaySoundBrick) tempBrick).getPathToSoundFile());
//                    }
//                    setChanged();
//                    notifyObservers();
//                    return true;
//                }
//                counter += scriptSize;
//            }
//        }
//        return false;
    }

    public void moveBrickUpInList(int position, Script script) {
        if (position >= 0 && position < script.getBrickList().size()) {
            script.moveBrickBySteps(script.getBrickList().get(position), -1);
            setChanged();
            notifyObservers();
        }
//        int counter = 0;
//        int scriptSize = 0;
//        if (position >= 0) {
//            for (Script s : currentSprite.getScriptList()) {
//                scriptSize = s.getBrickList().size();
//                if (counter + scriptSize > position) {
//                    s.moveBrickBySteps(s.getBrickList().get(position - counter), -1);
//                    setChanged();
//                    notifyObservers(position - 1);
//                    return true;
//                }
//                counter += scriptSize;
//            }
//        }
//        return false;

//         if (position > 0 && position < mCurrentSpriteCommandList.size()) {
//         HashMap<String, String> map =
//         mCurrentSpriteCommandList.get(position);
//         mCurrentSpriteCommandList.remove(position);
//         mCurrentSpriteCommandList.add(position - 1, map);
//        
//         loadCurrentSpriteCostumeNameList();
//         setChanged();
//         notifyObservers(position - 1);
//         return true;
//         }
//         return false;
    }

    public void moveBrickDownInList(int position, Script script) {
        if (position >= 0 && position < script.getBrickList().size()) {
            script.moveBrickBySteps(script.getBrickList().get(position), 1);
            setChanged();
            notifyObservers();
        }

//        int counter = 0;
//        int scriptSize = 0;
//        if (position >= 0) {
//            for (Script s : currentSprite.getScriptList())
//            {
//                scriptSize = s.getBrickList().size();
//                if (counter + scriptSize > position) {
//                    s.moveBrickBySteps(s.getBrickList().get(position - counter), 1);
//                    setChanged();
//                    notifyObservers(position + 1);
//                    return true;
//                }
//                counter += scriptSize;
//            }
//        }
//        return false;
    }

    public void initializeNewProject(String projectName) {
        try {
            project = new Project(context,projectName);
            currentSprite = project.getSpriteList().get(0);
            this.saveContent();
            setChanged();
            notifyObservers();
        } catch (NameNotFoundException e) {
            // TODO show error dialog
        }

    }

    public void setObserver(Observer observer) {
        addObserver(observer);
    }
    
    public Sprite getCurrentSprite(){
        return currentSprite;
    }
}