package at.tugraz.ist.s2a.test.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;
import at.tugraz.ist.s2a.utils.Utils;
import junit.framework.TestCase;

public class UtilsTest extends TestCase{
	
	
	private String testFileContent = "Hello, this is a Test-String";
	private File mTestFile;
	
	
	@Override
	protected void setUp() throws Exception {
		try {
			mTestFile = File.createTempFile("testCopyFiles", "txt");
			if(mTestFile.canWrite()){
				OutputStream stream = new FileOutputStream(mTestFile);
				stream.write(testFileContent.getBytes());
				stream.flush();
			}
		}
		catch (IOException e){
			e.printStackTrace();
		}
		super.setUp();
	}



	public void testCopyFile(){
		String newpath = mTestFile.getParent()+"/copiedFile.txt";
		Utils.copyFile(mTestFile.getAbsolutePath(), newpath);
		File newFile = new File(newpath);
		
		assertTrue(newFile.exists());
		
		FileReader fReader;
		String newContent = "";
		try {
			fReader = new FileReader(newFile);
			
			
			
			int read;
		   while((read = fReader.read())!= -1){
			   newContent = newContent + (char) read;
		   }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		assertEquals(testFileContent, newContent);
	}
	
	public void testDeleteFile(){
		Utils.deleteFile(mTestFile.getAbsolutePath());
		assertFalse(mTestFile.exists());
	}
	
	public void testConcatPath(){
		String first = "/abc/abc";
		String second = "/def/def/";
		String result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
		first = "/abc/abc";
		second = "def/def/";
		result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
		first = "/abc/abc/";
		second = "/def/def/";
		result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
		first = "/abc/abc/";
		second = "def/def/";
		result = "/abc/abc/def/def/";
		assertEquals(Utils.concatPaths(first, second), result);
	}
	
	public void testAddDefaultFileEnding(){
		String filename = "test";
		assertEquals(Utils.addDefaultFileEnding(filename), "test.spf");
	}

}
