package client;

import exceptions.UnautharizedException;
import exceptions.UndefinedException;
import gui.MainView;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import util.FileUtils;
import util.OpenSubtitlesHasher;
import util.UserCredentials;
import util.Utils;

/*
 * 0.2.1: Reads username and password from credentials-file instead of
 * storing it in the source code ;p
 * 
 * 
 */

/**
 * Class for communicating with OpenSubtitle.org server.
 * Uses XmlRpcClient.
 * @author rattmuffen
 * @version 0.2.1
 */


//TODO fixa ordentlig result-class?

public class OpenSubtitleClient extends XmlRpcClient {

	XmlRpcClientConfigImpl clientConfig;
	
	public String USERNAME = "";
	public String PASSWORD = "";
	public static final String LANG = "en";
	public static final String USERAGENT = "OS Test User Agent";
	public static final String NULL_TOKEN = "NOT_LOGGED_IN";
	
	private boolean isLoggedIn;
	
	
	
	HashMap<String,Object> loginInfo;
	String token;
	String hash;
	boolean wasLoginOk;
	File movie;
	
	
	public UserCredentials uc;
	
	/**
	 * Checks if client is logged in.
	 * @return True if logged in, false if not.
	 */
	public boolean isLoggedIn() {
		return isLoggedIn;
	}


	/**
	 * Constructor that loads credentials from file, and set ups clientconfig.
	 * @throws MalformedURLException
	 */
	public OpenSubtitleClient(UserCredentials uc) throws MalformedURLException {
		super();

		this.uc = uc;
		
		USERNAME = uc.getUsername();
		PASSWORD = uc.getPassword();
		
		isLoggedIn = false;
		token = NULL_TOKEN;
		
	    clientConfig = new XmlRpcClientConfigImpl();
	    clientConfig.setServerURL(new URL("http://api.opensubtitles.org/xml-rpc"));
	    
	    this.setConfig(clientConfig);
	}
	
	/*public HashMap<String,Object> execute(String ) {
		
	}*/
	
	/**
	 * Getter for token.
	 */
	public String getToken() {
		return token;
	}
	
	
	/**
	 * Try to login.
	 * @return Result of login. 
	 * @throws XmlRpcException
	 * @throws UnautharizedException 
	 */
	public boolean login() throws XmlRpcException, UnautharizedException, UndefinedException {
		HashMap<String, Object> result =  (HashMap<String, Object>) this.execute("LogIn", new String[] {USERNAME,PASSWORD,LANG,USERAGENT});
		
		
		if (result.get("status").equals("200 OK")) {
			isLoggedIn = true;
			token = (String) result.get("token");
			
			return true;
		} else if (result.get("status").equals("401 Unauthorized")) {
			throw new UnautharizedException(result);
		} else {
			return false;
		}
	}
	
	/**
	 * Try to logout.
	 * @return Result of logout.
	 * @throws XmlRpcException
	 */
	public HashMap<String, Object> logout() throws XmlRpcException {
		isLoggedIn = false;
		
		return (HashMap<String, Object>) this.execute("LogOut", new String[] {token});
	}
	
	/**
	 * Perform a search using the given parameters.
	 * @param token Token
	 * @param hash Hash-value of file.
	 * @param lang Language to search for.
	 * @param size Size of file
	 * @return Result of the search.
	 * @throws XmlRpcException
	 */
	public  HashMap<String,Object> search(String token, String hash, String lang, long size) throws XmlRpcException {
	      ArrayList<Object> params = new ArrayList<Object>();
	      ArrayList<Object> searches = new ArrayList<Object>();
	      
	      Map<String, Object> search = new HashMap<String, Object>();

	      search.put("sublanguageid", lang);
	      search.put("moviehash", hash);
	      search.put("moviebytesize", String.valueOf(size));
	      searches.add(search);
	      
	      params.add(token);
	      params.add(searches);
	    
	      return (HashMap<String,Object>) this.execute("SearchSubtitles",params);
	}
	
	
	
	//TODO detta kanske skall g�ras i en egen Thread eller?
	public boolean downloadSub(String filename, String language, MainView gui) throws IOException,XmlRpcException   {
		if (this.isLoggedIn() && !this.getToken().equals(OpenSubtitleClient.NULL_TOKEN)) {
			
			movie = new File(filename);
			hash = OpenSubtitlesHasher.computeHash(movie); 
			
			gui.addTextToMessageArea("Hash: " + hash, true);
			gui.addTextToMessageArea("Size: " + movie.length() +" bytes.", true);
		    
		    HashMap<String,Object> searchResult = this.search(token, hash, language, movie.length());
		    String dlURL = "";
		    
		    if (searchResult.get("data") instanceof Object[]) {
			   Object[] resultArray = (Object[]) searchResult.get("data");
			   
			   if (resultArray.length>=0) {
				   gui.addTextToMessageArea("!Found subtitle for this file!", true);
				   
				   HashMap<String, Object> firstHit = (HashMap<String, Object>) resultArray[0];
				   dlURL = (String) firstHit.get("SubDownloadLink");
				   
				   String test = (String) firstHit.get("IDMovieImdb");
				   System.out.println("http://www.imdb.com/title/tt" + test );
				   
				   File gzFile = new File(FileUtils.getDir(movie) +  new File(dlURL).getName());
				   
				   gui.addTextToMessageArea("Downloading...", true);
				   FileUtils.download(new URL(dlURL), gzFile);
	  

				   File outFile = new File(FileUtils.getDir(movie) + FileUtils.getNameWithoutExt(movie) + ".srt");
				   FileUtils.uncompress(gzFile, outFile);
				   gui.addTextToMessageArea("Download completed!", true);
				  
				   gzFile.delete();
				   
				   
				   gui.addTextToMessageArea("IMDB link: <a href=\"http://www.imdb.com/title/tt" + test + "\"> CLICK HERE LOL</a>",true);
				   return true;
			   } else {
				   gui.addTextToMessageArea("Found no subs for that release. Sorry!", true);
				   gui.addTextToMessageArea("<a href=\"http://subscene.com/s.aspx?q=" + Utils.getHTMLCompliantString(FileUtils.getNameWithoutExt(movie)) + "\">SubScene.com search</a>",true);
				   return false;
			   }
		   } else {
			   gui.addTextToMessageArea("Found no subs in that language. Sorry!", true);
			   gui.addTextToMessageArea("<a href=\"http://subscene.com/s.aspx?q=" + Utils.getHTMLCompliantString(FileUtils.getNameWithoutExt(movie)) + "\">SubScene.com search</a>",true);
			   return false;
		   }
		}
		return false;
	}
	
}
