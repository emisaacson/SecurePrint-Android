package com.emikek.printbeacon.persistance;

import java.io.Serializable;
import java.util.HashMap;


/**
 * Stores a map of Beacon IDs to printers
 */
public class BeaconMap implements Serializable {
	
	private static final long serialVersionUID = 2213735659371347291L;

	/**
	 * The current beacon map.
	 */
	private HashMap<String, String> map = new HashMap<String, String>();
	
	public final String TAG = "BEACON_OBJECT";

	/**
	 * Retrieves a printer near to the provided beacon ID, if it 
	 * exists, otherwise null.
	 * 
	 * @param Beacon The beacon's unique identifier
	 * @return The name of the printer, or null
	 */
	public String GetPrinter(String Beacon) {
		if (map != null && Beacon != null && map.containsKey(Beacon)) {
			return map.get(Beacon);
		}
		
		return null;
	}
	
	/**
	 * Add a new Beacon / Printer pair to the mpping
	 * 
	 * @param Beacon The beacon
	 * @param Printer The printer
	 */
	public void SetPrinter(String Beacon, String Printer) {
		map.put(Beacon, Printer);
	}
}
