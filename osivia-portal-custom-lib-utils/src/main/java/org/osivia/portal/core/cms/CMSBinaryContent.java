package org.osivia.portal.core.cms;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.osivia.portal.api.cache.services.ICacheDataListener;



public class CMSBinaryContent implements ICacheDataListener {

	private static final long serialVersionUID = -3209402949942533453L;
	
	// 2.0.22 : to stream big files
	public static final Map<String, CMSBinaryContent> largeFile = new Hashtable<String, CMSBinaryContent>();
	
	

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}


	private String mimeType;
	
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/* Explicitly removed from cache : new cache has replaced old value */
	public void remove() {
		if( file != null)	{
			file.delete();
			file = null;
		}
		
	}
	
	/* Derefrenced files : ie session closed */
	protected void finalize() throws Throwable	{
		if( file != null)	{
			file.delete();
			file = null;
		}
		
	}
	

}
