package gui.misc;

import java.io.File;

import javax.swing.filechooser.FileFilter;


public class MovieFileFilter extends FileFilter {

	String[] accepted = new String[] {"mkv","avi","wmv"};
	
	
	@Override
	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		} 
		
		for (int i = 0; i < accepted.length; i++) {
			if (file.getAbsolutePath().endsWith(accepted[i])) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public String getDescription() {
		String s = "Movie files: ";
		
		for (int i = 0; i < accepted.length; i++) {
			s += accepted[i] + ", ";
		}
		
		return s.substring(0, s.length()-2);
	}

}
