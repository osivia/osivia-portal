package org.osivia.portal.api.windows;

import java.util.Map;

import org.jboss.portal.core.model.portal.Window;

public class InternalWindow  implements PortalWindow	{
	 private Window internalReference;

	 public InternalWindow(Window internalReference) {
		super();
		this.internalReference = internalReference;
	}
	public Map<String, String> getProperties()	{
		return internalReference.getDeclaredProperties();
		 
	 }
    public String getProperty(String name)	{
   	 return internalReference.getDeclaredProperty( name);
   	 
    }
	 public void setProperty(String name, String value)	{
		 internalReference.setDeclaredProperty( name, value);
		 
	 }
	 
     public String getPageProperty(String name)	{
      	 return internalReference.getParent().getProperty( name);
   	 
     }
  
}