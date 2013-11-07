package org.osivia.portal.core.pagemarker;

import org.jboss.portal.Mode;
import org.jboss.portal.WindowState;
import org.jboss.portal.portlet.StateString;

public class WindowStateMarkerInfo {
	
	   private final WindowState windowState;

	   /** . */
	   private final Mode mode;

	   /** . */
	   private final StateString contentState;

	   /** . */
	   private final StateString publicContentState;
	   
	   public WindowStateMarkerInfo(WindowState windowState, Mode mode, StateString contentState, StateString publicContentState)
	   {
	      this.windowState = windowState;
	      this.mode = mode;
	      this.contentState = contentState;
	      this.publicContentState = publicContentState;
	   }

	public WindowState getWindowState() {
		return windowState;
	}

	public Mode getMode() {
		return mode;
	}

	public StateString getContentState() {
		return contentState;
	}

	public StateString getPublicContentState() {
		return publicContentState;
	}



}
