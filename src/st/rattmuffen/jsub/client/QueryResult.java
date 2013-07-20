package st.rattmuffen.jsub.client;

import java.io.File;
import java.util.HashMap;

/**
 * Wrapper class for query result from OpenSubtitles.org API.
 * @version 0.3
 * @author rattmuffen
 */
public class QueryResult extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 1L;
	public File sourceFile;
	
	public static enum Result_Type {
		RESULT_MESSAGE,
		ERROR_MESSAGE
	};

	public Result_Type type = Result_Type.RESULT_MESSAGE;
	public String message = "";
	public boolean downloadFirst = false;
	
	public QueryResult(Object o) {
		if (o instanceof HashMap) {
			HashMap<String, Object> hm = (HashMap<String, Object>)o;
			
			this.putAll(hm);
		}
	}

}
