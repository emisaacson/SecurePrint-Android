package com.emikek.printbeacon.serverinterface;

import com.emikek.printbeacon.R;
import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import com.emikek.printbeacon.persistance.LoginData;

public class ServerInterface {


	private LoginData loginData;
	private String baseUrl;
	private static AsyncHttpClient client = new AsyncHttpClient();
	
	public ServerInterface(Context context) {
		this.baseUrl = context.getString(R.string.base_api_url);
	}
	
	public ServerInterface(Context context, LoginData loginData) {
		this.loginData = loginData;
		this.baseUrl = context.getString(R.string.base_api_url);
	}
	
	public void SendAuthenticatedGetRetreiveJson(String path, PrintBeaconJsonHttpResponseHandler handler) {
		if (loginData == null) {
			throw new NullPointerException("You must send login data to make an authenticated request.");
		}
		
		String authenticatedPath = path + "?" + loginData.getLoginQueryString();
		
		SendGetRetreiveJson(authenticatedPath, handler);
	}
	
	public void SendAuthenticatedPostRetreiveJson(String path, RequestParams params, PrintBeaconJsonHttpResponseHandler handler) {
		if (loginData == null) {
			throw new NullPointerException("You must send login data to make an authenticated request");
		}
		
		String authenticatedPath = path + "?" + loginData.getLoginQueryString();
		
		SendPostRetreiveJson(authenticatedPath, params, handler);
	}
	
	public void SendGetRetreiveJson(String path, PrintBeaconJsonHttpResponseHandler handler) {
		
		client.get(this.baseUrl + path, handler);
	}
	
	public void SendPostRetreiveJson(String path, RequestParams params, PrintBeaconJsonHttpResponseHandler handler) {
		
		client.post(this.baseUrl + path, params, handler);
	}
	
	public static void CancelAllRequsts() {
		client.cancelAllRequests(true);
	}
}
