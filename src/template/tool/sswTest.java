/**
 * you can put a one sentence description of your tool here.
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

 package template.tool;
 
 import processing.app.*;
 import processing.app.tools.*;
 
 import java.io.BufferedReader;
import java.io.IOException;
 import java.io.InputStream;
 import java.io.InputStreamReader;
 import java.net.URL;
 import java.net.URLEncoder;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.io.*;
import java.net.*;

import javax.swing.JOptionPane;
 
 
 public class sswTest implements Tool {
 
 // when creating a tool, the name of the main class which implements Tool
 // must be the same as the value defined for project.name in your build.properties
 
    final static int size=1024;
    static File tempFolder;
    Editor eddie;
 
	public String getMenuTitle() {
		return "Update UnderChain";
	}
 
	public void init(Editor theEditor) {
		eddie = theEditor;
	}
 
	public void run() {
		eddie.statusIndeterminate("updating Underchain");
		System.out.println("Check the Toolchain's version.\n##tool.name## version ##tool.version## by ##author##");
		int currentVersion = -1;
		String downloadURL = "https://raw.github.com/underverk/SmartWatch_Toolchain/master/version.txt";
		int onlineVersion = -1;
		
		//String tempPath = "/home/david/tmp/";
		tempFolder = getTempFolder();
		String tempPath = tempFolder.getAbsolutePath() + File.separator;
		//System.out.println(tempPath);
		
		//get the proper folder name
	    String armToolchain;
	    if (Base.isMacOS()) {
	      armToolchain = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
	      armToolchain = new File(armToolchain).getParent() + "/tools/underchain/"; 
	    } else {
	      armToolchain = "tools/underchain/"; 
	    }
	    
	    // get the version from local file
	    File versionFile = new File(armToolchain + "version.txt");
	    if (versionFile.exists()) {
		    try {
				BufferedReader br = new BufferedReader(new FileReader(armToolchain + "version.txt"));
			    String line = br.readLine();	
		        if (line != null) {
		        	currentVersion = Integer.parseInt(line);
		        }
		        br.close();			
		    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Error trying to get toolchain's version number from local file");
		    }
		}

	    // get the version from github
	    try {
			onlineVersion = readInt(downloadURL);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Error trying to get toolchain's version number from github");
		}

		System.out.println("Installed underchain version: " + currentVersion + " - Online version: " + onlineVersion);
		
		boolean updateToolchain = false;  // by default do NOT update anything

		if (currentVersion != onlineVersion && onlineVersion > currentVersion) {
		    String prompt =
		    	        "A new version of the SmartWatch Toolchain is available (" + onlineVersion + "),\n" +
		    	        "would you like to update your current version (" + currentVersion + ") automatically?";

			Object[] options = { "Yes", "No" };
			int result = JOptionPane.showOptionDialog(null,
	                                        prompt,
	                                        "Update",
	                                        JOptionPane.YES_NO_OPTION,
	                                        JOptionPane.QUESTION_MESSAGE,
	                                        null,
	                                        options,
	                                        options[0]);
			if (result == JOptionPane.YES_OPTION) updateToolchain = true;
		}
		
		
		
		// go for it mannen! update underchain
		if (updateToolchain) {
			eddie.statusIndeterminate("downloading Underchain");
			System.out.println("Underchain's online version is newer than the one you have installed\n" +
							   "I will try to download the latest for you");

			fileDownload("https://github.com/underverk/SmartWatch_Toolchain/archive/master.zip",tempPath);

			eddie.statusIndeterminate("uncompressing Underchain");
			unZipIt(tempPath + "master.zip",tempPath);
			
			System.out.println("Rename folder to: " + tempPath + "underchain");
			renameDirectory(tempPath + "SmartWatch_Toolchain-master", tempPath + "underchain");

		    // delete the toolchain
		    deleteFolder(new File(armToolchain));
		    
			// move things to the right place
		    System.out.println("Move it to folder: " + armToolchain);

			File source = new File(tempPath + "underchain");
			File destination = new File(armToolchain);
			
			eddie.statusIndeterminate("installing Underchain");
			try {
				copyFolder(source, destination);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Failed, can you check the permissions?");
				eddie.statusError("Error when updating Underchain");
				return;
			}
			System.out.println("Underchain updated to version: " + onlineVersion);
		}
		eddie.statusNotice("Done updating Underchain");
		eddie.statusUnprogress();
	}
 

	  protected int readInt(String filename) throws Exception {
	    URL url = new URL(filename);
	    InputStream stream = url.openStream();
	    InputStreamReader isr = new InputStreamReader(stream);
	    BufferedReader reader = new BufferedReader(isr);
	    return Integer.parseInt(reader.readLine());
	  }

	    public static void  fileUrl(String fAddress, String localFileName, String destinationDir) {
	    OutputStream outStream = null;
	    URLConnection  uCon = null;

	    InputStream is = null;
	    try {
	        URL Url;
	        byte[] buf;
	        int ByteRead,ByteWritten=0;
	        Url= new URL(fAddress);
	        outStream = new BufferedOutputStream(new
	        FileOutputStream(destinationDir+File.separator+localFileName));

	        uCon = Url.openConnection();
	        is = uCon.getInputStream();
	        buf = new byte[size];
	        while ((ByteRead = is.read(buf)) != -1) {
	            outStream.write(buf, 0, ByteRead);
	            ByteWritten += ByteRead;
	        }
	        System.out.println("Downloaded Successfully.");
	        System.out.println("File name:\""+localFileName+ "\"\nNo of bytes :" + ByteWritten);
	    }catch (Exception e) {
	        e.printStackTrace();
	        }
	    finally {
	            try {
	            is.close();
	            outStream.close();
	            }
	            catch (IOException e) {
	        e.printStackTrace();
	            }
	        }
	}

	public static void  fileDownload(String fAddress, String destinationDir)
	{    
	    int slashIndex =fAddress.lastIndexOf('/');
	    int periodIndex =fAddress.lastIndexOf('.');

	    String fileName=fAddress.substring(slashIndex + 1);

	    if (periodIndex >=1 &&  slashIndex >= 0 
	    && slashIndex < fAddress.length()-1)
	    {
	        fileUrl(fAddress,fileName,destinationDir);
	    }
	    else
	    {
	        System.err.println("path or file name.");
	    }
	}

	/**
     * Unzip it
     * @param zipFile input zip file
     * @param output zip file output folder
     */
    public void unZipIt(String zipFile, String outputFolder){
 
     byte[] buffer = new byte[1024];
 
     try{
 
    	//create output directory is not exists
    	File folder = new File(outputFolder);
    	if(!folder.exists()){
    		folder.mkdir();
    	}
 
    	//get the zip file content
    	ZipInputStream zis = 
    		new ZipInputStream(new FileInputStream(zipFile));
    	//get the zipped file list entry
    	ZipEntry ze = zis.getNextEntry();
 
    	while(ze!=null){
 
    	   String fileName = ze.getName();
           File newFile = new File(outputFolder + File.separator + fileName);
 
           //System.out.println("file unzip : "+ newFile.getAbsoluteFile());
           System.out.print(".");
           System.out.flush();
 
            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
           if (ze.isDirectory()) {
        	   String temp = newFile.getCanonicalPath();
        	   new File(temp).mkdir();
           } else {
        	   FileOutputStream fos = new FileOutputStream(newFile);
        	   int len; while ((len = zis.read(buffer)) > 0) {
        	   fos.write(buffer, 0, len); }
        	   fos.close();
           }
           ze = zis.getNextEntry();
    	}
 
        zis.closeEntry();
    	zis.close();
 
    	System.out.println(" Done");
 
    }catch(IOException ex){
       ex.printStackTrace(); 
    }
   }   
    
    public void renameDirectory(String fromDir, String toDir) {

        File from = new File(fromDir);

        if (!from.exists() || !from.isDirectory()) {

          System.out.println("Directory does not exist: " + fromDir);
          return;
        }

        File to = new File(toDir);

        //Rename
        if (from.renameTo(to))
          System.out.println("Success!");
        else
          System.out.println("Error");
      }
    
    public static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteFolder(f);
                } else {
                    f.delete();
                }
            }
        }
        folder.delete();
    }

    static public File getTempFolder() {
	    tempFolder = createTempFolder("toolchain");
	    tempFolder.deleteOnExit();
	    return tempFolder;
	  }

    /**
     * Get the path to the platform's temporary folder, by creating
     * a temporary temporary file and getting its parent folder.
     * <br/>
     * Modified for revision 0094 to actually make the folder randomized
     * to avoid conflicts in multi-user environments. (Bug 177)
     */
    static public File createTempFolder(String name) {
      try {
        File folder = File.createTempFile(name, null);
        //String tempPath = ignored.getParent();
        //return new File(tempPath);
        folder.delete();
        folder.mkdirs();
        return folder;

      } catch (Exception e) {
        e.printStackTrace();
      }
      return null;
    }

    public static void copyFolder(File src, File dest)
        	throws IOException{
     
        	if(src.isDirectory()){
     
        		//if directory not exists, create it
        		if(!dest.exists()){
        		   dest.mkdir();
        		   //System.out.println("Directory copied from " 
                   //               + src + "  to " + dest);
        		}
     
        		//list all the directory contents
        		String files[] = src.list();
     
        		for (String file : files) {
        		   //construct the src and dest file structure
        		   File srcFile = new File(src, file);
        		   File destFile = new File(dest, file);
        		   //recursive copy
        		   copyFolder(srcFile,destFile);
        		}
     
        	}else{
        		//if file, then copy it
        		//Use bytes stream to support all file types
        		InputStream in = new FileInputStream(src);
        	        OutputStream out = new FileOutputStream(dest); 
     
        	        byte[] buffer = new byte[1024];
     
        	        int length;
        	        //copy the file content in bytes 
        	        while ((length = in.read(buffer)) > 0){
        	    	   out.write(buffer, 0, length);
        	        }
     
        	        in.close();
        	        out.close();
        	        //System.out.println("File copied from " + src + " to " + dest);
        	}
        }

    public static void copyFile(String src, String dest)
        	throws IOException{
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dest);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
           out.write(buf, 0, len);
        }
        in.close();
        out.close(); 
    } 
 }

 

