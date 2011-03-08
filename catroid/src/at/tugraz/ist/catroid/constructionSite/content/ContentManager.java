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
    private final String DEFAULT_PROJECT_NAME;

    private Sprite currentSprite;
    private final Context context;
    private Project project;

    public ContentManager(Context context, String projectName) {
        this.context = context;
        DEFAULT_PROJECT_NAME = context.getString(R.string.default_project_name);
        try {
            if (projectName != null && projectName.length() != 0) {
                if (!loadContent(projectName)) {
                    if (!loadContent(DEFAULT_PROJECT_NAME)) {
                        project = StorageHandler.getInstance().createDefaultProject(context);
                        currentSprite = project.getSpriteList().get(0); // stage
                    }
                }
            } else {
                project = StorageHandler.getInstance().createDefaultProject(context);
                currentSprite = project.getSpriteList().get(0); // stage
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean loadContent(String projectName) {
        try {
            project = StorageHandler.getInstance().loadProject(projectName);
            if (project == null) {
                project = StorageHandler.getInstance().createDefaultProject(context);
            }
            currentSprite = project.getSpriteList().get(0); // stage
            setChanged();
            notifyObservers();
            return true;
        } catch (IOException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_load_project));
            return false;
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
        setChanged();
        notifyObservers();
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

    public void addBrick(Brick brick, Script script) {
        script.addBrick(brick);
        setChanged();
        notifyObservers();
    }

    public void removeBrick(int position, Script script) {
        if (position >= 0 && position < script.getBrickList().size()) {
            script.getBrickList().remove(position);
            setChanged();
            notifyObservers();
        }
    }

    public void moveBrickUpInList(int position, Script script) {
        if (position >= 0 && position < script.getBrickList().size()) {
            script.moveBrickBySteps(script.getBrickList().get(position), -1);
            setChanged();
            notifyObservers();
        }
    }

    public void moveBrickDownInList(int position, Script script) {
        if (position >= 0 && position < script.getBrickList().size()) {
            script.moveBrickBySteps(script.getBrickList().get(position), 1);
            setChanged();
            notifyObservers();
        }
    }

    public void initializeNewProject(String projectName) {
        try {
            project = new Project(context, projectName);
            currentSprite = project.getSpriteList().get(0);
            saveContent();
            setChanged();
            notifyObservers();
        } catch (NameNotFoundException e) {
            Utils.displayErrorMessage(context, context.getString(R.string.error_save_project));
        }
    }

    public void setObserver(Observer observer) {
        addObserver(observer);
    }

    public Sprite getCurrentSprite() {
        return currentSprite;
    }
    
    public Project getCurrentProject() {
		return project;
    }
}