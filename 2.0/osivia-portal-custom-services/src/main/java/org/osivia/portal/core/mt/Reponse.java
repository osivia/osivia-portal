package org.osivia.portal.core.mt;

import org.jboss.portal.core.model.portal.Window;
import org.jboss.portal.core.model.portal.content.WindowRendition;
import org.jboss.portal.core.theme.PageRendition;

public class Reponse {
	Window window;
	WindowRendition rendition;
	public Reponse(Window window, WindowRendition rendition) {
		super();
		this.window = window;
		this.rendition = rendition;
	}
	public Window getWindow() {
		return window;
	}
	public void setWindow(Window window) {
		this.window = window;
	}
	public WindowRendition getRendition() {
		return rendition;
	}
	public void setRendition(WindowRendition rendition) {
		this.rendition = rendition;
	}

}
