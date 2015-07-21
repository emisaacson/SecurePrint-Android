package com.emikek.printbeacon.activities;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import com.emikek.printbeacon.R;
import com.emikek.printbeacon.persistance.IdentityManager;
import com.emikek.printbeacon.persistance.LoginData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Logout is a webview instead of an API call. This doesn't
 * make any sense and should be changed. Beginner mistake.
 *
 */
public class LogoutActivity extends Activity {
	private WebView webView;
	private static final String TAG = "LogoutActivity";
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_logout);
		
		webView = (WebView) findViewById(R.id.logoutWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		
		// The server will create redirects to /logout/close or
		// /logout/clear to tell the client what to do. We intercept
		// those by overriding shouldOverrideUrlLoading
		webView.setWebViewClient(new WebViewClient() {
			
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				URL parsedUrl;
				try {
					parsedUrl = new URL(url);
				} catch (MalformedURLException e) {
					return false;
				}
				if (parsedUrl != null && parsedUrl.getPath().toLowerCase(Locale.ENGLISH).equals("/logout/close")) {
					finish();
					return true;
				}
				else if (parsedUrl != null && parsedUrl.getPath().toLowerCase(Locale.ENGLISH).equals("/logout/clear")) {
					IdentityManager idm = new IdentityManager(getApplication());
					if (!idm.Clear()) {
						Log.e(TAG, "Could not delete login.");
					}
					finish();
					return true;
				}
				
				return false;
			}
		});
		
		IdentityManager idm = new IdentityManager(getApplication());
		LoginData loginData = idm.GetObject();
		
		webView.loadUrl(getString(R.string.base_api_url) + getString(R.string.logout_path) + "?" + loginData.getLoginQueryString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.logout, menu);
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
