package com.emikek.printbeacon.activities.managers;

import java.io.IOException;
import java.util.Iterator;

import com.emikek.printbeacon.R;
import com.emikek.printbeacon.activities.MainActivity;
import com.emikek.printbeacon.bluetooth.BluetoothLeScanner;
import com.emikek.printbeacon.bluetooth.BluetoothUtils;
import com.emikek.printbeacon.persistance.BeaconMap;
import com.emikek.printbeacon.persistance.IdentityManager;
import com.emikek.printbeacon.persistance.ObjectManager;
import com.emikek.printbeacon.serverinterface.APIMethods;
import com.emikek.printbeacon.serverinterface.PrintBeaconJsonHttpResponseHandler;
import com.emikek.printbeacon.serverinterface.ServerInterface;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.IBeaconDevice;
import uk.co.alt236.bluetoothlelib.util.IBeaconUtils;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class manages UI events on the main application screen
 *
 */
public class MainActivityWorkflowManager {
    public static final String TAG = "MainActivityWorkflowManager";
    
    /**
     * A reference to the activity this class is
     * managing events for
     */
    private final MainActivity mainActivity;
    
    /**
     * Scans for nearby bluetooth devices
     */
    private BluetoothLeScanner mScanner;
    
    /**
     * This is a mapping of all known beacons and
     * the printers they are close to. It's loaded
     * from the server once at application startup
     * then later as required.
     */
    private static BeaconMap beaconMapCache = null;
    
    /**
     * The most recent printer the user has walked up
     * to. If the print button is press, it will be
     * sent to this one.
     */
    private static String printer = null;
    
