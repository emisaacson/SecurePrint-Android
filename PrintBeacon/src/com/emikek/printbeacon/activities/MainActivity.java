package com.emikek.printbeacon.activities;

import com.emikek.printbeacon.R;
import com.emikek.printbeacon.activities.managers.MainActivityWorkflowManager;
import com.emikek.printbeacon.bluetooth.BluetoothUtils;
import com.emikek.printbeacon.serverinterface.ServerInterface;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


/**
 * Main activity class. This delegates almost all logic
 * to the MainActivityWorkflowManager class
 *
 */
public class MainActivity extends Activity {
    protected static final String TAG = "MainActivity";
	private MainActivityWorkflowManager manager;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Create the workflow manager and initialize the UI's
        // event handlers
        manager = new MainActivityWorkflowManager(this);
        manager.InitializeEventHandlers();

        // Application doesn't work too well if BT isn't on
		BluetoothUtils bt = new BluetoothUtils(this);
		bt.askUserToEnableBluetoothIfNeeded();
        
		manager.ResetToDefault();
    }

    @Override
    protected void onDestroy() {
    	ServerInterface.CancelAllRequsts();
    	super.onDestroy();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
    	if (menu != null) {
	    	manager.ConstructMainMenu(menu);
	    	manager.ResetToDefault();
    	}
    	return true;
    }
    
    @Override
    protected void onPause() {
    	manager.StopScanning();
    	super.onPause();
    }
    
    @Override
    protected void onResume() {
    	invalidateOptionsMenu();
    	super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.loginMenuItem) {
        	Intent intent = new Intent(this, LoginActivity.class);
        	startActivity(intent);
        }
        if (id == R.id.logoutMenuItem) {
        	Intent intent = new Intent(this, LogoutActivity.class);
        	startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
