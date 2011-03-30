//package at.tugraz.ist.catroid.test.content;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import android.test.InstrumentationTestCase;
//import at.tugraz.ist.catroid.Consts;
//import at.tugraz.ist.catroid.FileChecksumContainer;
//import at.tugraz.ist.catroid.constructionSite.content.ProjectManager;
//import at.tugraz.ist.catroid.content.project.Project;
//import at.tugraz.ist.catroid.io.StorageHandler;
//import at.tugraz.ist.catroid.test.R;
//
//public class FileChecksumContainerTest extends InstrumentationTestCase{
//
//	private static final int IMAGE_FILE_ID = R.raw.icon;
//	private StorageHandler storageHandler;
//	private File testImage;
//	private File testSound;
//	private String currentProjectName = "testCopyFile";
//	private final int fileSizeImage = 4000;
//	private final int fileSizeSound = 4000;
//
//    @Override
//    protected void setUp() throws Exception {
//    	storageHandler = StorageHandler.getInstance();
//    	Project testCopyFile = new Project(null, currentProjectName);
//    	ProjectManager.getInstance().setProject(testCopyFile);
//        storageHandler.saveProject(testCopyFile);
//
//        final String imagePath = "/mnt/sdcard/catroid/testImage.png"; 
//        testImage = new File(imagePath);
//        if(!testImage.exists()) {
//            testImage.createNewFile();
//        }
//        InputStream in   = getInstrumentation().getContext().getResources().openRawResource(IMAGE_FILE_ID);
//        OutputStream out = new BufferedOutputStream(new FileOutputStream(testImage), fileSizeImage);
//        
//        byte[] buffer = new byte[fileSizeImage];
//        int length = 0;
//        while ((length = in.read(buffer)) > 0) {
//            out.write(buffer, 0, length);
//        }
//         
//
//        in.close();
//        out.flush();
//        out.close();
//               
//        final String soundPath = "/mnt/sdcard/catroid/testsound"; 
//        testSound = new File(soundPath);
//        if(!testSound.exists()) {
//            testSound.createNewFile();
//        }
//        in   = getInstrumentation().getContext().getResources().openRawResource(R.raw.testsound);
//        out  = new BufferedOutputStream(new FileOutputStream(testSound), fileSizeSound);
//        buffer = new byte[fileSizeSound];
//        length = 0;
//        while ((length = in.read(buffer)) > 0) {
//            out.write(buffer, 0, length);
//        }
//        
//        in.close();
//        out.flush();
//        out.close();
//        
//    }
//    
//    @Override
//    protected void tearDown() throws Exception {
///*        if(testImage != null && testImage.exists()){
//            testImage.delete();
//        }
//        if(testSound != null && testSound.exists()){
//            testSound.delete();
//        }*/        
//    }
//	
//    public void testCopyFile() throws IOException, InterruptedException {
//        storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
//        String checksum = storageHandler.getChecksum(testImage);
//
//        FileChecksumContainer container = ProjectManager.getInstance().getCurrentProject().getFileChecksumContainer();
//        assertTrue("Checksum isn't in container", container.findChecksum(checksum));
//		storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
//		File imageDirectory = new File(Consts.DEFAULT_ROOT + "/"+ currentProjectName + Consts.IMAGE_DIRECTORY);
//		File[] filesImage = imageDirectory.listFiles();
//		System.out.println("++++++++++++++++++################### number of files: "+filesImage.length);
//		assertEquals("Wrong amount of files in folder", 2, filesImage.length);
//
//		storageHandler.copySoundFile(testSound.getAbsolutePath());
//		String checksumSound = storageHandler.getChecksum(testSound);
//		assertTrue("Checksum isn't in container",
//				container.findChecksum(checksumSound));
//		File soundDirectory = new File(Consts.DEFAULT_ROOT + "/"
//				+ currentProjectName + Consts.SOUND_DIRECTORY);
//		File[] filesSound = soundDirectory.listFiles();
//		assertEquals("Wrong amount of files in folder", 2, filesSound.length);
//		
//    }  
//    
// /*   public void testDeleteChecksum() throws IOException{      
//        File imageDirectory = new File(Consts.DEFAULT_ROOT + "/" + currentProjectName + Consts.IMAGE_DIRECTORY);
//        File[] files = imageDirectory.listFiles();
//		System.out.println("++++++++++++++++++ number of files: "+files.length);
//		
//        storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
//        
//        String checksum = storageHandler.getChecksum(testImage);
//        FileChecksumContainer container = ProjectManager.getInstance().getCurrentProject().getFileChecksumContainer();
//  
//        assertTrue("Checksum isn't in container",container.findChecksum(checksum));
//        storageHandler.copyImage(currentProjectName, testImage.getAbsolutePath());
//
//        assertEquals("Wrong amount of files in folder",2,files.length);
//
//        container.deleteChecksum(checksum);
//        assertTrue("Checksum was deleted",container.findChecksum(checksum));
//        container.deleteChecksum(checksum);
//        assertFalse("Checksum wasn't deleted",container.findChecksum(checksum));	
//    }*/
//}
