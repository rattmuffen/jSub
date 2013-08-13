package st.rattmuffen.jsub.gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.apache.xmlrpc.XmlRpcException;

import st.rattmuffen.jsub.client.OpenSubtitleClient;
import st.rattmuffen.jsub.client.QueryResult;
import st.rattmuffen.jsub.gui.misc.FileTransferHandler;
import st.rattmuffen.jsub.gui.misc.MovieFileFilter;
import st.rattmuffen.jsub.util.FileUtils;
import st.rattmuffen.jsub.util.PropertiesHandler;
import st.rattmuffen.jsub.util.Utils;

@SuppressWarnings("serial")
/**
 * Panel containing options stuff and dnd-area.
 * @version 0.3
 * @author rattmuffen
 */
public class SubPanel extends JPanel implements ItemListener, ActionListener {


	static final String DEFAULT_MESSAGE_TEXT = "Drop movie files here!";

	FileTransferHandler transferHandler;

	JPanel optionsPanel,languageSelectionPanel;
	JScrollPane scrollPane;

	OptionCheckBox dlBox, exitBox, openBox, renameBox;
	JComboBox<String> languageComboBox;
	JButton browseButton,credentialsButton;

	JList<QueryResult> resultList;
	DefaultListModel<QueryResult> resultListModel;
	ResultListCellPanel rlcp;

	JFileChooser filec;

	String[] acceptedFileExtensions = {"avi","wmv","mkv","iso","mp4"};

	boolean exitEnabled = false, openEnabled = false, dlFirst = false, renameAfter = false;

	OpenSubtitleClient client;
	JSubFrame controller;

