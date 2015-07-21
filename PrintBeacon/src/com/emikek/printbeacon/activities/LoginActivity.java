package com.emikek.printbeacon.activities;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;

import com.emikek.printbeacon.R;
import com.emikek.printbeacon.persistance.IdentityManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Login is a web view and everything happens on the server.
 * 
 * This doesn't make that much sense and it would be better to
 * have it as a client side form making an API call.
 *
 */
public class LoginActivity extends Activity {

	private WebView webView;
	private static final String TAG = "LoginActivity";
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		webView = (WebView) findViewById(R.id.loginWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		
		// We intercent normal operation if there's a redirect to
		// /login/close. In that case, the activity exists
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				URL parsedUrl;
				try {
					parsedUrl = new URL(url);
				} catch (MalformedURLException e) {
					return false;
				}
				if (parsedUrl != null && parsedUrl.getPath().toLowerCase(Locale.ENGLISH).equals("/login/close")) {
					String queryString = parsedUrl.getQuery();
					HashMap<String, String> QueryStringParsed = new HashMap<String, String>();
					String[] QueryStringSplit = queryString.split("&");
					if (QueryStringSplit != null) {
						for (int i = 0; i < QueryStringSplit.length; i++) {
							String[] KV = QueryStringSplit[i].split("=");
							if (KV != null && KV.length == 2) {
								QueryStringParsed.put(KV[0], KV[1]);
							}
						}
					}
					if (!QueryStringParsed.containsKey("Username") || !QueryStringParsed.containsKey("Domain") || !QueryStringParsed.containsKey("Token") ||
						 QueryStringParsed.get("Username") == null || QueryStringParsed.get("Username").isEmpty() ||
						 QueryStringParsed.get("Domain") == null ||
						 QueryStringParsed.get("Token") == null || QueryStringParsed.get("Token").isEmpty()) {
						Log.e(TAG, "Could not get login from query string.");
					}
					IdentityManager idm = new IdentityManager(getApplication());
					try {
						idm.StoreLogin(QueryStringParsed.get("Username"), QueryStringParsed.get("Domain"), QueryStringParsed.get("Token"));
					} catch (IOException e) {
						Log.e(TAG, "Could not store login.", e);
					}
					finish();
					return true;
				}
				
				return false;
			}
		});
		
		// Get the device ID so we can associate it with the current user.
		TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(TELEPHONY_SERVICE);
		String DeviceID = tm.getDeviceId();
		try {
			DeviceID = URLEncoder.encode(DeviceID, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "Hell froze over", e);
		}
		
		webView.loadUrl(getString(R.string.base_api_url) + getString(R.string.login_path) + "?device_id=" + DeviceID);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
