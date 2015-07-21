package com.emikek.printbeacon.serverinterface;

import com.loopj.android.http.RequestParams;

import com.emikek.printbeacon.R;
import com.emikek.printbeacon.persistance.IdentityManager;
import com.emikek.printbeacon.persistance.LoginData;
import android.content.Context;

public class APIMethods {
	public final static String TAG = "APIMethods";
	
	public static void ReleaseJobs(Context context, String printer, PrintBeaconJsonHttpResponseHandler handler) {
		IdentityManager idManager = new IdentityManager(context);
		LoginData loginData = idManager.GetObject();
		
		ServerInterface si = new ServerInterface(context, loginData);
		
		RequestParams params = new RequestParams();
		params.put("printer", printer);
		
		si.SendAuthenticatedPostRetreiveJson(context.getString(R.string.release_jobs_path), params, handler);
	}
	
    public static void getJobs(Context context, PrintBeaconJsonHttpResponseHandler handler) {
    	
		IdentityManager idManager = new IdentityManager(context);
		LoginData loginData = idManager.GetObject();
		
		ServerInterface si = new ServerInterface(context, loginData);
		
		si.SendAuthenticatedGetRetreiveJson(context.getString(R.string.get_jobs_path), handler);

    }
    
    public static void GetBeaconMap(Context context, PrintBeaconJsonHttpResponseHandler handler) {
		IdentityManager idManager = new IdentityManager(context);
		LoginData loginData = idManager.GetObject();
		
		ServerInterface si = new ServerInterface(context, loginData);
		
		si.SendAuthenticatedGetRetreiveJson(context.getString(R.string.get_beacon_map), handler);
    }
}
