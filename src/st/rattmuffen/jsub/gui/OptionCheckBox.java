package st.rattmuffen.jsub.gui;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Had to create own checkbox class because of a stupid alignment bug!
 * So annoying!
 * @version 0.3
 * @author rattmuffen
 */
public class OptionCheckBox extends JPanel{

	private static final long serialVersionUID = 1L;
	
	public JCheckBox checkBox;

	public OptionCheckBox(String text) {
		super();
		
		FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
		fl.setHgap(1);
		fl.setVgap(1);
		
		setLayout(fl);
		
		this.add(new JLabel(text));
		
		checkBox = new JCheckBox("");
		this.add(checkBox);
	}

}
