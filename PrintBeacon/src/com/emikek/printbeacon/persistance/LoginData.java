package com.emikek.printbeacon.persistance;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;

import android.util.Log;

public class LoginData implements Serializable {
	
	public Date LoginDate;
	public String UserName;
	public String Domain;
	public String Token;
	
	public final String TAG = "LOGIN_DATA";
	
	
	private static final long serialVersionUID = 3743010698463986618L;
	
	public String getLoginQueryString() {
		String safeUserName = "", safeDomain = "", safeToken = "";
		try {
			safeUserName = URLEncoder.encode(UserName, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		try {
			safeDomain   = URLEncoder.encode(Domain, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		try {
			safeToken    = URLEncoder.encode(Token, "utf-8");
		} catch (UnsupportedEncodingException e) {
			Log.d(TAG, e.getMessage());
		}
		
		return "domain="+safeDomain+"&username="+safeUserName+"&token="+safeToken;
	}
}
