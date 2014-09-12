/*
 * (C) Copyright 2014 OSIVIA (http://www.osivia.com) 
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 */
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
