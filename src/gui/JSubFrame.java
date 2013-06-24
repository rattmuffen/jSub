package gui;

import java.awt.Dimension;
import java.net.MalformedURLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import util.UserCredentials;

import client.OpenSubtitleClient;
import exceptions.NoCredentialFileFoundException;
import exceptions.UnautharizedException;

public class JSubFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	static final Dimension WINDOW_DIMENSION = new Dimension(350,200);
	static final String WINDOW_TITLE = "jSub - 0.3 indev - by rattmuffen 2013";
	static final String DEFAULT_CREDENTIAL_FILENAME = "credentials";

	OpenSubtitleClient client;

	public JSubFrame(String string) throws Exception {
		super(string);
		
		this.pack();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setVisible(true);

		try {
			System.out.println("Looking after credential file: " + DEFAULT_CREDENTIAL_FILENAME);

			UserCredentials credentials = new UserCredentials("", "");
			credentials.load(DEFAULT_CREDENTIAL_FILENAME);


			client = new OpenSubtitleClient(credentials);
			System.out.println("loaded client with credentials: " + client.uc.getUsername() + ", " + client.uc.getPassword());

			this.setViewByLoginSuccess(client.login());

		}	catch (UnautharizedException ue) {
			this.setViewByLoginSuccess(false);

		} catch (NoCredentialFileFoundException e) {
			this.setViewByLoginSuccess(false);


		} catch (Exception evt) {
			//TODO error handling yay!
			evt.printStackTrace();
			System.exit(ERROR);
		}



	}


	public void relogin(UserCredentials newCredentials) {
		try {
			client = new OpenSubtitleClient(newCredentials);
			System.out.println("loaded client with credentials: " + client.uc.getUsername() + ", " + client.uc.getPassword());
			this.setViewByLoginSuccess(client.login());

		}	catch (UnautharizedException ue) {
			this.setViewByLoginSuccess(false);
		} catch (Exception evt) {
			//TODO error handling yay!
			evt.printStackTrace();
			System.exit(ERROR);
		}
	}


	public void setViewByLoginSuccess(boolean success) {
		if (success) {
			System.out.println("login success!");
			this.setContentPane(new MainView(client,this));
			this.pack();
		} else {
			JOptionPane.showMessageDialog(this, "Error when logging in...", "jSub - Error", JOptionPane.ERROR_MESSAGE);
			this.setContentPane(new LoginView(this));
			this.pack();
		}
	}



	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			new JSubFrame(WINDOW_TITLE);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(ERROR);
		}
	}
}
