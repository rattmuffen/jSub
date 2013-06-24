package client;

import org.apache.xmlrpc.XmlRpcException;

import exceptions.UnautharizedException;
import exceptions.UndefinedException;

public interface SubtitleClient {

	public boolean login() throws XmlRpcException, UnautharizedException, UndefinedException;
	
	
}
