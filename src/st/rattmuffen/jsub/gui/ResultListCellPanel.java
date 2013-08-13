package st.rattmuffen.jsub.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import st.rattmuffen.jsub.client.QueryResult;

/**
 * Custom cell renderer for query result!
 * So fancy!
 * Created with WindowBuilder.
 * @version 0.3
 * @author rattmuffen
 */
public class ResultListCellPanel extends JPanel implements ListCellRenderer<QueryResult>, MouseListener{

	private static final long serialVersionUID = 1L;

	public JPanel titlePanel;
	public JLabel titleLabel;
	public JLabel yearLabel;
	public JLabel ratingLabel;

	public String imdbURL = "";
	public JList<QueryResult> hitsList;

	DefaultListModel<QueryResult> listModel;
	private JPanel infoPanel;
	private JLabel hitsLabel;

	private File sourceFile;

	private SubPanel controller;

	/**
	 * Create the panel.
	 */
	public ResultListCellPanel(SubPanel parent) {
		controller = parent;

		setBorder(null);
		setOpaque(false);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		this.titlePanel = new JPanel();
		add(this.titlePanel);
		this.titlePanel.setName("infoPanel");
		this.titlePanel.setLayout(new BoxLayout(this.titlePanel, BoxLayout.Y_AXIS));

		this.titleLabel = new JLabel("Title");
		this.titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		this.titlePanel.add(this.titleLabel);
		this.titleLabel.setName("titleLabel");

		listModel = new DefaultListModel<QueryResult>();

		this.hitsList = new JList<QueryResult>();

		infoPanel = new JPanel();
		add(infoPanel);

		this.yearLabel = new JLabel("XXXX");
		infoPanel.add(yearLabel);
		this.yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.yearLabel.setName("yearLabel");

		this.ratingLabel = new JLabel("X.X");
		infoPanel.add(ratingLabel);
		this.ratingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		this.ratingLabel.setName("ratingLabel");

		hitsLabel = new JLabel("X hits.");
		infoPanel.add(hitsLabel);
		
		this.hitsList.addMouseListener(this);
		this.hitsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.hitsList.setModel(listModel);
		this.hitsList.setCellRenderer(new ListCellRenderer<QueryResult>() {

			@Override
			public Component getListCellRendererComponent(
					JList<? extends QueryResult> list, QueryResult value, int index,
					boolean isSelected, boolean cellHasFocus) {

				JPanel jp = new JPanel(new FlowLayout());

				jp.add(new JLabel((index+1) + 
						". Downloads: " + (String) value.get("SubDownloadsCnt") +
						". Rating: " + (String) value.get("SubRating") +
						". Bad: " + (String) value.get("SubBad")));
				

				if (isSelected)
					jp.setBackground(Color.gray.darker());

				return jp;
			}
		});
		
		
		this.hitsList.setVisibleRowCount(-1);
		this.hitsList.setName("list_1");
		this.hitsList.setBackground(getBackground());
		add(this.hitsList);
	}

	@Override
	public Component getListCellRendererComponent(
			JList<? extends QueryResult> list, QueryResult result, int index,
			boolean isSelected, boolean hasFocus) {

		
		switch (result.type) {

		case RESULT_MESSAGE:
			listModel.clear();
			
			this.sourceFile = result.sourceFile;

			if (result.get("data") instanceof Object[]) {
				Object[] resultArray = (Object[]) result.get("data");

				QueryResult firstHit = new QueryResult(resultArray[0]);

				titleLabel.setText((String) firstHit.get("MovieName"));
				ratingLabel.setText("Rating: " + (String)firstHit.get("MovieImdbRating"));
				yearLabel.setText("Released: " + (String)firstHit.get("MovieYear"));
				hitsLabel.setText("Synced results: " + resultArray.length);

				for (int i = 0; i < resultArray.length; i++) {
					QueryResult r = new QueryResult(resultArray[i]);
					listModel.addElement(r);
				}
			}

			hitsList.setModel(listModel);
			break;
		case ERROR_MESSAGE:
			listModel.clear();

			titleLabel.setText(result.message);
			ratingLabel.setText("");
			yearLabel.setText("");
			hitsLabel.setText("");

			break;
		}

		return this;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		Point p = new Point(e.getPoint().x, e.getPoint().y - (titlePanel.getHeight() + infoPanel.getHeight()));
		int index = hitsList.locationToIndex(p);
		
		if (index >= 0 && p.y > 0) {
			hitsList.setSelectedIndex(index);
			controller.downloadAndExtractSubArchive(sourceFile, 
					(String)hitsList.getModel().getElementAt(index).get("SubDownloadLink"),
					(String)hitsList.getModel().getElementAt(index).get("MovieName"));
			
			hitsList.repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}
	@Override
	public void mouseExited(MouseEvent e) {}
	@Override
	public void mousePressed(MouseEvent e) {}
	@Override
	public void mouseReleased(MouseEvent e) {}
}
