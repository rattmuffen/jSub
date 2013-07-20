package st.rattmuffen.jsub.client;

import org.apache.xmlrpc.XmlRpcException;

import st.rattmuffen.jsub.exceptions.UnautharizedException;
import st.rattmuffen.jsub.exceptions.UndefinedException;

/**
 * Interface for subtitle clients.
 * Not really doing anything at the moment since only one type of client exists.
 * @version 0.3
 * @author rattmuffen
 */
public interface SubtitleClient {

	public boolean login() throws XmlRpcException, UnautharizedException, UndefinedException;
	
	
}
