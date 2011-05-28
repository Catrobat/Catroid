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
package at.tugraz.ist.catroid.io;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import at.tugraz.ist.catroid.ProjectManager;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.common.Consts;
import at.tugraz.ist.catroid.common.FileChecksumContainer;
import at.tugraz.ist.catroid.common.SoundInfo;
import at.tugraz.ist.catroid.content.Costume;
import at.tugraz.ist.catroid.content.Project;
import at.tugraz.ist.catroid.content.Script;
import at.tugraz.ist.catroid.content.Sprite;
import at.tugraz.ist.catroid.content.StartScript;
import at.tugraz.ist.catroid.content.TapScript;
import at.tugraz.ist.catroid.content.bricks.ChangeXByBrick;
import at.tugraz.ist.catroid.content.bricks.ChangeYByBrick;
import at.tugraz.ist.catroid.content.bricks.ComeToFrontBrick;
import at.tugraz.ist.catroid.content.bricks.GlideToBrick;
import at.tugraz.ist.catroid.content.bricks.GoNStepsBackBrick;
import at.tugraz.ist.catroid.content.bricks.HideBrick;
import at.tugraz.ist.catroid.content.bricks.IfStartedBrick;
import at.tugraz.ist.catroid.content.bricks.IfTouchedBrick;
import at.tugraz.ist.catroid.content.bricks.PlaceAtBrick;
import at.tugraz.ist.catroid.content.bricks.PlaySoundBrick;
import at.tugraz.ist.catroid.content.bricks.ScaleCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetCostumeBrick;
import at.tugraz.ist.catroid.content.bricks.SetXBrick;
import at.tugraz.ist.catroid.content.bricks.SetYBrick;
import at.tugraz.ist.catroid.content.bricks.ShowBrick;
import at.tugraz.ist.catroid.content.bricks.WaitBrick;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.XStream;

/**
 * @author Peter Treitler
 * 
 */
public class StorageHandler {

	private static StorageHandler instance;
	private static final String TAG = "StorageHandler";
	private ArrayList<SoundInfo> soundContent;
	private XStream xstream;

