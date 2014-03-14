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

    private final StateString additionnalState;


    public StateString getAdditionnalState() {
        return additionnalState;
    }

    public WindowStateMarkerInfo(WindowState windowState, Mode mode, StateString contentState, StateString publicContentState, StateString additionnalState) {
        this.windowState = windowState;
        this.mode = mode;
        this.contentState = contentState;
        this.publicContentState = publicContentState;
        this.additionnalState = additionnalState;
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
