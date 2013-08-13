package st.rattmuffen.jsub.client;

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

import st.rattmuffen.jsub.exceptions.UnautharizedException;
import st.rattmuffen.jsub.exceptions.UndefinedException;
import st.rattmuffen.jsub.gui.SubPanel;
import st.rattmuffen.jsub.util.FileUtils;
import st.rattmuffen.jsub.util.OpenSubtitlesHasher;
import st.rattmuffen.jsub.util.UserCredentials;

/**
 * Class for communicating with OpenSubtitle.org server.
 * Uses XmlRpcClient.
 * @version 0.3
 * @author rattmuffen
 */
public class OpenSubtitleClient extends XmlRpcClient implements SubtitleClient {

	XmlRpcClientConfigImpl clientConfig;
	
	public String USERNAME = "";
	public String PASSWORD = "";
	public static final String LANG = "en";
	public static final String USERAGENT = "OS Test User Agent";
	public static final String NULL_TOKEN = "NOT_LOGGED_IN";
	
	private boolean isLoggedIn;
	
	QueryResult loginInfo;
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
		QueryResult result =  new QueryResult(this.execute("LogIn", new String[] {USERNAME,PASSWORD,LANG,USERAGENT}));
		
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
	public QueryResult logout() throws XmlRpcException {
		isLoggedIn = false;
		return new QueryResult(this.execute("LogOut", new String[] {token}));
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
	private QueryResult search(String token, String hash, String lang, long size) throws XmlRpcException {
	      ArrayList<Object> params = new ArrayList<Object>();
	      ArrayList<Object> searches = new ArrayList<Object>();
	      
	      Map<String, Object> search = new HashMap<String, Object>();

	      search.put("sublanguageid", lang);
	      search.put("moviehash", hash);
	      search.put("moviebytesize", String.valueOf(size));
	      searches.add(search);
	      
	      params.add(token);
	      params.add(searches);
	    
	      return new QueryResult(this.execute("SearchSubtitles",params));
	}
	
	/**
	 * Get a QueryResult of a search.
	 * @param filename Filename of file to download subs for.
	 * @param language Language of wanted sub.
	 * @return QueryResult containing search result
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	public QueryResult performSearch(String filename, String language, boolean dlFirst, SubPanel controller) 
			throws IOException, XmlRpcException {
		if (this.isLoggedIn() && !this.getToken().equals(OpenSubtitleClient.NULL_TOKEN)) {
			movie = new File(filename);
			hash = OpenSubtitlesHasher.computeHash(movie); 

		    QueryResult searchResult = this.search(token, hash, language, movie.length());
		    if (dlFirst) {
		    	 if (searchResult.get("data") instanceof Object[]) {
					   Object[] resultArray = (Object[]) searchResult.get("data");
					   
					   if (resultArray.length>=0) {
						   HashMap<String, Object> firstHit = (HashMap<String, Object>) resultArray[0];
						   String dlURL = (String) firstHit.get("SubDownloadLink");
						   
						   controller.downloadAndExtractSubArchive(movie, dlURL, (String) firstHit.get("MovieName"));
					   }
		    	 }
		    }
		    return searchResult;
		} 
		return null;
	}
	
	/**
	 * Search and automatically download first hit for file.
	 * @param filename Filename of file to download subs for.
	 * @param language Language of wanted sub.
	 * @return true if succeeded, false if not
	 * @throws IOException
	 * @throws XmlRpcException
	 */
	public boolean searchAndDownloadSub(String filename, String language) 
			throws IOException,XmlRpcException   {
		if (this.isLoggedIn() && !this.getToken().equals(OpenSubtitleClient.NULL_TOKEN)) {
			
			movie = new File(filename);
			hash = OpenSubtitlesHasher.computeHash(movie); 
			
			System.out.println("Hash: " + hash);
			System.out.println("Size: " + movie.length() +" bytes.");
		    
		    QueryResult searchResult = this.search(token, hash, language, movie.length());
		    String dlURL = "";
		    
		    if (searchResult.get("data") instanceof Object[]) {
			   Object[] resultArray = (Object[]) searchResult.get("data");
			   
			   if (resultArray.length>=0) {
				   System.out.println("Found subtitle for this file!");
				   
				   HashMap<String, Object> firstHit = (HashMap<String, Object>) resultArray[0];
				   dlURL = (String) firstHit.get("SubDownloadLink");
								   
				   File gzFile = new File(FileUtils.getDir(movie) +  new File(dlURL).getName());
				   
				   System.out.println("Downloading...");
				   FileUtils.download(new URL(dlURL), gzFile);
	  
				   File outFile = new File(FileUtils.getDir(movie) + FileUtils.getNameWithoutExt(movie) + ".srt");
				   FileUtils.uncompress(gzFile, outFile);
				   System.out.println("Download completed!");
				  
				   gzFile.delete();
				   return true;
			   } else {
				   System.out.println("Found no subs for that release. Sorry!");
				   return false;
			   }
		   } else {
			   System.out.println("Found no subs in that language. Sorry!");
			   return false;
		   }
		}
		return false;
	}


	public QueryResult performSearch(String canonicalPath, String langCode, SubPanel controller) 
			throws IOException, XmlRpcException {
		return performSearch(canonicalPath, langCode, false, controller);
	}
	
}
