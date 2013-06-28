package org.catrobat.catroid.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.test.AndroidTestCase;
import android.util.Log;
import cucumber.api.DataTable;
import cucumber.api.android.CucumberInstrumentation;
import cucumber.api.java.en.And;
import gherkin.formatter.model.DataTableRow;
import org.catrobat.catroid.R;
import org.catrobat.catroid.common.Constants;
import org.catrobat.catroid.common.LookData;
import org.catrobat.catroid.content.*;
import org.catrobat.catroid.content.bricks.*;
import org.catrobat.catroid.io.StorageHandler;
import org.catrobat.catroid.utils.ImageEditing;
import org.catrobat.catroid.utils.Utils;

import java.io.*;

public class ScriptSteps extends AndroidTestCase {
    private Sprite mCurrentSprite;

    @And("^an? \\b(background|object) '(\\w+)' that has a (\\w+Script) with these bricks:$")
    public void object_that_has_script_with_bricks(String type, String name, String scriptName, DataTable bricks) {
        Project project = (Project) RunCukes.get(RunCukes.KEY_PROJECT);
        if ("background".equals(type)) {
            mCurrentSprite = new Sprite("background");
            mCurrentSprite.look.setZIndex(0);
        } else {
            mCurrentSprite = new Sprite(name);
        }
        Script script = newScript(scriptName);
        addBricks(script, bricks);
        mCurrentSprite.addScript(script);
        project.addSprite(mCurrentSprite);
    }

    @And("^a (\\w+Script) with these bricks:$")
    public void script_with_bricks(String scriptName, DataTable bricks) {
        Script script = newScript(scriptName);
        addBricks(script, bricks);
        mCurrentSprite.addScript(script);
    }

    @And("^a BroadcastScript for the (\\w+) message with these bricks:$")
    public void broadcast_script_with_bricks(String message, DataTable bricks) {
        BroadcastScript script = new BroadcastScript(mCurrentSprite, message);
        addBricks(script, bricks);
        mCurrentSprite.addScript(script);
    }

    private Script newScript(String name) {
        if ("StartScript".equals(name)) {
            return new StartScript(mCurrentSprite);
        } else if ("WhenTappedScript".equals(name)) {
            WhenScript script = new WhenScript(mCurrentSprite);
            script.setAction(0);
            return script;
        } else {
            fail(String.format("No script for name '%s'", name));
            return null;
        }
    }

    private void addBricks(Script script, DataTable bricks) {
        for (DataTableRow row : bricks.getGherkinRows()) {
            String brickName = row.getCells().get(0);
            String argName = row.getCells().get(1);
            try {
                Brick brick = newBrick(brickName, argName);
                script.addBrick(brick);
            } catch (IOException e) {
                Log.e(CucumberInstrumentation.TAG, e.toString());
                fail(e.getMessage());
            }
        }
    }

    private LoopBeginBrick mLoopBeginBrick;

    private Brick newBrick(String className, String arg) throws IOException {
        if (className.equals(SetLookBrick.class.getSimpleName())) {
            SetLookBrick brick = new SetLookBrick(mCurrentSprite);
            LookData lookData = null;
            if ("background".equals(arg)) {
                lookData = newLookData("background", createBackgroundImage("background"));
            } else if ("default_image".equals(arg) || arg == null) {
                // By default, use a default image for the look.
                lookData = newLookData(mCurrentSprite.getName() + "-look", R.drawable.default_project_mole_1);
            } else {
                fail(String.format("No look for argument '%s'", arg));
            }
            mCurrentSprite.getLookDataList().add(lookData);
            brick.setLook(lookData);
            return brick;
        } else if (className.equals(BroadcastBrick.class.getSimpleName())) {
            BroadcastBrick brick = new BroadcastBrick(mCurrentSprite, arg);
            return brick;
        } else if (className.equals(ChangeYByNBrick.class.getSimpleName())) {
            int dy = Integer.parseInt(arg);
            ChangeYByNBrick brick = new ChangeYByNBrick(mCurrentSprite, dy);
            return brick;
        } else if (className.equals(HideBrick.class.getSimpleName())) {
            return new HideBrick(mCurrentSprite);
        } else if (className.equals(ShowBrick.class.getSimpleName())) {
            return new ShowBrick(mCurrentSprite);
        } else if (className.equals(RepeatBrick.class.getSimpleName())) {
            int n = Integer.parseInt(arg);
            mLoopBeginBrick = new RepeatBrick(mCurrentSprite, n);
            return mLoopBeginBrick;
        } else if (className.equals(LoopEndBrick.class.getSimpleName())) {
            Brick brick = new LoopEndBrick(mCurrentSprite, mLoopBeginBrick);
            mLoopBeginBrick = null;
            return brick;
        } else {
            fail(String.format("Unsupported brick '%s'", className));
            return null;
        }
    }

