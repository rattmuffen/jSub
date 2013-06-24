package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import exceptions.NoCredentialFileFoundException;

/**
 * Class to store/load user credentials.
 * Stores in chararray, but uses regular String internally.
 * @author rattmuffen
 * @version 0.2.1
 */
public class UserCredentials implements Serializable {
	

	private static final long serialVersionUID = 1L;
	
	private String username;
	private String password;

	private byte[] usrname = {};
	private byte[] pssword = {};
	
	
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


	
	public void save(String filename) {
		try {
			File file = new File(filename);
			
			usrname = username.getBytes();
			pssword = password.getBytes();
			
			for (int i = 0; i < usrname.length; i++) {
				usrname[i] = (byte) (usrname[i]*2);
			}
			
			for (int i = 0; i < pssword.length; i++) {
				pssword[i] = (byte) (pssword[i]*2);
			}
			
	        ObjectOutputStream serialOut = new ObjectOutputStream(new FileOutputStream(file));
	        serialOut.writeObject(this);
	        
	        serialOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

	public void load(String filename) throws NoCredentialFileFoundException {
		try {
			File file = new File(filename);
			
			ObjectInputStream serialIn = new ObjectInputStream(new FileInputStream(file));

			UserCredentials uc = ((UserCredentials) serialIn.readObject());
			
			
			this.usrname =((UserCredentials) serialIn.readObject()).usrname;
			this.pssword =((UserCredentials) serialIn.readObject()).pssword;
			
			
			username = uc.username;
			password = uc.password;
			
			serialIn.close();
		} catch (java.io.EOFException eofe) {
			eofe.printStackTrace();
		} catch (Exception e) {
			throw new NoCredentialFileFoundException();
		}
	}
}
