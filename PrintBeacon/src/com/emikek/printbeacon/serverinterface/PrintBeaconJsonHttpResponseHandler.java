package com.emikek.printbeacon.serverinterface;

import org.apache.http.Header;
import org.json.JSONObject;
import android.util.Log;
import com.loopj.android.http.JsonHttpResponseHandler;

public class PrintBeaconJsonHttpResponseHandler extends JsonHttpResponseHandler {
	private static final String TAG = "PrintBeaconJsonHttpResponseHandler";

	@Override
	public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
	}
	
	@Override
	public void onFailure(int statusCode, org.apache.http.Header[] headers, java.lang.String responseString, java.lang.Throwable throwable) {
		Log.e(TAG, "Status Code: " + String.valueOf(statusCode) + "; Content: " + responseString);
	}

}