    private LookData newLookData(String name, int resourceId) throws IOException {
        Project project = (Project) RunCukes.get(RunCukes.KEY_PROJECT);
        File file = copyAndScaleImageToProject(project.getName(), getContext(), name, resourceId);
        return newLookData(name, file);
    }

    private LookData newLookData(String name, File file) {
        LookData look = new LookData();
        look.setLookName(name);
        look.setLookFilename(file.getName());
        return look;
    }

    private File copyAndScaleImageToProject(String projectName, Context context, String imageName, int imageId) throws IOException {
        String directoryName = Utils.buildPath(Utils.buildProjectPath(projectName), Constants.IMAGE_DIRECTORY);
        File tempImageFile = savePictureFromResourceInProject(projectName, imageName, imageId, context);
        int[] dimensions = ImageEditing.getImageDimensions(tempImageFile.getAbsolutePath());
        int originalWidth = dimensions[0];
        int originalHeight = dimensions[1];
        double ratio = (double) originalHeight / (double) originalWidth;
        Point screen = Util.getScreenDimensions(getContext());
        Bitmap tempBitmap = ImageEditing.getScaledBitmapFromPath(tempImageFile.getAbsolutePath(), screen.x / 3, (int) (screen.x / 3 * ratio), false);
        StorageHandler.saveBitmapToImageFile(tempImageFile, tempBitmap);
        String finalImageFileString = Utils.buildPath(directoryName, Utils.md5Checksum(tempImageFile) + "_" + tempImageFile.getName());
        File finalImageFile = new File(finalImageFileString);
        tempImageFile.renameTo(finalImageFile);
        return finalImageFile;
    }

    private File savePictureFromResourceInProject(String project, String outputName, int fileId, Context context) throws IOException {
        final String imagePath = Utils.buildPath(Utils.buildProjectPath(project), Constants.IMAGE_DIRECTORY, outputName);
        File testImage = new File(imagePath);
        if (!testImage.exists()) {
            testImage.createNewFile();
        }
        InputStream in = context.getResources().openRawResource(fileId);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Constants.BUFFER_8K);
        byte[] buffer = new byte[Constants.BUFFER_8K];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.flush();
        out.close();
        return testImage;
    }

    private File createBackgroundImage(String name) {
        Project project = (Project) RunCukes.get(RunCukes.KEY_PROJECT);
        String directoryName = Utils.buildPath(Utils.buildProjectPath(project.getName()), Constants.IMAGE_DIRECTORY);
        Point screen = Util.getScreenDimensions(getContext());
        Bitmap backgroundBitmap = ImageEditing.createSingleColorBitmap(screen.x, screen.y, Color.BLUE);
        try {
            File backgroundTemp = File.createTempFile(name, ".png", new File(directoryName));
            StorageHandler.saveBitmapToImageFile(backgroundTemp, backgroundBitmap);
            File backgroundFile = new File(directoryName, Utils.md5Checksum(backgroundTemp) + "_" + backgroundTemp.getName());
            backgroundTemp.renameTo(backgroundFile);
            return backgroundFile;
        } catch (IOException e) {
            Log.e(CucumberInstrumentation.TAG, e.toString());
            fail(e.getMessage());
            return null;
        }
    }
}
