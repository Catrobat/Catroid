package com.parrot.freeflight.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public final class FileUtils
{
	public static final String THUMBNAILS_FOLDER = ".thumbnails";
	public static final String MEDIA_PUBLIC_FOLDER_NAME = "AR.Drone";
	public static final String NO_MEDIA_FILE = ".no_media";

	private static final String TAG = "FileUtils";

	private FileUtils()
	{

	}

	
	/**
	 * Retrieves FreeFlight media directory.
	 * May return null.
	 * @param context
	 * @return Media directory to store the media files or null if sd card is not mounted.
	 */
	public static File getMediaFolder(Context context)
	{
	    File dcimFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
	
	    if (dcimFolder == null) {
            Log.w(TAG, "Looks like sd card is not available.");
            return null;
        }
	    
	    File mediaFolder = new File(dcimFolder, MEDIA_PUBLIC_FOLDER_NAME);
		
		if(!mediaFolder.exists())
		{
			mediaFolder.mkdirs();
			Log.d(TAG, "Root media folder created " + mediaFolder);
		}
	
		return mediaFolder;
	}
	
	
	public static boolean isExtStorgAvailable()
	{
	    boolean result = false;
	    
	    if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED))
	    {
	        result = true;
	    }
	    
	    return result;
	}
	
	
	public static File getMediaThumbFolder(Context context)
	{
		File mediaThumbFolder = new File(getMediaFolder(context), THUMBNAILS_FOLDER);

		if (mediaThumbFolder != null) {

    		if (!mediaThumbFolder.exists()) {
    			mediaThumbFolder.mkdirs();
    			Log.d(TAG, "Thumbnails folder created " + mediaThumbFolder);
    		} 
    
    		createNoMediaFile(mediaThumbFolder);
		}
		return mediaThumbFolder;
	}

	
	private static void createNoMediaFile(File file)
	{
		try {
			File noMediaFile = new File(file, NO_MEDIA_FILE);

			if (!noMediaFile.exists()) {
				noMediaFile.createNewFile();
			}
		} catch (IOException e) {
			Log.w(TAG, e.toString());
		}
	}

	
	public static void sortFileByDate(List<File> allFileList )
	{
		File[] files = new File[allFileList.size()];
		
		for(int i=0; i<allFileList.size(); i++)
		{
			files[i] = allFileList.get(i);
		}
		
		Arrays.sort(files, new Comparator<File>()
		{
			public int compare(File f1, File f2)
			{
				return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			}
		});
		
		allFileList.clear();
		
		allFileList.addAll(Arrays.asList(files));
	}

	
	public static File getMediaCopyFolder(Context context)
	{
		File mediaCopyFolder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), MEDIA_PUBLIC_FOLDER_NAME);

		if (mediaCopyFolder.exists())
		{
			// Log.d(TAG, "Media folder exist: " + mediaCopyFolder);
		} else
		{
			mediaCopyFolder.mkdirs();

			// Log.d(TAG, "Media folder created: " + mediaCopyFolder);
		}

		return mediaCopyFolder;
	}

	public static void copyFileToDir(File sourceFile, File destFile)
	{
		InputStream inStream = null;
		OutputStream outStream = null;

		try
		{

			inStream = new FileInputStream(sourceFile);
			outStream = new FileOutputStream(destFile);

			byte[] buffer = new byte[1024];

			int length;

			// copy the file content in bytes
			while ((length = inStream.read(buffer)) > 0)
			{

				outStream.write(buffer, 0, length);

			}

			Log.d(TAG, "File copied: " + destFile);

		} catch (IOException e)
		{
			Log.w(TAG, e.toString());
		} finally
		{
			if (inStream != null)
			{
				try
				{
					inStream.close();
				} catch (IOException e)
				{
					Log.d(TAG, e.toString());
				}
			}

			if (outStream != null)
			{
				try
				{
					outStream.close();
				} catch (IOException e)
				{
					Log.d(TAG, e.toString());
				}
			}
		}
	}
	
	public static void deleteFile(String file)
    {
        deleteFile(new File(file));
    }

	public static void deleteFile(File file)
	{
		if (file.exists())
		{
			file.delete();

			Log.d(TAG, "File deleted: " + file);
		}
	}
	
	public static boolean isVideo(String file)
    {
	    if (file.endsWith("mp4"))
            return true;

        return false;
    }

	public static boolean isVideo(File file)
	{
		return isVideo(file.getName());
	}

	public static File convertFormat(File file, String newFormat)
	{
		StringBuilder tmpValue = new StringBuilder(file.getAbsolutePath());
		int index = tmpValue.lastIndexOf(".") + 1;
		tmpValue.delete(index, tmpValue.length());
		tmpValue.append(newFormat);
		File result = new File(tmpValue.toString());
	
		return result;
	}

	public static String getFileExt(String fileName)
	{
		int index = fileName.lastIndexOf(".") + 1;
		String ext = fileName.substring(index, fileName.length());
		return ext;
	}

	public static void getFileList(final List<File> fileList, final File root, final File[] ignoreList)
	{
		final File[] list = root.listFiles();

		if (list == null)
		{
			return;
		}

		for (final File f : list)
		{
			if (f.isDirectory() && !isIgnored(ignoreList, f))
			{
				getFileList(fileList, f, ignoreList);
			} else
			{
				String filename = getFileExt(f.getName());

				if (filename.equalsIgnoreCase("jpg") || filename.equalsIgnoreCase("png") || filename.equalsIgnoreCase("mp4"))
				{
					fileList.add(f);
				}
			}
		}
	}
	
	
	public static void getFileList(final List<File> fileList, final File root, FileFilter filter)
	{
		final File[] list = root.listFiles(filter);

		if (list == null) {
			return;
		}

		for (final File f : list) {
			if (f.isDirectory()) {
			    fileList.add(f);
				getFileList(fileList, f, filter);
			} else {
				fileList.add(f);
			}
		}
	}
	
	
	public static String getNextFile(final File root, final String extension)
	{
		final File[] list = root.listFiles(new FileFilterImpl(extension));

		if (list == null) {
			return null;
		}

		for (final File f : list) {
			if (f.isDirectory()) {
				String path = getNextFile(f, extension);
				if (path != null)
					return path;
				
			} else {
				String path = f.getAbsolutePath();
				if (path != null) {
					return path;
				}
			}
		}
		
		return null;
	}
	

	private static boolean isIgnored(File[] ignoreList, File file)
	{
		boolean result = false;

		for (File item : ignoreList)
		{
			if (item.getName().equalsIgnoreCase(file.getName()))
			{
				result = true;

				// Log.d(TAG, "Skipping folder: " + file);
			}
		}

		return result;
	}
	
	
	private static class FileFilterImpl implements FileFilter
	{
		private String ext;
		
		public FileFilterImpl(String extension)
		{
			this.ext = extension;
		}
		
		public boolean accept(File pathname) {
			
			return pathname.isDirectory() || pathname.getName().endsWith(ext);
		}
	}
}
