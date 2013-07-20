package st.rattmuffen.jsub.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.GZIPInputStream;

/**
 * Functions regarding file handling 'n such...
 * @author rattmuffen
 * @version 0.1
 */
public class FileUtils {

	/**
	 * Downloads contents of an URL to a given file.
	 * @param url URL of resource to download.
	 * @param file File to download to.
	 * @throws IOException IOException
	 */
	public static void download(URL url, File file) throws IOException {
		  BufferedInputStream in = new BufferedInputStream(url.openStream());
		  FileOutputStream fos = new FileOutputStream(file);
		  BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
		  
		  byte[] data = new byte[1024];
		  int x=0;
		  while((x=in.read(data,0,1024))>=0) {
			  bout.write(data,0,x);
		  }
		  
		  bout.close();
		  in.close();
	}
	
	/**
	 * Gets dir of file.
	 * @param f File to get dir of.
	 * @return Dir of file.
	 */
	public static String getDir(File f) {
		return f.getAbsoluteFile().getParent() + "\\";
	}
	
	/**
	 * Gets filename without extension.
	 * Simply removes everything after the last point in the filename (if there is one).
	 * @param f File to get name from.
	 * @return String of the filename without the file extension.
	 */
	public static String getNameWithoutExt(File f) {
        int lastPointPos = f.getName().lastIndexOf(".");

        if (lastPointPos == -1) return f.getName();
        return f.getName().substring(0, lastPointPos);
	}
	
	/**
	 * Uncompresses a .gz-file to a target file.
	 * @param inFile Zip-file to be uncompressed.
	 * @param targetFile File to uncompress to.
	 * @throws IOException IOException duh!
	 */
	public static void uncompress(File inFile, File targetFile) throws IOException{
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(inFile));
		OutputStream out = new FileOutputStream(targetFile);
	    
		byte[] buf = new byte[1024];
		int len;
		
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		in.close();
		out.close();
	}

	/**
	 * Oh how delightfully trivial!
	 * @param f file to get extension from...
	 * @return file extension!
	 */
	public static String getFileExtension(File f) {
        int lastPointPos = f.getName().lastIndexOf(".");

        if (lastPointPos == -1)
        	return "";
        
        return f.getName().substring(lastPointPos+1,f.getName().length()).toLowerCase();
	}
}
