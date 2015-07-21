package com.emikek.printbeacon.persistance;

import java.io.IOException;
import java.util.Date;

import android.content.Context;

/**
 * Manages persistence of the user identity object
 *
 */
public class IdentityManager extends ObjectManager<LoginData>{

	
	public IdentityManager(Context context) {
		super(context, "LOGIN_OBJECT");
	}
	
	public boolean IsLoggedIn() {
		LoginData login = this.GetObject();

		if (login == null) {
			return false;
		}
		return true;
	}
	
	public void StoreLogin(String Username, String Domain, String Token) throws IOException {
		LoginData login = new LoginData();
		login.UserName = Username;
		login.Domain = Domain;
		login.LoginDate = new Date();
		login.Token = Token;
		
		StoreObject(login);
	}
}
