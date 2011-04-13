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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.FileChecksumContainer;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.brick.SetCostumeBrick;
import at.tugraz.ist.catroid.content.brick.WaitBrick;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.content.script.Script;
import at.tugraz.ist.catroid.content.sprite.Sprite;
import at.tugraz.ist.catroid.utils.ImageEditing;
import at.tugraz.ist.catroid.utils.UtilFile;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.XStream;

/**
 * @author Peter Treitler
 * 
 */
public class StorageHandler {

	public static final int FILE_EXISTED = 0;
	public static final int FILE_COPIED = 1;
	public static final int COPY_FAILED = 2;

	private static StorageHandler instance;
	private ArrayList<SoundInfo> soundContent;
	private File catroidRoot;
	private XStream xstream;

	private StorageHandler() throws IOException {
		String state = Environment.getExternalStorageState();
		xstream = new XStream();
		// xstream.aliasPackage("", "at.tugraz.ist.catroid");

		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			throw new IOException("Could not read external storage");
		}

		// We can read and write the media
		String catroidPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + Consts.DIRECTORY_NAME;
		catroidRoot = new File(catroidPath);
		if (!catroidRoot.exists()) {
			catroidRoot.mkdirs();
		}
	}

	public synchronized static StorageHandler getInstance() throws IOException {
		if (instance == null) {
			instance = new StorageHandler();
		}
		return instance;
	}

	public Project loadProject(String projectName) {
		try {
			projectName = Utils.getProjectName(projectName);

			File projectDirectory = new File(catroidRoot.getAbsolutePath() + "/" + projectName);

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

	public void saveProject(Project project) {
		if (project == null)
			return;
		try {
			String spfFile = xstream.toXML(project);

			File projectDirectory = new File(catroidRoot.getAbsolutePath() + "/" + project.getName());
			if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
				projectDirectory.mkdir();
				File imageDirectory = new File(projectDirectory.getAbsolutePath() + Consts.IMAGE_DIRECTORY);
				imageDirectory.mkdir();
				File noMediaFile = new File(projectDirectory.getAbsolutePath() + Consts.IMAGE_DIRECTORY + "/.nomedia");
				noMediaFile.createNewFile();
				File soundDirectory = new File(projectDirectory.getAbsolutePath() + Consts.SOUND_DIRECTORY);
				soundDirectory.mkdir();
				noMediaFile = new File(projectDirectory.getAbsolutePath() + Consts.SOUND_DIRECTORY + "/.nomedia");
				noMediaFile.createNewFile();
			}
			BufferedWriter out = new BufferedWriter(new FileWriter(projectDirectory.getAbsolutePath() + "/"
					+ project.getName()
					+ Consts.PROJECT_EXTENTION));
			out.write(spfFile);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteProject(Project project) {
		if (project != null)
			UtilFile.deleteDirectory(new File(catroidRoot.getAbsolutePath() + "/" + project.getName()));
	}

	public boolean projectExists(String projectName) {
		File projectDirectory = new File(catroidRoot.getAbsolutePath() + "/" + projectName);
		if (!projectDirectory.exists()) {
			return false;
		}
		return true;
	}

	// TODO: Find a way to access sound files on the device
	public void loadSoundContent(Context context) {
		if (soundContent != null) {
			return;
		}
		soundContent = new ArrayList<SoundInfo>();
		String[] projectionOnOrig = { MediaStore.Audio.Media.DATA, MediaStore.Audio.AudioColumns.TITLE,
				MediaStore.Audio.Media._ID };

		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				projectionOnOrig, null, null, null);

		if (cursor.moveToFirst()) {
			int column_data_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
			int column_title_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE);
			int column_id_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);

			do {
				SoundInfo info = new SoundInfo();
				info.setId(cursor.getInt(column_id_index));
				info.setTitle(cursor.getString(column_title_index));
				info.setPath(cursor.getString(column_data_index));
				soundContent.add(info);
			} while (cursor.moveToNext());
		}
		System.out.println("LOAD SOUND");
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
		Log.d("StorageHandler: ", "Path to original soundFile: " + path);
		String currentProject = ProjectManager.getInstance().getCurrentProject().getName();
		File soundDirectory = new File(catroidRoot.getAbsolutePath() + "/" + currentProject + Consts.SOUND_DIRECTORY);
		File inputFile = new File(path);
		if (!inputFile.exists() || !inputFile.canRead())
			return null;

		final String timestamp = Utils.getTimestamp();
		File outputFile = new File(soundDirectory.getAbsolutePath() + "/" + timestamp + inputFile.getName());

		return copyFile(outputFile, inputFile, soundDirectory);
	}

	public File copyImage(String currentProjectName, String inputFilePath) throws IOException {
		File imageDirectory = new File(catroidRoot.getAbsolutePath() + "/" + currentProjectName
				+ Consts.IMAGE_DIRECTORY);

		File inputFile = new File(inputFilePath);
		if (!inputFile.exists() || !inputFile.canRead())
			return null;

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String timestamp = simpleDateFormat.format(new Date());
		File outputFile = new File(imageDirectory.getAbsolutePath() + "/" + timestamp + inputFile.getName());

		int[] imageDimensions = new int[2];
		imageDimensions = ImageEditing.getImageDimensions(inputFilePath);

		if ((imageDimensions[0] <= Consts.MAX_COSTUME_WIDTH) && (imageDimensions[1] <= Consts.MAX_COSTUME_HEIGHT)) {
			return copyFile(outputFile, inputFile, imageDirectory);
		} else
			return copyAndResizeImage(outputFile, inputFile, imageDirectory);
	}

	public File copyAndResizeImage(File destinationFile, File sourceFile, File directory) throws IOException {

		FileOutputStream outputStream = new FileOutputStream(destinationFile);

		String checksumSource = getMD5Checksum(sourceFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getCurrentProject()
				.getFileChecksumContainer();

		if (fileChecksumContainer.containsChecksum(checksumSource)) {
			fileChecksumContainer.incrementValue(checksumSource);
			destinationFile.delete();
			return new File(fileChecksumContainer.getPath(checksumSource));
		}

		try {
			fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
			Bitmap bitmap = ImageEditing.getBitmap(sourceFile.getAbsolutePath(), Consts.MAX_COSTUME_WIDTH,
					Consts.MAX_COSTUME_HEIGHT);
			if (sourceFile.getName().contains(".jpg") || sourceFile.getName().contains(".jpeg")) {
				bitmap.compress(CompressFormat.JPEG, Consts.JPG_COMPRESSION_SETING, outputStream);
			} else {
				bitmap.compress(CompressFormat.PNG, Consts.JPG_COMPRESSION_SETING, outputStream);
			}
			outputStream.flush();
			outputStream.close();

			return destinationFile;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public File copyFile(File destinationFile, File sourceFile, File directory) throws IOException {
		FileChannel inputChannel = new FileInputStream(sourceFile).getChannel();
		FileChannel outputChannel = new FileOutputStream(destinationFile).getChannel();
		String checksumSource = getMD5Checksum(sourceFile);

		FileChecksumContainer fileChecksumContainer = ProjectManager.getInstance().getCurrentProject()
				.getFileChecksumContainer();
		if (fileChecksumContainer.containsChecksum(checksumSource)) {
			fileChecksumContainer.incrementValue(checksumSource);
			destinationFile.delete();
			return new File(fileChecksumContainer.getPath(checksumSource));
		}

		try {
			inputChannel.transferTo(0, inputChannel.size(), outputChannel);
			fileChecksumContainer.addChecksum(checksumSource, destinationFile.getAbsolutePath());
			return destinationFile;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (inputChannel != null)
				inputChannel.close();
			if (outputChannel != null)
				outputChannel.close();
		}
	}

	public String getMD5Checksum(File file) throws IOException {

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		byte[] dataBytes = new byte[1024];

		int nread = 0;

		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		;

		byte[] mdbytes = md.digest();
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		md.reset();
		return sb.toString();
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
		Sprite sprite = new Sprite("Catroid");
		//scrips:
		Script startScript = new Script("startScript", sprite);
		Script touchScript = new Script("touchScript", sprite);
		touchScript.setTouchScript(true);
		//bricks:
		File normalCat = savePictureFromResInProject(projectName, 4147, "normalCat", R.drawable.catroid, context);
		File banzaiCat = savePictureFromResInProject(projectName, 4147, "banzaiCat", R.drawable.catroid_banzai, context);
		File cheshireCat = savePictureFromResInProject(projectName, 4147, "cheshireCat", R.drawable.catroid_cheshire,
				context);

		SetCostumeBrick setCostumeBrick = new SetCostumeBrick(sprite);
		setCostumeBrick.setCostume(normalCat.getAbsolutePath());

		SetCostumeBrick setCostumeBrick2 = new SetCostumeBrick(sprite);
		setCostumeBrick2.setCostume(banzaiCat.getAbsolutePath());

		SetCostumeBrick setCostumeBrick3 = new SetCostumeBrick(sprite);
		setCostumeBrick3.setCostume(cheshireCat.getAbsolutePath());

		WaitBrick waitBrick1 = new WaitBrick(sprite, 1000);

		//define script:
		for (int i = 0; i < 5; i++) {
			startScript.addBrick(setCostumeBrick);
			startScript.addBrick(waitBrick1);
			startScript.addBrick(setCostumeBrick2);
			startScript.addBrick(waitBrick1);
		}
		startScript.addBrick(setCostumeBrick3);

		touchScript.addBrick(setCostumeBrick3);

		//merging:
		defaultProject.addSprite(sprite);
		sprite.getScriptList().add(startScript);
		sprite.getScriptList().add(touchScript);
		ProjectManager.getInstance().setProject(defaultProject);
		this.saveProject(defaultProject);
		return defaultProject;
	}

	private File savePictureFromResInProject(String project, int fileSize, String name, int fileID, Context context)
			throws IOException {

		final String imagePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/" + name;
		File testImage = new File(imagePath);
		if (!testImage.exists()) {
			testImage.createNewFile();
		}
		InputStream in = context.getResources().openRawResource(fileID);
		OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSize);
		byte[] buffer = new byte[fileSize];
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
