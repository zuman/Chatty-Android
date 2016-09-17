package com.zap.chatty;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

//2014-08-17
public class Tools{
	
	public static boolean isValidEmail(String email) {
		   boolean result = true;
		   try {
		      InternetAddress emailAddr = new InternetAddress(email);
		      emailAddr.validate();
		   } catch (AddressException ex) {
		      result = false;
		   }
		   return result;
		}

	public static boolean isValidUser(String user)
	{
		boolean b=false;
		String mat="[a-zA-Z0-9._-]{3,}";
		if(user.matches(mat))
			b=true;
		return b;
	}
}
