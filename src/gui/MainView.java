package gui;

import gui.misc.FileTransferHandler;
import gui.misc.MovieFileFilter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLEditorKit;


import org.apache.xmlrpc.XmlRpcException;

import util.Utils;
import client.OpenSubtitleClient;

@SuppressWarnings("serial")
public class MainView extends JPanel implements ItemListener, ActionListener{


	static final String DEFAULT_MESSAGE_TEXT = "Drop movie files here!";
	
	FileTransferHandler transferHandler;
	
	JPanel optionsPanel,languageSelectionPanel;
	JScrollPane scrollPane;
	JEditorPane messageArea;
	JCheckBox exitBox, openBox;
	JComboBox languageComboBox;
	JButton browseButton,credentialsButton;

	JFileChooser filec;

	String[] acceptedFileExtensions = {"avi","wmv","mkv","iso","mp4"};

	boolean exitEnabled = false, openEnabled = false;
	private MenuItem clearMenuItem;
	private PopupMenu popupMenu;
	
	
	OpenSubtitleClient client;
	JSubFrame controller;
	
	
	public MainView(OpenSubtitleClient client, JSubFrame controller) {
		super();
		
		this.client = client;
		this.controller = controller;
		
		this.setLayout(new BorderLayout());
		transferHandler = new FileTransferHandler(this);

		filec = new JFileChooser("C:\\");
		filec.setToolTipText("Select movie file to get subtitles for");
		filec.setMultiSelectionEnabled(true);
		filec.setApproveButtonText("Download subs");
		filec.setApproveButtonToolTipText("You know...");
		filec.setDragEnabled(true);
		filec.setName("File selection (because you're too stupid for drag and drop)");
		filec.setFileHidingEnabled(false);
		filec.setFileSelectionMode(JFileChooser.FILES_ONLY);
		
		MovieFileFilter filter = new MovieFileFilter();
		filec.setFileFilter(filter);

		optionsPanel = new JPanel(new BorderLayout());
		exitBox = new JCheckBox("Exit jSub when completed successfully: ");
		exitBox.setHorizontalTextPosition(SwingConstants.LEFT);
		exitBox.setSelected(false);
		exitBox.addItemListener(this);
		
		openBox = new JCheckBox("Open file with default media program: ");
		openBox.setHorizontalTextPosition(SwingConstants.LEFT);
		openBox.setSelected(false);
		openBox.addItemListener(this);
 
		browseButton = new JButton("Browse file");
		browseButton.addActionListener(this);
		
		credentialsButton = new JButton("Log in");
		credentialsButton.addActionListener(this);
		
		languageSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		languageSelectionPanel.add(new JLabel("Subtitle language:"));
		
		languageComboBox = new JComboBox(new String[] {"eng","sve"});
		languageComboBox.addItemListener(this);
		languageSelectionPanel.add(languageComboBox);
		languageSelectionPanel.add(browseButton,FlowLayout.RIGHT);
		languageSelectionPanel.add(credentialsButton,FlowLayout.RIGHT);

		optionsPanel.add(exitBox, BorderLayout.PAGE_START);
		optionsPanel.add(openBox,  BorderLayout.CENTER);
		optionsPanel.add(languageSelectionPanel,  BorderLayout.PAGE_END);
	//	optionsPanel.add(browseButton,BorderLayout.AFTER_LINE_ENDS);
		
		popupMenu = new PopupMenu();
		clearMenuItem = new MenuItem("Clear text");
		clearMenuItem.addActionListener(this);
		popupMenu.add(clearMenuItem);
		
		messageArea = new JEditorPane();
		messageArea.setContentType("text/html");
		messageArea.setText(DEFAULT_MESSAGE_TEXT);
		messageArea.setEditable(false);
		messageArea.setSelectedTextColor(Color.black);
		messageArea.setSelectionColor(Color.white);
		messageArea.setTransferHandler(transferHandler);
		messageArea.setEnabled(true);
		messageArea.add(popupMenu);
		
		messageArea.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == 3) {
					popupMenu.show(messageArea, e.getX(), e.getY());
				}
			}

		    public void mousePressed(MouseEvent e) {}
		    public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		messageArea.addHyperlinkListener(new HyperlinkListener() {
			
			@Override
			public void hyperlinkUpdate(HyperlinkEvent hle) {
				if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {  
					Utils.openURL(hle.getURL());
				} 
			}
		});
		
		scrollPane = new JScrollPane(messageArea);
		scrollPane.setAutoscrolls(true);
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		this.add(scrollPane, BorderLayout.CENTER);
		this.add(optionsPanel, BorderLayout.PAGE_END);
	}
	
	
	public void importFiles(List<File> files) {
		//messageArea.setText("");
		boolean success = false;
		
		try {
			if (client.isLoggedIn() && messageArea.isEnabled()) {
				
				for (File file : files) {
					addTextToMessageArea("-----------------",true);
					addTextToMessageArea("File: " + file.getName(),true);
					

					if (Utils.checkExtension(file, acceptedFileExtensions)) 
						success = client.downloadSub(file.getCanonicalPath(),(String) languageComboBox.getSelectedItem(), this);
					else
						addTextToMessageArea("File type not supported.", false);
				}
				addTextToMessageArea("-----------------",true);
				
				if (success) {
					if (openEnabled) {
						for (File f : files) {
							Desktop desktop = Desktop.getDesktop();
							desktop.open(f);
						}
					}
					
					if (exitEnabled) {
						addTextToMessageArea("Good bye!",true); //fullständigt meningslöst p_q
						System.exit(0);
					}
				}
			} else {
				addTextToMessageArea("Could not log in to OpenSubtitles server... Try again?",true);
			}
		} catch (XmlRpcException e) {
			addTextToMessageArea("Could not communicate with OpenSubtitles server... Try again?",true);
			e.printStackTrace();
		} catch (IOException e) {
			addTextToMessageArea("IOException, check file/folder read/write permissions and try again.",true);
			e.printStackTrace();
		}
	}
	
	public void addTextToMessageArea(String text, boolean newline) {
		//if (newline)
		//	messageArea.setText(messageArea.getText().concat(text + "\n"));
		//System.out.println("Adding " +  text + " to messageArea");
		
		Document doc = (Document) messageArea.getDocument();
		Element e = doc.getDefaultRootElement();
		
		HTMLEditorKit editor = (HTMLEditorKit) messageArea.getEditorKit();
		StringReader reader = new StringReader( "\n<p>" + text + "</p>");

		try {
		  editor.read(reader, messageArea.getDocument(), messageArea.getDocument().getLength());
		} catch(BadLocationException ex) {
		   //This happens if your offset is out of bounds.
		} catch (IOException ex) {
		  // I/O error
		}
		//scrollPane.
		
		messageArea.update(messageArea.getGraphics());
		messageArea.setCaretPosition(messageArea.getDocument().getLength());
	}
	
	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getItemSelectable();

	    if (source == exitBox) {
		    if (event.getStateChange() == ItemEvent.DESELECTED)
		    	exitEnabled = false;
		    else
		    	exitEnabled = true;
	    } else if (source == openBox) {
		    if (event.getStateChange() == ItemEvent.DESELECTED)
		    	openEnabled = false;
		    else
		    	openEnabled = true;
	    } else if (source == languageComboBox) {
	    	messageArea.setText(DEFAULT_MESSAGE_TEXT);
	    }
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();

		if (source == browseButton) {			
			int returnVal = filec.showOpenDialog(this);

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = filec.getSelectedFile();
	            ArrayList<File> list = new ArrayList<File>();
	            list.add(file);
	            
	            importFiles(list);
	        } else {
	        	//
	        }
		} else if (source == credentialsButton) {
			controller.setContentPane(new LoginView(controller));
			controller.pack();
		} else if (source == clearMenuItem) {
		
		}
	}
}