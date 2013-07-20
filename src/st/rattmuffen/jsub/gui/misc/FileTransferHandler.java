package st.rattmuffen.jsub.gui.misc;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import st.rattmuffen.jsub.gui.SubPanel;


/**
 * Simple class for handling drag and drop.
 * @version 0.2.1
 * @author rattmuffen
 */
public class FileTransferHandler extends TransferHandler {
	
	private static final long serialVersionUID = 1L;
	private List<File> importedFiles = null;
	private SubPanel parent;
	
	public FileTransferHandler(SubPanel gui) {
		super();
		
		parent = gui;
	}
	
	public boolean canImport(JComponent component, DataFlavor[] flavor) {
		return true;
	}

	//TODO UnsupportedFlavorException om Linux :/
	public boolean importData(JComponent comp, final Transferable t) {
		 try {
			 Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {

					 try {
						importedFiles = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
					} catch (UnsupportedFlavorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					 parent.importFiles(importedFiles);
				}
			});
			 
			 thread.run();
			 return true;
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 return false;
	}
}
