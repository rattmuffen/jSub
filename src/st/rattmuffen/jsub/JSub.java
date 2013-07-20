package st.rattmuffen.jsub;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import st.rattmuffen.jsub.client.OpenSubtitleClient;
import st.rattmuffen.jsub.exceptions.NoCredentialFileFoundException;
import st.rattmuffen.jsub.gui.JSubFrame;
import st.rattmuffen.jsub.util.UserCredentials;

public class JSub extends JFrame {

	private static final long serialVersionUID = 1L;
	public static final String DEFAULT_CREDENTIAL_FILENAME = "credentials";
	public static final String DEFAULT_LANGUAGE = "eng";

	/**
	 * If no arguments then CLI! Otherwise GUI!
	 * @param args Arguments!
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
						new JSubFrame();
					} catch (Exception e) {
						System.err.println(e.getMessage());
						System.exit(ERROR);
					}
				}
			});
		} else {
			try {
				UserCredentials credentials = new UserCredentials("", "");
				credentials.load(DEFAULT_CREDENTIAL_FILENAME);

				OpenSubtitleClient client = new OpenSubtitleClient(credentials);
				boolean success = client.login();
				
				if (success) {		
					for (int i = 0; i < args.length; i++) {
						File f = new File(args[i]);

						if (f.exists()) {
							client.searchAndDownloadSub(f.getAbsolutePath(), DEFAULT_LANGUAGE);
						} else {
							System.out.println("Could not access " + f.getAbsolutePath() + ".");
						}
					}
				} else {
					throw new Exception("Could not log in!");
				}
			} catch (NoCredentialFileFoundException e) {
				System.out.println("Could not find a credentials file! Start the program without a parameter and input credentials.");
				System.exit(ERROR);
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(ERROR);
			}
		}
	}
}

