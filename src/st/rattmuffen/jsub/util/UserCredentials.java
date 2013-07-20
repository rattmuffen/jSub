package st.rattmuffen.jsub.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import st.rattmuffen.jsub.exceptions.NoCredentialFileFoundException;

/**
 * Class to store/load user credentials from properties file.
 * Uses jasypt for encryption and decryption of passwords.
 * @author rattmuffen
 * @version 0.3
 */
public class UserCredentials implements Serializable {


	private static final long serialVersionUID = 1L;

	private String username;
	private String password;

	private String passwordPropertyKey = "OpenSubtitlesPassword";
	private String usernamePropertyKey = "OpenSubtitlesUsername";
	
	private String encryptionPassword = "jsub";

	public static boolean existsValidCredentialFile(String dir) throws NoCredentialFileFoundException {
		File f = new File(dir);
		File[] files = f.listFiles();

		for (int i = 0; i < files.length; i++) {
			if (f.isFile() && f.getAbsolutePath().endsWith("credentials")) {
				return true;
			}
		}
		return false;
	}

	public UserCredentials(String u, String p) {
		username = u;
		password = p;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void load(String filename) throws NoCredentialFileFoundException, IOException {
		try {
			Properties file = new Properties();
			file.load(new FileInputStream(new File(filename)));

			String encryptedPropertyValue = file.getProperty(passwordPropertyKey);

			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(encryptionPassword);

			username = file.getProperty(usernamePropertyKey);
			password = encryptor.decrypt(encryptedPropertyValue);
		} catch (FileNotFoundException e) {
			throw new NoCredentialFileFoundException();
		}
	}


	public void save(String filename) throws IOException, NoCredentialFileFoundException {
		try {
			Properties file = new Properties();
			file.load(new FileInputStream(new File(filename)));

			StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
			encryptor.setPassword(encryptionPassword);

			String encryptedPassword = encryptor.encrypt(password);

			file.setProperty(passwordPropertyKey, encryptedPassword);
			file.setProperty(usernamePropertyKey, username);

			file.store(new FileOutputStream(new File(filename)),"- jSub Credentials file -");
		} catch (FileNotFoundException e) {
			throw new NoCredentialFileFoundException();
		}
	}

	public void createFileAndSave(String filename) {
		File f = new File(filename);
		
		try {
			boolean success = f.createNewFile();
			
			if (success) {
				save(filename);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NoCredentialFileFoundException e) {
			
			e.printStackTrace();
		}
	}
}
