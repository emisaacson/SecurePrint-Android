package com.emikek.printbeacon.activities.managers;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Helper class for creating an alert message to the user.
 *
 */
public class AlertManager {
	public static void CreateAlert(Context context, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(text);
		
		AlertDialog notifyUser = builder.create();
		notifyUser.show();
	}
}