	public SubPanel(OpenSubtitleClient client, JSubFrame controller) {
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

		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(new BorderLayout());

		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS)); 
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options"));

		dlBox = new OptionCheckBox("Auto-download first hit:");
		dlBox.checkBox.addItemListener(this);

		exitBox = new OptionCheckBox("Exit jSub when completed:");
		exitBox.checkBox.addItemListener(this);

		openBox = new OptionCheckBox("Open movie when completed:");
		openBox.checkBox.addItemListener(this);
		
		renameBox = new OptionCheckBox("Rename movie file to title.ext:");
		renameBox.checkBox.addItemListener(this);

		languageSelectionPanel = new JPanel(new FlowLayout());
		languageSelectionPanel.add(new JLabel("Language:"));

		languageComboBox = new JComboBox<String>(Utils.acceptedLangs);
		languageComboBox.addItemListener(this);
		languageSelectionPanel.add(languageComboBox);
		

		// Read properties file.
		try {
			dlBox.checkBox.setSelected(PropertiesHandler.readProperty(PropertiesHandler.DOWNLOAD_FIRST).equals("true"));
			exitBox.checkBox.setSelected(PropertiesHandler.readProperty(PropertiesHandler.EXIT_AFTER).equals("true"));
			openBox.checkBox.setSelected(PropertiesHandler.readProperty(PropertiesHandler.OPEN_AFTER).equals("true"));
			renameBox.checkBox.setSelected(PropertiesHandler.readProperty(PropertiesHandler.RENAME_AFTER).equals("true"));
			
			languageComboBox.setSelectedIndex(Integer.parseInt(PropertiesHandler.readProperty(PropertiesHandler.LANGUAGE)));
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(this, "Could not find properties file!\n" + e.getMessage(),
					"jSub - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error when accessing properties file!\n" + e.getMessage(),
					"jSub - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Illegal property value present!",
					"jSub - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}


		optionsPanel.add(languageSelectionPanel);
		optionsPanel.add(dlBox);
		optionsPanel.add(exitBox);
		optionsPanel.add(openBox);
		optionsPanel.add(renameBox);

		browseButton = new JButton("Browse file");
		browseButton.addActionListener(this);

		credentialsButton = new JButton("Log out");
		credentialsButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		buttonPanel.add(browseButton);
		buttonPanel.add(credentialsButton);

		sidePanel.add(optionsPanel,BorderLayout.PAGE_START);
		sidePanel.add(buttonPanel,BorderLayout.PAGE_END);

		resultList = new JList<QueryResult>();

		resultList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		resultList.setLayoutOrientation(JList.VERTICAL);
		resultList.setVisibleRowCount(-1);

		resultList.setTransferHandler(transferHandler);
		resultList.setBorder(BorderFactory.createEmptyBorder());

		rlcp = new ResultListCellPanel(this);
		resultList.setCellRenderer(rlcp);
		resultList.setOpaque(false);

		resultList.addMouseListener(rlcp);

		resultListModel = new DefaultListModel<QueryResult>();
		resultList.setModel(resultListModel);

		scrollPane = new JScrollPane(resultList);
		scrollPane.setAutoscrolls(true);
		scrollPane.setWheelScrollingEnabled(true);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBorder(BorderFactory.createTitledBorder("Drop file(s) here..."));
		scrollPane.setPreferredSize(new Dimension(400,300));

		this.add(sidePanel, BorderLayout.LINE_START);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	public void importFiles(List<File> files) {
		QueryResult result = null;
		
		DefaultListModel<QueryResult> listModel = (DefaultListModel<QueryResult>) resultList.getModel();
        listModel.removeAllElements();

		if (client.isLoggedIn()) {
			System.out.println("Files length: " + files.size());
			for (File file : files) {

				if (Utils.checkExtension(file, acceptedFileExtensions)) {

					try {
						result = client.performSearch(file.getCanonicalPath(),
								Utils.getLangCode((String) languageComboBox.getSelectedItem()),
								dlFirst,this);

						if (result.get("data") instanceof Object[]) {
							Object[] resultArray = (Object[]) result.get("data");

							if (resultArray.length>0) {
								result.type = QueryResult.Result_Type.RESULT_MESSAGE;
								result.sourceFile = file;
							} else {
								result = new QueryResult(null);
								result.type = QueryResult.Result_Type.ERROR_MESSAGE;
								result.message = "Found no matches for that release.";
							}
						} else {
							result = new QueryResult(null);
							result.type = QueryResult.Result_Type.ERROR_MESSAGE;
							result.message = "Found no matches for that release.";
						}
					} catch (IOException e) {
						e.printStackTrace();

						result = new QueryResult(null);
						result.type = QueryResult.Result_Type.ERROR_MESSAGE;
						result.message = "Could not read file!";
					} catch (XmlRpcException e) {
						e.printStackTrace();

						result = new QueryResult(null);
						result.type = QueryResult.Result_Type.ERROR_MESSAGE;
						result.message = "Could not communicate with OpenSubtitles server!";
					}
				} else {
					result = new QueryResult(null);
					result.type = QueryResult.Result_Type.ERROR_MESSAGE;
					result.message = "Not a valid file!";
				}
			}
		} else {
			result = new QueryResult(null);
			result.type = QueryResult.Result_Type.ERROR_MESSAGE;
			result.message = "Not logged in!";
		}

        listModel.addElement(result);
	}

	public void downloadAndExtractSubArchive(File movie, String dlURL, String movieTitle) {
		File gzFile = new File(FileUtils.getDir(movie) +  new File(dlURL).getName());

		try {
			if (renameAfter && movieTitle != null) {
				File newNameFile = new File(FileUtils.getDir(movie) + movieTitle + "." + FileUtils.getFileExtension(movie));
								
				boolean renameSuccess = movie.renameTo(newNameFile);
				
				
				if (!renameSuccess) {
					JOptionPane.showMessageDialog(this, "Could not rename the file!", "jSub - Error", JOptionPane.ERROR_MESSAGE);
				}
			}
			
			FileUtils.download(new URL(dlURL), gzFile);
			File outFile = new File(FileUtils.getDir(movie) + FileUtils.getNameWithoutExt(movie) + ".srt");
			FileUtils.uncompress(gzFile, outFile);

			gzFile.delete();

			if (openEnabled) {
				Desktop desktop = Desktop.getDesktop();
				desktop.open(movie);
			}

			if (exitEnabled) {
				System.exit(0);
			}
		} catch (MalformedURLException e) {
			JOptionPane.showMessageDialog(this, "Error when downloading file!", "jSub - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error when downloading file!", "jSub - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent event) {
		Object source = event.getItemSelectable();

		try {
			if (source == exitBox.checkBox) {
				if (event.getStateChange() == ItemEvent.DESELECTED)
					exitEnabled = false;
				else
					exitEnabled = true;

				PropertiesHandler.writeProperty(PropertiesHandler.EXIT_AFTER, String.valueOf(exitEnabled));
			} else if (source == openBox.checkBox) {
				if (event.getStateChange() == ItemEvent.DESELECTED)
					openEnabled = false;
				else
					openEnabled = true;

				PropertiesHandler.writeProperty(PropertiesHandler.OPEN_AFTER, String.valueOf(openEnabled));
			} else if (source == dlBox.checkBox) {
				if (event.getStateChange() == ItemEvent.DESELECTED)
					dlFirst = false;
				else
					dlFirst = true;

				PropertiesHandler.writeProperty(PropertiesHandler.DOWNLOAD_FIRST, String.valueOf(dlFirst));
			} else if (source == renameBox.checkBox) {
					if (event.getStateChange() == ItemEvent.DESELECTED)
						renameAfter = false;
					else
						renameAfter = true;

					PropertiesHandler.writeProperty(PropertiesHandler.RENAME_AFTER, String.valueOf(renameAfter));
			} else if (source == languageComboBox) {
				int lang = languageComboBox.getSelectedIndex();

				PropertiesHandler.writeProperty(PropertiesHandler.LANGUAGE, String.valueOf(lang));
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error when accessing properties file!\n" + e.getMessage(),
					"jSub - Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
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
			} 
		} else if (source == credentialsButton) {
			controller.setContentPane(new LoginPanel(controller));
			controller.pack();
		}
	}
}