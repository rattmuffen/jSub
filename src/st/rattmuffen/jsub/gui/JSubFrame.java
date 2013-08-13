package st.rattmuffen.jsub.gui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import st.rattmuffen.jsub.JSub;
import st.rattmuffen.jsub.client.OpenSubtitleClient;
import st.rattmuffen.jsub.exceptions.NoCredentialFileFoundException;
import st.rattmuffen.jsub.exceptions.UnautharizedException;
import st.rattmuffen.jsub.util.UserCredentials;

/**
 * Frame that either displays the LoginPanel or the SubPanel. 
 * @version 0.3
 * @author rattmuffen
 */
public class JSubFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	static final Dimension WINDOW_DIMENSION = new Dimension(350,200);
	static final String WINDOW_TITLE = "jSub - 0.3 - rattmuffen 2013";

	OpenSubtitleClient client;
	
	public JSubFrame() throws Exception {
		super(WINDOW_TITLE);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		try {
			System.out.println("Looking after credential file: " + JSub.DEFAULT_CREDENTIAL_FILENAME);

			UserCredentials credentials = new UserCredentials("", "");
			credentials.load(JSub.DEFAULT_CREDENTIAL_FILENAME);
			
			client = new OpenSubtitleClient(credentials);

			this.setViewByLoginSuccess(client.login());
		}	catch (UnautharizedException ue) {
			ue.printStackTrace();
			this.setViewByLoginSuccess(false);

		} catch (NoCredentialFileFoundException e) {
			e.printStackTrace();
			this.setViewByLoginSuccess(false);
		} catch (Exception evt) {
			evt.printStackTrace();
			System.exit(ERROR);
		}
	}

	public void relogin(UserCredentials newCredentials) {
		try {
			client = new OpenSubtitleClient(newCredentials);
			this.setViewByLoginSuccess(client.login());

		}	catch (UnautharizedException ue) {
			this.setViewByLoginSuccess(false);
		} catch (Exception evt) {
			evt.printStackTrace();
			System.exit(ERROR);
		}
	}

	public void setViewByLoginSuccess(boolean success) {
		if (success) {
			this.setContentPane(new SubPanel(client,this));
			this.pack();
		} else {
			JOptionPane.showMessageDialog(this, "Error when logging in...", "jSub - Error", JOptionPane.ERROR_MESSAGE);
			this.setContentPane(new LoginPanel(this));
			this.pack();
		}
	}
}