	private StorageHandler() throws IOException {
		String state = Environment.getExternalStorageState();
		xstream = new XStream();
		xstream.alias("project", Project.class);
		xstream.alias("sprite", Sprite.class);
		xstream.alias("script", Script.class);
		xstream.alias("startScript", StartScript.class);
		xstream.alias("tapScript", TapScript.class);
		xstream.alias("costume", Costume.class);

		xstream.alias("changeXByBrick", ChangeXByBrick.class);
		xstream.alias("changeYByBrick", ChangeYByBrick.class);
		xstream.alias("comeToFrontBrick", ComeToFrontBrick.class);
		xstream.alias("goNStepsBackBrick", GoNStepsBackBrick.class);
		xstream.alias("hideBrick", HideBrick.class);
		xstream.alias("ifStartedBrick", IfStartedBrick.class);
		xstream.alias("ifTouchedBrick", IfTouchedBrick.class);
		xstream.alias("placeAtBrick", PlaceAtBrick.class);
		xstream.alias("playSoundBrick", PlaySoundBrick.class);
		xstream.alias("scaleCostumeBrick", ScaleCostumeBrick.class);
		xstream.alias("setCostumeBrick", SetCostumeBrick.class);
		xstream.alias("setXBrick", SetXBrick.class);
		xstream.alias("setYBrick", SetYBrick.class);
		xstream.alias("showBrick", ShowBrick.class);
		xstream.alias("waitBrick", WaitBrick.class);
		xstream.alias("glideToBrick", GlideToBrick.class);

		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			throw new IOException("Could not read external storage");
		}
		createCatroidRoot();

	}

	private void createCatroidRoot() {
		// We can read and write the media
		File catroidRoot = new File(Consts.DEFAULT_ROOT);
		if (!catroidRoot.exists()) {
			catroidRoot.mkdirs();
		}
	}

	public synchronized static StorageHandler getInstance() {
		if (instance == null) {
			try {
				instance = new StorageHandler();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, "Exception in Storagehandler, please refer to the StackTrace");
			}
		}
		return instance;
	}

	public Project loadProject(String projectName) {
		createCatroidRoot();
		try {
			projectName = Utils.getProjectName(projectName);

			File projectDirectory = new File(Consts.DEFAULT_ROOT + "/" + projectName);

			if (projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite()) {
				InputStream spfFileStream = new FileInputStream(projectDirectory.getAbsolutePath() + "/" + projectName
						+ Consts.PROJECT_EXTENTION);
				return (Project) xstream.fromXML(spfFileStream);
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean saveProject(Project project) {
		createCatroidRoot();
		if (project == null) {
			return false;
		}
		try {
			String spfFile = xstream.toXML(project);

			String projectDirectoryName = Consts.DEFAULT_ROOT + "/" + project.getName();
			File projectDirectory = new File(projectDirectoryName);

			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();

				File imageDirectory = new File(projectDirectoryName + Consts.IMAGE_DIRECTORY);
				imageDirectory.mkdir();

				File noMediaFile = new File(projectDirectoryName + Consts.IMAGE_DIRECTORY + "/.nomedia");
				noMediaFile.createNewFile();

				File soundDirectory = new File(projectDirectoryName + Consts.SOUND_DIRECTORY);
				soundDirectory.mkdir();

				noMediaFile = new File(projectDirectoryName + Consts.SOUND_DIRECTORY + "/.nomedia");
				noMediaFile.createNewFile();
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(projectDirectoryName + "/" + project.getName()
					+ Consts.PROJECT_EXTENTION), Consts.BUFFER_8K);

			out.write(spfFile);
			out.flush();
			out.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.e(TAG, "saveProject threw an exception and failed.");
			return false;
		}
	}

	public boolean deleteProject(Project project) {
		if (project != null) {
			return UtilFile.deleteDirectory(new File(Consts.DEFAULT_ROOT + "/" + project.getName()));
		}
		return false;
	}

	public boolean projectExists(String projectName) {
		File projectDirectory = new File(Consts.DEFAULT_ROOT + "/" + projectName);
		if (!projectDirectory.exists()) {
			return false;
		}
		return true;
	}

	public void loadSoundContent(Context context) {
		soundContent = new ArrayList<SoundInfo>();
		String[] projectionOnOrig = { MediaStore.Audio.Media.DATA, MediaStore.Audio.AudioColumns.TITLE,
				MediaStore.Audio.Media._ID };

		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projectionOnOrig, null, null, null);

		if (cursor.moveToFirst()) {
			int columnDataIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			int columnTitleIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
			int columnIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

			do {
				SoundInfo info = new SoundInfo();
				info.setId(cursor.getInt(columnIdIndex));
				info.setTitle(cursor.getString(columnTitleIndex));
				info.setPath(cursor.getString(columnDataIndex));
				soundContent.add(info);
			} while (cursor.moveToNext());
		}
		Log.v(TAG, "LOAD SOUND");
		cursor.close();
	}

	public ArrayList<SoundInfo> getSoundContent() {
		java.util.Collections.sort(soundContent);
		return soundContent;
	}

	public void setSoundContent(ArrayList<SoundInfo> soundContent) {
		if (this.soundContent == null) {
			this.soundContent = new ArrayList<SoundInfo>();
		} else {
			this.soundContent.clear();
		}
		this.soundContent.addAll(soundContent);
	}

	public File copySoundFile(String path) throws IOException {
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File soundDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProject + Consts.SOUND_DIRECTORY);

		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}
		String inputFileChecksum = getMD5Checksum(inputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;
		if (fileChecksumContainer.containsChecksum(inputFileChecksum)) {
			fileChecksumContainer.addChecksum(inputFileChecksum, null);
			return new File(fileChecksumContainer.getPath(inputFileChecksum));
		}
		File outputFile = new File(soundDirectory.getAbsolutePath() + "/" + inputFileChecksum + "_"
				+ inputFile.getName());

		return copyFile(outputFile, inputFile, soundDirectory);
	}

	public File copyImage(String currentProjectName, String inputFilePath) throws IOException {
		File imageDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName
						+ Consts.IMAGE_DIRECTORY);

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead()) {
			return null;
		}

		int[] imageDimensions = new int[2];
		imageDimensions = ImageEditing.getImageDimensions(inputFilePath);
		FileChecksumContainer checksumCont = ProjectManager.getInstance().fileChecksumContainer;

		if ((imageDimensions[0] <= Consts.MAX_COSTUME_WIDTH) && (imageDimensions[1] <= Consts.MAX_COSTUME_HEIGHT)) {
			String checksumSource = getMD5Checksum(inputFile);

			String newFilePath = imageDirectory.getAbsolutePath() + "/" + checksumSource + "_" + inputFile.getName();
			if (checksumCont.containsChecksum(checksumSource)) {
				checksumCont.addChecksum(checksumSource, newFilePath);
				return new File(checksumCont.getPath(checksumSource));
			}
			File outputFile = new File(newFilePath);
			return copyFile(outputFile, inputFile, imageDirectory);
		} else {
			File outputFile = new File(imageDirectory + "/" + inputFile.getName());
			return copyAndResizeImage(outputFile, inputFile, imageDirectory);
		}
	}

	private File copyAndResizeImage(File outputFile, File inputFile, File imageDirectory) throws IOException {
		FileOutputStream outputStream = new FileOutputStream(outputFile);
		Bitmap bitmap = ImageEditing.getBitmap(inputFile.getAbsolutePath(), Consts.MAX_COSTUME_WIDTH,
				Consts.MAX_COSTUME_HEIGHT);
		try {
			String name = inputFile.getName();
			if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".JPG") || name.endsWith(".JPEG")) {
				bitmap.compress(CompressFormat.JPEG, Consts.JPG_COMPRESSION_SETING, outputStream);
			} else {
				bitmap.compress(CompressFormat.PNG, Consts.JPG_COMPRESSION_SETING, outputStream);
			}
			outputStream.flush();
			outputStream.close();
		} catch (IOException e) {

		}

		String checksumCompressedFile = StorageHandler.getInstance().getMD5Checksum(outputFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;
		String newFilePath = imageDirectory.getAbsolutePath() + "/" + checksumCompressedFile + "_"
				+ inputFile.getName();

		if (!fileChecksumContainer.addChecksum(checksumCompressedFile, newFilePath)) {
			outputFile.delete();
			return new File(fileChecksumContainer.getPath(checksumCompressedFile));
		}

		File compressedFile = new File(newFilePath);
		outputFile.renameTo(compressedFile);

		return compressedFile;
	}

	private File copyFile(File destinationFile, File sourceFile, File directory) throws IOException {
		FileChannel inputChannel = new FileInputStream(sourceFile).getChannel();
		FileChannel outputChannel = new FileOutputStream(destinationFile).getChannel();

		String checksumSource = getMD5Checksum(sourceFile);
		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().fileChecksumContainer;

		try {
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
			return destinationFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputChannel != null) {
				inputChannel.close();
			}
			if (outputChannel != null) {
				outputChannel.close();
			}
		}
	}

	public void deleteFile(String filepath) {
		FileChecksumContainer container = ProjectManager.getInstance().fileChecksumContainer;
		try {
			if (container.decrementUsage(filepath)) {
				File toDelete = new File(filepath);
				toDelete.delete();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getMD5Checksum(File file) {

		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "NoSuchAlgorithmException thrown in StorageHandler::getMD5Checksum");
		}

		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			byte[] buffer = new byte[Consts.BUFFER_8K];

			int length = 0;

			while ((length = fis.read(buffer)) != -1) {
				messageDigest.update(buffer, 0, length);
			}
		} catch (IOException e) {
			Log.e(TAG, "IOException thrown in StorageHandler::getMD5Checksum");
			e.printStackTrace();
		} finally {
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException thrown in StorageHandler::getMD5Checksum by FileInputStream.close()");
			}
		}

		byte[] mdbytes = messageDigest.digest();
		StringBuilder md5StringBuilder = new StringBuilder(2 * mdbytes.length);

		for (byte b : mdbytes) {
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0xF0) >> 4));
			md5StringBuilder.append("0123456789ABCDEF".charAt((b & 0x0F)));
		}

		return md5StringBuilder.toString();
	}

	/**
	 * Creates the default project and saves it to the filesystem
	 * 
	 * @return the default project object if successful, else null
	 * @throws IOException
	 */
	public Project createDefaultProject(Context context) throws IOException {
		String projectName = context.getString(R.string.default_project_name);
		Project defaultProject = new Project(context, projectName);
		saveProject(defaultProject);
		ProjectManager.getInstance().setProject(defaultProject);
		Sprite sprite = new Sprite("Catroid");
		Sprite stageSprite = defaultProject.getSpriteList().get(0);
		//scripts:
		Script stageStartScript = new StartScript("stageStartScript", stageSprite);
		Script startScript = new StartScript("startScript", sprite);
		Script touchScript = new TapScript("touchScript", sprite);
		//bricks:
		File normalCat = savePictureFromResInProject(projectName, Consts.NORMAL_CAT, R.drawable.catroid, context);
		File banzaiCat = savePictureFromResInProject(projectName, Consts.BANZAI_CAT, R.drawable.catroid_banzai, context);
		File cheshireCat = savePictureFromResInProject(projectName, Consts.CHESHIRE_CAT, R.drawable.catroid_cheshire,
				context);
		File background = savePictureFromResInProject(projectName, Consts.BACKGROUND, R.drawable.background_blueish,
				context);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(normalCat.getName());

		SetCostumeBrick setCostumeBrick1 = new SetCostumeBrick(sprite);
		setCostumeBrick1.setCostume(normalCat.getName());

		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(sprite);
		setCostumeBrick2.setCostume(banzaiCat.getName());

		SetCostumeBrick setCostumeBrick3 = new SetCostumeBrick(sprite);
		setCostumeBrick3.setCostume(cheshireCat.getName());

		SetCostumeBrick setCostumeBackground = new SetCostumeBrick(stageSprite);
		setCostumeBackground.setCostume(background.getName());

		WaitBrick waitBrick1 = new WaitBrick(sprite, 500);
		WaitBrick waitBrick2 = new WaitBrick(sprite, 500);

		startScript.addBrick(setCostumeBrick);

		touchScript.addBrick(setCostumeBrick2);
		touchScript.addBrick(waitBrick1);
		touchScript.addBrick(setCostumeBrick3);
		touchScript.addBrick(waitBrick2);
		touchScript.addBrick(setCostumeBrick1);
		stageStartScript.addBrick(setCostumeBackground);

		//merging:
		defaultProject.addSprite(sprite);
		sprite.getScriptList().add(startScript);
		sprite.getScriptList().add(touchScript);
		stageSprite.getScriptList().add(stageStartScript);
		//ProjectManager.getInstance().setProject(defaultProject);
		this.saveProject(defaultProject);
		return defaultProject;
	}

	private File savePictureFromResInProject(String project, String name, int fileID, Context context)
			throws IOException {

		final String imagePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/" + name;
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), Consts.BUFFER_8K);
		byte[] buffer = new byte[Consts.BUFFER_8K];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}
}
