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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.R;
import at.tugraz.ist.catroid.content.entities.SoundInfo;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.utils.Utils;

import com.thoughtworks.xstream.XStream;

/**
 * @author Peter Treitler
 * 
 */
public class StorageHandler {

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

        try {
            String spfFile = xstream.toXML(project);

            File projectDirectory = new File(catroidRoot.getAbsolutePath() + "/" + project.getName());
            if (!(projectDirectory.exists() && projectDirectory.isDirectory() && projectDirectory.canWrite())) {
                projectDirectory.mkdir();
                File imageDirectory = new File(projectDirectory.getAbsolutePath() + "/images");
                imageDirectory.mkdir();
                File soundDirectory = new File(projectDirectory.getAbsolutePath() + "/sounds");
                soundDirectory.mkdir();
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(projectDirectory.getAbsolutePath() + "/" + project.getName()
                    + Consts.PROJECT_EXTENTION));
            out.write(spfFile);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        soundContent = new ArrayList<SoundInfo>();
        String[] projectionOnOrig = { MediaStore.Audio.Media.DATA, MediaStore.Audio.AudioColumns.TITLE, MediaStore.Audio.Media._ID };

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionOnOrig, null, null, null);

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
        return soundContent;
    }

    public void setSoundContent(ArrayList<SoundInfo> soundContent) {
        System.out.println("SOUND SET");
        this.soundContent.clear();
        this.soundContent.addAll(soundContent);
    }

    /**
     * Creates the default project and saves it to the filesystem
     * 
     * @return the default project object if successful, else null
     */
    public Project createDefaultProject(Context context) {
        try {
            Project defaultProject = new Project(context, context.getString(R.string.default_project_name));
            saveProject(defaultProject);
            return defaultProject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public File copyImage(String currentProjectName, String inputFilePath) throws IOException {
        File imageDirectory = new File(catroidRoot.getAbsolutePath() + "/" + currentProjectName + Consts.IMAGE_DIRECTORY);
        if (!imageDirectory.exists()) {
            imageDirectory.mkdirs();
            File noMediaFile = new File(imageDirectory.getAbsolutePath() + "/.nomedia");
            noMediaFile.createNewFile();
        }

        File inputFile = new File(inputFilePath);
        if (!inputFile.exists() || !inputFile.canRead())
            return null;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = simpleDateFormat.format(new Date());
        File outputFile = new File(imageDirectory.getAbsolutePath() + "/" + timestamp + inputFile.getName());

        if(copyFile(outputFile, inputFile))
            return outputFile;
        else
            return null;
    }
    
    public boolean copyFile(File destinationFile, File sourceFile) throws IOException {
        FileChannel inputChannel = new FileInputStream(sourceFile).getChannel();
        FileChannel outputChannel = new FileOutputStream(destinationFile).getChannel();
        try {
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inputChannel != null)
                inputChannel.close();
            if (outputChannel != null)
                outputChannel.close();
        }
    }
}
