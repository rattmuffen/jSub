package exceptions;

import java.util.HashMap;

/**
 * Exception for bad username/pass when login
 * @author Magnus
 * @version 0.2.1
 */
public class UnautharizedException extends Exception {

	private static final long serialVersionUID = 1L;
		
	HashMap<String, Object> result;
	
	public UnautharizedException(HashMap<String, Object> result) {
		this.result = result;
	}
	
	@Override
	public void printStackTrace() {
		System.out.println("EXCEPTION: " + result);
	}

}
