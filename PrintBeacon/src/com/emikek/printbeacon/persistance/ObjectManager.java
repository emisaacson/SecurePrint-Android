package com.emikek.printbeacon.persistance;

import java.io.IOException;
import java.io.Serializable;

import android.content.Context;

/**
 * Get and sets objects to the device's persistence storage.
 *
 * @param <T> The type of object to get or set
 */
public class ObjectManager<T extends Serializable> {
	protected String OBJECT_KEY;
	protected Context context;
	
	public ObjectManager(Context context, String ObjKey) {
		this.context = context;
		OBJECT_KEY = ObjKey;
	}
	
	@SuppressWarnings("unchecked")
	public T GetObject() {
		ObjectPersistance Storage = new ObjectPersistance(OBJECT_KEY, context);
		T data = null;
		
		try {
			data = (T)Storage.ReadObject();
			return data;
		} catch (Exception e) {
			return null;
		}
	}
	
	public boolean Clear() {
		ObjectPersistance Storage = new ObjectPersistance(OBJECT_KEY, context);
		return Storage.DeleteObject();
	}
	
	public void StoreObject(T Obj) throws IOException {
		ObjectPersistance Storage = new ObjectPersistance(OBJECT_KEY, context);
		Storage.WriteObject(Obj);
	}
	
}
