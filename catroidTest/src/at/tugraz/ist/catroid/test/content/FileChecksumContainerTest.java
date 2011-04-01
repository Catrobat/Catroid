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

package at.tugraz.ist.catroid.test.content;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.test.InstrumentationTestCase;
import at.tugraz.ist.catroid.Consts;
import at.tugraz.ist.catroid.FileChecksumContainer;
import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
import at.tugraz.ist.catroid.content.project.Project;
import at.tugraz.ist.catroid.io.StorageHandler;
import at.tugraz.ist.catroid.test.R;
import at.tugraz.ist.catroid.utils.UtilFile;

public class FileChecksumContainerTest extends InstrumentationTestCase{

	private static final int IMAGE_FILE_ID = R.raw.icon;
	private StorageHandler storageHandler;
	private ProjectManager projectManager;
	private File testImage;
	private File testSound;
	private String currentProjectName = "testCopyFile";
	private final int fileSizeImage = 4000;
	private final int fileSizeSound = 4000;

	
	public FileChecksumContainerTest() throws IOException {
		File directory = new File("/sdcard/catroid/" + currentProjectName);
        if (directory.exists()) {
            UtilFile.deleteDirectory(directory);
        }
      	storageHandler = StorageHandler.getInstance();
    	Project testCopyFile = new Project(null, currentProjectName);
    	projectManager = ProjectManager.getInstance();
    	projectManager.setProject(testCopyFile);
        storageHandler.saveProject(testCopyFile);
	}
	
    @Override
    protected void setUp() throws Exception{
        
        final String imagePath = "/sdcard/catroid/testImage.png"; 
        testImage = new File(imagePath);
        if(!testImage.exists()) {
            testImage.createNewFile();
        }
        InputStream in   = getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID);
        OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSizeImage);
        
        byte[] buffer = new byte[fileSizeImage];
        int length = 0;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
         

        in.close();
        out.flush();
        out.close();
               
        final String soundPath = "/sdcard/catroid/testsound"; 
        testSound = new File(soundPath);
        if(!testSound.exists()) {
            testSound.createNewFile();
        }
        in   = getInstrumentation().getContext().getResources().openRawResource(R.raw.testsound);
        out  = new BufferedOutputStream(new FileOutputStream(testSound), fileSizeSound);
        buffer = new byte[fileSizeSound];
        length = 0;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        
        in.close();
        out.flush();
        out.close();
    }
    
    @Override
    protected void tearDown() throws Exception {
    	if(testImage != null && testImage.exists()){
            testImage.delete();
        }
        if(testSound != null && testSound.exists()){
            testSound.delete();
        }   
    }
	
    public void testContainer() throws IOException, InterruptedException {
        
    	storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());

        String checksumImage = storageHandler.getChecksum(testImage);

        FileChecksumContainer container = projectManager.getCurrentProject().getFileChecksumContainer();
        assertTrue("Checksum isn't in container", container.findChecksum(checksumImage));
        
        //wait to get a different timestamp on next file
        Thread.sleep(2000);
        
		storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
		File imageDirectory = new File(Consts.DEFAULT_ROOT + "/"+ currentProjectName + Consts.IMAGE_DIRECTORY);
		File[] filesImage = imageDirectory.listFiles();
		assertEquals("Wrong amount of files in folder", 2, filesImage.length);

		storageHandler.copySoundFile(testSound.getAbsolutePath());
		String checksumSound = storageHandler.getChecksum(testSound);
		assertTrue("Checksum isn't in container", container.findChecksum(checksumSound));
		File soundDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName + Consts.SOUND_DIRECTORY);
		File[] filesSound = soundDirectory.listFiles();
		assertEquals("Wrong amount of files in folder", 2, filesSound.length);
		

        container.deleteChecksum(checksumImage);
        assertTrue("Checksum was deleted",container.findChecksum(checksumImage));
        container.deleteChecksum(checksumImage);
        assertFalse("Checksum wasn't deleted",container.findChecksum(checksumImage));	
		
    }  
}
