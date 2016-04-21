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
package org.osivia.portal.api.profiler;


/**
 * The Interface IProfilerService.
 * 
 * The profiler is used to log performance datas (log4j implementation at this time)
 */
public interface IProfilerService {
	
	/**
	 * Log event.
	 *
	 * @param category the category
	 * @param name the name
	 * @param time the time
	 * @param error the error
	 */
	public void logEvent( String category, String name, long time, boolean error);
	
}