    public MainActivityWorkflowManager(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
    
    /**
     * Sets up handlers for start scanning, stop scanning, and print buttons.
     * 
     * Also initializes the BT scanner.
     */
    public void InitializeEventHandlers() {
        Button printButton = getPrintButton();
        Button startScanningButton = getStartScanningButton();
        Button stopScanningButton = getStopScanningButton();
        
        printButton.setOnClickListener(GeneratePrintCallback());
        startScanningButton.setOnClickListener(GenerateScanButtonCallback());
        stopScanningButton.setOnClickListener(GenerateStopScanningCallback());
        
        BluetoothUtils mBluetoothUtils = new BluetoothUtils(mainActivity);
        mScanner = new BluetoothLeScanner(GenerateScanCallback(), mBluetoothUtils);
        
    }
    
    /**
     * Stop any in-progress scan and reset the UI to the default state
     */
    public void StopScanning() {
        if (mScanner.isScanning()) {
            mScanner.scanLeDevice(-1, false); 
        }
        ResetToDefault();
    }
    
    /**
     * Reset the layout to the default state.
     */
    public void ResetToDefault() {
        ResetToDefault(null);
    }
    
    /**
     * Reset the layout to the default state and display a message
     * if the optionalMessage parameter is not null.
     * 
     * @param optionalMessage The message to display to the user.
     */
    public void ResetToDefault(String optionalMessage) {
        TextView statusLabel = getStatusLabel();
        Button printButton = getPrintButton();
        Button startScanningButton = getStartScanningButton();
        Button stopScanningButton = getStopScanningButton();
                
        printButton.setVisibility(View.GONE);
        startScanningButton.setVisibility(View.GONE);
        stopScanningButton.setVisibility(View.GONE);
        
        printer = null;
        
        IdentityManager idm = new IdentityManager(mainActivity.getApplication());
        if (!idm.IsLoggedIn()) {            
            statusLabel.setText((optionalMessage != null ? optionalMessage + "\n\n" : "") +
                    "Login to continue");
        }
        else {
            statusLabel.setText((optionalMessage != null ? optionalMessage + "\n\n" : "") +
                    "Go to the printer and tap the button to retrieve your documents.");
            startScanningButton.setVisibility(View.VISIBLE);
        }
    }
    
    /**
     * Generate the application menu based on the current application state.
     * 
     * @param menu A reference to the menu which will be modified.
     */
    public void ConstructMainMenu(Menu menu) {
        IdentityManager idm = new IdentityManager(mainActivity.getApplication());
        
        if (menu != null) {
            MenuItem loginItem = menu.findItem(R.id.loginMenuItem);
            MenuItem logoutItem = menu.findItem(R.id.logoutMenuItem);
            
            if (idm.IsLoggedIn() && !logoutItem.isVisible()) {
                loginItem.setVisible(false);
                logoutItem.setVisible(true);
            }
            else if (!idm.IsLoggedIn() && !loginItem.isVisible()) {
                loginItem.setVisible(true);
                logoutItem.setVisible(false);
            }
        }
    }
    
    /**
     * Set the UI up to begin retrieving jobs.
     */
    public void beginGetJobs() {
        Button printButton = getPrintButton();
        Button startScanningButton = getStartScanningButton();
        Button stopScanningButton = getStopScanningButton();
        
        printButton.setVisibility(View.GONE);
        startScanningButton.setVisibility(View.GONE);
        stopScanningButton.setVisibility(View.GONE);
    }
    
    /**
     * Set the UI up to start scanning, and actually start scanning
     * for beacons. It will abort the scan operation if the 
     * BeaconCache is not populated.
     */
    public void StartScanning() {

        TextView statusLabel = getStatusLabel();
        Button startScanningButton = getStartScanningButton();
        Button stopScanningButton = getStopScanningButton();
        
        statusLabel.setText("Scanning...");
        stopScanningButton.setVisibility(View.VISIBLE);
        startScanningButton.setVisibility(View.GONE);
        
        if (beaconMapCache == null) {
            AlertManager.CreateAlert(mainActivity, "We were unable to find the printers! Please try again.");
            StopScanning();
        }
        else {
            if (!mScanner.isScanning()) {
                BluetoothUtils bt = new BluetoothUtils(mainActivity);
                if (bt.isBluetoothOn()) {
                    mScanner.scanLeDevice(-1, true);                    
                }
                else {
                    AlertManager.CreateAlert(mainActivity, "Bluetooth must be enabled.");
                }
            }
        }
    }
    
    
    
    ////////////////////////////////////
    ///// Private helper methods ///////
    ////////////////////////////////////
    
    
    
    /**
     * Gets a reference to the status label on the main screen.
     * 
     * @return The status label
     */
    private TextView getStatusLabel() {
        TextView view = (TextView)mainActivity.findViewById(R.id.statusLabel);
        return view;
    }
    
    /**
     * Gets a reference to the print button on the main screen.
     * 
     * @return The print button
     */
    private Button getPrintButton() {
        Button printButton = (Button)mainActivity.findViewById(R.id.printButton);
        
        return printButton;
    }
    
    /**
     * Gets a reference to the "stop scanning" button on the main screen.
     * 
     * @return The stop scanning button
     */
    private Button getStopScanningButton() {
        return (Button)mainActivity.findViewById(R.id.stopScanningButton);
    }
    

    /**
     * Gets a reference to the "start scanning" button.
     * 
     * @return The start scanning button
     */
    private Button getStartScanningButton() {
        return (Button)mainActivity.findViewById(R.id.startScanningButton);
    }
    
    /**
     * Handles a response from the server that contains a JSON serialized list
     * of known beacons. It will cache the response in beaconMapCache and then
     * begin scanning for available beacons.
     * 
     * @return An HTTP handler object that can parse and process the beacon list
     */
    private PrintBeaconJsonHttpResponseHandler GenerateGetBeaconMapThenStartScanningHandler() {
        return new PrintBeaconJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, final JSONObject response) {
                BeaconMap map = new BeaconMap();
                
                class WhyJavaDoesJavaMakeMeDoThis implements Iterable<String> {
                    @SuppressWarnings("unchecked")
                    public Iterator<String> iterator() {
                        return response.keys();
                    }
                }
                for (String key : new WhyJavaDoesJavaMakeMeDoThis()) {
                    try {
                        map.SetPrinter(key, response.getString(key));
                    } catch (JSONException e) {
                        Log.e(TAG, "Bad response from server: " + e.getMessage());
                    }
                }
                
                ObjectManager<BeaconMap> beaconManager = new ObjectManager<BeaconMap>(mainActivity.getApplication(), "BEACON_OBJECT");
                try {
                    beaconManager.StoreObject(map);
                } catch (IOException e) {
                    Log.e(TAG, "Can't store beacons: " + e.getMessage());
                }
                
                beaconMapCache = map;
                
                StartScanning();
            }
        };
    }
    
    /**
     * Handles a response from the server that contains a json serialized list of jobs for the current user
     * that can be printed on the printer specified in the printer argument
     * 
     * @param _printer A printer name, typically returned from matching a beacon UUID to printer in the BeaconMapCache
     * @return An Http handler that parse and process the jobs list
     */
    private PrintBeaconJsonHttpResponseHandler generateGetJobsHandler(final String _printer) {
        return new PrintBeaconJsonHttpResponseHandler() {
            
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                int count = 0;
                TextView statusLabel = getStatusLabel();
                Button printButton = getPrintButton();
                
                if (!response.has("job_0")) {
                    statusLabel.setText("No jobs found.");
                    getStopScanningButton().setVisibility(View.GONE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            ResetToDefault();
                        }
                    }, 5000);
                }
                else {
                    statusLabel.setText("Jobs found:\n");
                    while (response.has("job_"+String.valueOf(count))) {
                        try {
                            statusLabel.append(response
                                    .getJSONObject("job_"+String.valueOf(count))
                                    .getJSONObject("job_name")
                                    .getString("_value0")+"\n"
                                    );
                        } catch (JSONException e) {
                            statusLabel.append("N/A\n");
                        }
                        count++;
                    }
                    printer = _printer;
                    printButton.setVisibility(View.VISIBLE);
                }
            }
        };
    }
    
    /**
     * Creates the callback on every scan of the bluetooth sensor.
     * 
     * The callback tries to get the UUID of the beacon and match it to
     * the list of known beacons in the BeaconMapCache. If a match is found,
     * it calls the server to get a list of jobs for the current user.
     * @return A handler for the bluetooth scan.
     */
    private BluetoothAdapter.LeScanCallback GenerateScanCallback() {
        return new BluetoothAdapter.LeScanCallback() {
            
             @Override
             public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
             
                 final BluetoothLeDevice deviceLe = new BluetoothLeDevice(device, rssi, scanRecord, System.currentTimeMillis());
                 
                 // This cannot run in a background thread.
                 mainActivity.runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         
                        if (IBeaconUtils.isThisAnIBeacon(deviceLe)) {
                            IBeaconDevice beacon = new IBeaconDevice(deviceLe);
                            String beaconUUID = beacon.getUUID();
                            if (beacon.getMajor() == 375 && beacon.getMinor() == 29 &&
                                    beacon.getRssi() > -53 && beaconMapCache.GetPrinter(beaconUUID) != null) {
                                
                                 mScanner.scanLeDevice(-1, false);
                                 beginGetJobs();
                                 APIMethods.getJobs(mainActivity, generateGetJobsHandler(beaconMapCache.GetPrinter(beaconUUID)));
                            }
                        }
                     }
                     
                 });
             }
        };
    }
    
    /**
     * Generates a click listener to handle the print button.
     * 
     * This implementation sends a request to the server to release all jobs in the
     * print queue to the printer discovered previously by scanning for beacons.
     * 
     * @return The click handler.
     */
    private OnClickListener GeneratePrintCallback() {
        
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                APIMethods.ReleaseJobs(mainActivity, printer, new PrintBeaconJsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        if (response.has("isSuccessful")) {
                            try {
                                if (response.getBoolean("isSuccessful")) {
                                    getStatusLabel().setText("Printed!");
                                    getPrintButton().setVisibility(View.GONE);
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            ResetToDefault();
                                        }
                                    }, 2000);
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }
                });
            }
        };
    }
    
    /**
     * Returns a click handler to handle the stop scanning button.
     * 
     * @return The click listener.
     */
    private OnClickListener GenerateStopScanningCallback() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                ServerInterface.CancelAllRequsts();
                StopScanning();
            }
        };
    }
    
    /**
     * Returns a click handler to handle the start scanning button.
     * @return The click handler.
     */
    private OnClickListener GenerateScanButtonCallback() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                APIMethods.GetBeaconMap(mainActivity, GenerateGetBeaconMapThenStartScanningHandler());
            }
        };
    }
}
