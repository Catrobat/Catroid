package at.tugraz.ist.catroid.test.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.utils.UtilFile;

public class Utils {

	public static File savePictureInProject(String project, String name, int fileID, Context context)
			throws IOException {

		BufferedInputStream in = new BufferedInputStream(context.getResources().openRawResource(fileID));
		final String imagePath = Consts.DEFAULT_ROOT + "/" + project + Consts.IMAGE_DIRECTORY + "/" + name;
		File testImage = new File(imagePath);
		testImage.createNewFile();

		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), 1024);
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}

		in.close();
		out.flush();
		out.close();

		return testImage;
	}

	public static File saveSoundFileInProject(String project, String name, int fileID, Context context)
			throws IOException {
		// Note: File needs to be copied as MediaPlayer has no access to resources
		BufferedInputStream inputStream = new BufferedInputStream(context.getResources().openRawResource(fileID));
		final String pathToSoundfile = Consts.DEFAULT_ROOT + "/" + project + Consts.SOUND_DIRECTORY + "/" + name;
		File soundFile = new File(pathToSoundfile);
		soundFile.createNewFile();

		BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(soundFile), 1024);

		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}

		inputStream.close();
		outputStream.flush();
		outputStream.close();

		return soundFile;
	}

	public static boolean clearProject(String projectname) {
		File directory = new File(Consts.DEFAULT_ROOT + "/" + projectname);
		if (directory.exists()) {
			return UtilFile.deleteDirectory(directory);
		}
		return false;
	}

}
