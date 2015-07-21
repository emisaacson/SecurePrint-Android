package com.emikek.printbeacon.persistance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;

import android.content.Context;

/**
 * Reads and writes objects to the device's persistent
 * storage.
 *
 */
class ObjectPersistance {
	private Context context;
	private String ObjectFileKey;
	
	public ObjectPersistance(String ObjectFileKey, Context context) {
		this.ObjectFileKey = ObjectFileKey;
		this.context = context;
	}
	
	public boolean DeleteObject() {
		return context.deleteFile(ObjectFileKey);
	}
	
	public void WriteObject(Serializable Obj) throws IOException {
		
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = context.openFileOutput(ObjectFileKey, Context.MODE_PRIVATE);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(Obj);
		} finally {
			if (oos != null){
				oos.close();
			}
			if (fos != null) {				
				fos.close();
			}
		}
	}
	
	public Object ReadObject() throws OptionalDataException, ClassNotFoundException, IOException {
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = context.openFileInput(ObjectFileKey);
			ois = new ObjectInputStream(fis);
			return ois.readObject();
		}
		finally {
			if (ois != null) {
				ois.close();
			}
			if (fis != null) {
				fis.close();
			}
		}
	}
}
