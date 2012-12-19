package fr.toutatice.portail.api.windows;

import java.util.Map;

public interface PortalWindow {
	 public Map<String, String> getProperties();
     public String getProperty(String name);
	 public void setProperty(String name, String value);
	 public String getPageProperty(String name);
}
